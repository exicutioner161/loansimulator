package loansim;

import exilib.Utils;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Simulator for federal-style student loans that differentiates subsidized and
 * unsubsidized portions and supports semester disbursements.
 */
public class StudentLoanSimulator extends LoanSimulator {
    // TODO: SIMULATION IS STILL NOT ALIGNED WITH STUDENTAID LOAN SIMULATOR
    private static final int MONTHS_IN_UNI = 48;
    private static final int POSTGRAD_SUB_GRACE_MONTHS = 6;
    private static final int YEARS_IN_UNI = 4;
    private static final int SEMESTERS_IN_YEAR = 2;
    private static final int MONTHS_IN_SEMESTER = 6;
    private static final int MAX_REPAYMENT_TERM_MONTHS = 120;
    private final Scanner input;
    private LocalDate firstDisbursementDate;
    private LocalDate[] disbursementDates;
    private Set<LocalDate> disbursementDateSet;
    private LocalDate repaymentStartDate;
    private int paymentDayOfMonth;
    private double totalPaid;
    private double annualInterestRate;
    private double semesterSubOrig;
    private double yearlySubOrig;
    private double semesterUnsubOrig;
    private double yearlyUnsubOrig;
    private double origTotalLoanAmount;
    private double principalSubOwed;
    private double principalUnsubOwed;
    private double principalTotalOwed;
    private double accruedUnsubInterest;
    private double accruedSubInterest;
    private double totalInterest;
    private double totalInterestPaid;
    private double schoolMonthlyPayment;
    private double postgradMonthlyPayment;
    private int month;
    private boolean capitalizedUnsub;

    /** Generate semester disbursement dates and compute repayment start date. */
    private void generateDisbursementSchedule(LocalDate firstDate) {
        int totalSemesters = YEARS_IN_UNI * SEMESTERS_IN_YEAR;
        disbursementDates = new LocalDate[totalSemesters];
        disbursementDateSet = new HashSet<>();
        for (int i = 0; i < totalSemesters; i++) {
            LocalDate d = firstDate.plusMonths((long) i * MONTHS_IN_SEMESTER);
            disbursementDates[i] = d;
            disbursementDateSet.add(d);
        }
        LocalDate graduationDate = firstDate.plusMonths(MONTHS_IN_UNI);
        repaymentStartDate = graduationDate.plusMonths(POSTGRAD_SUB_GRACE_MONTHS);
    }

    /**
     * Create a student loan simulator that reads user input from {@code input}.
     *
     * @param input Scanner instance used to prompt the user
     */
    public StudentLoanSimulator(Scanner input) {
        resetState();
        this.input = input;
    }

    /** Disburse semester funds if the date matches a scheduled disbursement. */
    private void processDisbursement(LocalDate date) {
        if (disbursementDateSet != null && disbursementDateSet.contains(date)) {
            principalSubOwed += semesterSubOrig;
            principalUnsubOwed += semesterUnsubOrig;
            principalTotalOwed += semesterSubOrig + semesterUnsubOrig;
        }
    }

    /**
     * Capitalize accrued unsubsidized interest on the exact repayment start date.
     */
    private void maybeCapitalizeUnsub(LocalDate date) {
        if (!capitalizedUnsub && repaymentStartDate != null && date.equals(repaymentStartDate)) {
            if (accruedUnsubInterest > 0) {
                principalUnsubOwed += accruedUnsubInterest;
                accruedUnsubInterest = 0;
                principalTotalOwed = principalSubOwed + principalUnsubOwed;
                totalInterest = accruedSubInterest + accruedUnsubInterest;
            }
            capitalizedUnsub = true;
        }
    }

    /** Accrue interest for a single day using the correct per-year day count. */
    private void accrueForDay(LocalDate date) {
        double dailyRate = annualInterestRate / (isLeapYear(date.getYear()) ? 366.0 : 365.0);
        if (principalUnsubOwed > 0) {
            accrueUnsubDaily(dailyRate);
        }
        if (!date.isBefore(repaymentStartDate) && principalSubOwed > 0) {
            accrueSubDaily(dailyRate);
        }
        totalInterest = accruedSubInterest + accruedUnsubInterest;
    }

    /**
     * Apply a monthly payment: pay interest first then principal proportionally.
     */
    private void applyMonthlyPayment(double payment) {
        double amountTowardsPrincipal = makeInterestPayment(payment);
        if (amountTowardsPrincipal > 0) {
            double principalSubPortion = principalTotalOwed > 0 ? principalSubOwed / principalTotalOwed : 0;
            double principalUnsubPortion = 1.0 - principalSubPortion;
            payOffPrincipalAmount(amountTowardsPrincipal, principalSubPortion, principalUnsubPortion);
        }
    }

    /**
     * Return true if the given year is a leap year.
     */
    private static boolean isLeapYear(int yr) { return (yr % 4 == 0 && (yr % 100 != 0 || yr % 400 == 0)); }

    /**
     * Accrue unsubsidized interest for a single day using the provided daily rate.
     */
    private void accrueUnsubDaily(double dailyRate) {
        double dailyUnsub = dailyRate * principalUnsubOwed;
        accruedUnsubInterest += dailyUnsub;
    }

    /**
     * Accrue subsidized interest for a single day using the provided daily rate.
     */
    private void accrueSubDaily(double dailyRate) {
        double dailySub = dailyRate * principalSubOwed;
        accruedSubInterest += dailySub;
    }

    /**
     * Apply a payment that is smaller than the total outstanding interest. The
     * payment is split proportionally between accrued subsidized and unsubsidized
     * interest, each accrued field is reduced, and the {@code totalInterestPaid} is
     * increased by the payment amount. No amount remains to reduce principal when
     * this method returns.
     *
     * @param payment        monthly payment amount
     * @param interestBefore total interest outstanding before the payment
     * @return {@code 0.0} because the full payment went to interest
     */
    private double paymentLessThanInterest(double payment, double interestBefore) {
        double subPortion = accruedSubInterest / interestBefore;
        double unsubPortion = accruedUnsubInterest / interestBefore;
        double subPaid = payment * subPortion;
        double unsubPaid = payment * unsubPortion;
        accruedSubInterest = Math.max(0, accruedSubInterest - subPaid);
        accruedUnsubInterest = Math.max(0, accruedUnsubInterest - unsubPaid);
        totalInterestPaid = totalInterestPaid + payment;
        totalInterest = accruedSubInterest + accruedUnsubInterest;
        return 0.0;
    }

    /**
     * Apply a payment that is larger than the total outstanding interest. The
     * outstanding interest is fully paid (added to {@code totalInterestPaid}) and
     * the accrued interest fields are cleared. The remaining amount after covering
     * interest is returned so it can be applied to principal.
     *
     * @param payment        monthly payment amount
     * @param interestBefore total interest outstanding before the payment
     * @return amount remaining after paying interest, to be applied to principal
     */
    private double paymentGreaterThanInterest(double payment, double interestBefore) {
        totalInterestPaid = totalInterestPaid + interestBefore;
        double extraAmount = payment - interestBefore;
        accruedSubInterest = 0;
        accruedUnsubInterest = 0;
        totalInterest = 0;
        return extraAmount;
    }

    /**
     * Apply the provided payment to outstanding accrued interest first.
     *
     * @param payment monthly payment amount
     * @return amount remaining after interest that can be applied to principal
     */
    private double makeInterestPayment(double payment) {
        double interestBefore = totalInterest;
        if (interestBefore <= 0) {
            return payment;
        }
        if (payment >= interestBefore) {
            return paymentGreaterThanInterest(payment, interestBefore);
        } else {
            return paymentLessThanInterest(payment, interestBefore);
        }
    }

    /**
     * Reduce principal balances by applying the given payment across the two loan
     * components proportionally to their principal amounts.
     *
     * @param amountTowardsPrincipal amount available to reduce principal
     * @param subPortion             proportion of principal payment for subsidized
     *                               loans
     * @param unsubPortion           proportion of principal payment for
     *                               unsubsidized loans
     */
    private void payOffPrincipalAmount(double amountTowardsPrincipal, double subPortion, double unsubPortion) {
        double subPayment = amountTowardsPrincipal * subPortion;
        double unsubPayment = amountTowardsPrincipal * unsubPortion;
        principalSubOwed = principalSubOwed - subPayment;
        principalUnsubOwed = principalUnsubOwed - unsubPayment;
        if (principalSubOwed < 0) {
            principalSubOwed = 0;
        }
        if (principalUnsubOwed < 0) {
            principalUnsubOwed = 0;
        }
        principalTotalOwed = principalSubOwed + principalUnsubOwed;
    }

    /**
     * The main simulation loop: disburse funds, apply payments (interest first,
     * then principal), accrue interest, and repeat until paid or term limit.
     */
    private void simulate() {
        LocalDate endDate = firstDisbursementDate
                .plusMonths((long) MONTHS_IN_UNI + POSTGRAD_SUB_GRACE_MONTHS + MAX_REPAYMENT_TERM_MONTHS);
        LocalDate currentDate = firstDisbursementDate;
        while (!currentDate.isAfter(endDate)) {
            processDisbursement(currentDate);
            maybeCapitalizeUnsub(currentDate);
            accrueForDay(currentDate);
            boolean finished = applyPaymentIfScheduled(currentDate);
            if (finished) {
                month = (int) ChronoUnit.MONTHS.between(firstDisbursementDate, currentDate) + 1;
                return;
            }
            currentDate = currentDate.plusDays(1);
        }
        System.out.println("Unable to repay within repayment term!");
    }

    /**
     * Apply a scheduled payment on the given date if it matches the configured pay
     * day.
     */
    private boolean applyPaymentIfScheduled(LocalDate date) {
        int scheduledPayDay = Math.min(paymentDayOfMonth, date.lengthOfMonth());
        if (date.getDayOfMonth() != scheduledPayDay) {
            return false;
        }
        double payment = date.isBefore(repaymentStartDate) ? schoolMonthlyPayment : postgradMonthlyPayment;
        double outstanding = totalInterest + principalTotalOwed;
        if (outstanding <= 0) {
            return true;
        }
        double paymentToUse = Math.min(payment, outstanding);
        applyMonthlyPayment(paymentToUse);
        totalPaid += paymentToUse;
        return principalTotalOwed <= 0;
    }

    /** Reset simulator state before running a new simulation. */
    @Override
    final void resetState() {
        accruedUnsubInterest = 0;
        accruedSubInterest = 0;
        totalInterestPaid = 0;
        principalSubOwed = 0;
        principalUnsubOwed = 0;
        principalTotalOwed = 0;
        month = 0;
        capitalizedUnsub = false;
        firstDisbursementDate = null;
        disbursementDates = null;
        disbursementDateSet = null;
        repaymentStartDate = null;
        paymentDayOfMonth = 1;
        totalPaid = 0;
    }

    /**
     * Collect user inputs required by the simulation (semester amounts, rates, and
     * payment levels).
     */
    @Override
    final void handleInput() {
        semesterSubOrig = Utils.takeUserDoubleInput(input, "Subsidized loan amount per semester: ");
        semesterUnsubOrig = Utils.takeUserDoubleInput(input, "Unsubsidized loan amount per semester: ");
        yearlySubOrig = semesterSubOrig * SEMESTERS_IN_YEAR;
        yearlyUnsubOrig = semesterUnsubOrig * SEMESTERS_IN_YEAR;
        origTotalLoanAmount = (yearlySubOrig + yearlyUnsubOrig) * YEARS_IN_UNI;
        annualInterestRate = Utils.takeUserDoubleInput(input, "Annual interest rate (as a percentage): ") / 100.0;
        schoolMonthlyPayment = Utils.takeUserDoubleInput(input, "Monthly payment while in school: ");
        postgradMonthlyPayment = Utils.takeUserDoubleInput(input, "Monthly payment after graduation: ");
        // Optional: first disbursement date
        System.out.print("First disbursement date (YYYY-MM-DD) [default today]: ");
        String dateIn = input.nextLine().trim();
        if (dateIn.isEmpty()) {
            firstDisbursementDate = LocalDate.now();
        } else {
            try {
                firstDisbursementDate = LocalDate.parse(dateIn);
            } catch (DateTimeParseException _) {
                System.out.println("Invalid date format; using today as first disbursement date.");
                firstDisbursementDate = LocalDate.now();
            }
        }
        generateDisbursementSchedule(firstDisbursementDate);
        // Payment day-of-month (optional)
        int defaultPayDay = repaymentStartDate.getDayOfMonth();
        System.out.printf("Payment day of month (1-28) [default %d]: ", defaultPayDay);
        String payDayIn = input.nextLine().trim();
        if (payDayIn.isEmpty()) {
            paymentDayOfMonth = defaultPayDay;
        } else {
            try {
                int d = Integer.parseInt(payDayIn);
                paymentDayOfMonth = Math.clamp(d, 1, 28);
            } catch (NumberFormatException _) {
                paymentDayOfMonth = defaultPayDay;
            }
        }
    }

    /** Run the simulation and print a summary of results. */
    @Override
    final void runSimulation() {
        simulate();
        totalInterestPaid = Utils.roundTwoDecimals(totalInterestPaid);
        totalPaid = Utils.roundTwoDecimals(totalPaid);
        String message = "%nTime elapsed: %d months or %.2f years to pay off your $%.2f loan.%n"
                + "Yearly loan amount: $%.2f%n" + "Interest rate: %.3f%%%n" + "Total interest paid: $%.2f%n"
                + "Total amount paid: $%.2f%n";
        System.out.printf(message, month, Utils.toYears(month), origTotalLoanAmount, yearlySubOrig + yearlyUnsubOrig,
                annualInterestRate * 100, totalInterestPaid, totalPaid);
    }

    /** @return total interest paid computed by the simulator */
    @Override
    public final double getTotalInterestPaid() { return totalInterestPaid; }

    /** @return original total loan amount used by the simulation */
    @Override
    public final double getOriginalLoanAmount() { return origTotalLoanAmount; }
}