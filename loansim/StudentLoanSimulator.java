package loansim;

import exilib.Utils;
import java.util.Scanner;

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
    private double annualInterestRate;
    private double monthlyInterestRate;
    private double semesterSubOrig;
    private double yearlySubOrig;
    private double semesterUnsubOrig;
    private double yearlyUnsubOrig;
    private double origTotalLoanAmount;
    private double principalSubOwed;
    private double principalUnsubOwed;
    private double principalTotalOwed;
    private double monthlyUnsubInterest;
    private double monthlySubInterest;
    private double accruedUnsubInterest;
    private double accruedSubInterest;
    private double totalUnsubInterest;
    private double totalSubInterest;
    private double totalInterest;
    private double totalInterestPaid;
    private double schoolMonthlyPayment;
    private double postgradMonthlyPayment;
    private int month;
    private boolean capitalizedUnsub;

    /**
     * Create a student loan simulator that reads user input from {@code input}.
     *
     * @param input Scanner instance used to prompt the user
     */
    public StudentLoanSimulator(Scanner input) {
        resetState();
        this.input = input;
    }

    /**
     * Accrue interest on subsidized principal after the grace period following
     * graduation. Subsidized loans generally do not accrue while the student is in
     * school or during the immediate postgrad grace period.
     */
    private void accrueSubPostgradInterest() {
        if (month > MONTHS_IN_UNI + POSTGRAD_SUB_GRACE_MONTHS) {
            monthlySubInterest = monthlyInterestRate * principalSubOwed;
            accruedSubInterest = accruedSubInterest + monthlySubInterest;
            totalSubInterest = totalSubInterest + monthlySubInterest;
        }
    }

    /** Accrue interest on unsubsidized principal every month. */
    private void accrueUnsubInterest() {
        monthlyUnsubInterest = monthlyInterestRate * principalUnsubOwed;
        accruedUnsubInterest = accruedUnsubInterest + monthlyUnsubInterest;
        totalUnsubInterest = totalUnsubInterest + monthlyUnsubInterest;
    }

    /** Update aggregated interest totals used for payment allocation. */
    private void accrueInterest() {
        accrueSubPostgradInterest();
        accrueUnsubInterest();
        totalInterest = accruedSubInterest + accruedUnsubInterest;
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
     * Disburse semester loan amounts at the beginning of each semester while in
     * school.
     */
    private void disburseSemesterLoanIfNeeded() {
        if (month <= MONTHS_IN_UNI && (month - 1) % MONTHS_IN_SEMESTER == 0) {
            principalSubOwed += semesterSubOrig;
            principalUnsubOwed += semesterUnsubOrig;
            principalTotalOwed += semesterSubOrig + semesterUnsubOrig;
        }
    }

    /**
     * The main simulation loop: disburse funds, apply payments (interest first,
     * then principal), accrue interest, and repeat until paid or term limit.
     */
    private void simulate() {
        // TODO: ITERATE PER DAY INSTEAD OF PER MONTH
        for (month = 1; month <= MAX_REPAYMENT_TERM_MONTHS; month++) {
            disburseSemesterLoanIfNeeded();
            accrueInterest();
            if (!capitalizedUnsub && month == MONTHS_IN_UNI + POSTGRAD_SUB_GRACE_MONTHS + 1) {
                if (accruedUnsubInterest > 0) {
                    principalUnsubOwed += accruedUnsubInterest;
                    accruedUnsubInterest = 0;
                    principalTotalOwed = principalSubOwed + principalUnsubOwed;
                    totalInterest = accruedSubInterest + accruedUnsubInterest;
                }
                capitalizedUnsub = true;
            }
            double payment = month <= MONTHS_IN_UNI ? schoolMonthlyPayment : postgradMonthlyPayment;
            double amountTowardsPrincipal = makeInterestPayment(payment);
            if (amountTowardsPrincipal > 0) {
                double principalSubPortion = principalTotalOwed > 0 ? principalSubOwed / principalTotalOwed : 0;
                double principalUnsubPortion = 1.0 - principalSubPortion;
                payOffPrincipalAmount(amountTowardsPrincipal, principalSubPortion, principalUnsubPortion);
            }
            if (principalTotalOwed <= 0) {
                return;
            }
        }
        System.out.println("Unable to repay within repayment term!");
    }

    /** Reset simulator state before running a new simulation. */
    @Override
    final void resetState() {
        monthlyUnsubInterest = 0;
        monthlySubInterest = 0;
        accruedUnsubInterest = 0;
        accruedSubInterest = 0;
        totalUnsubInterest = 0;
        totalSubInterest = 0;
        totalInterestPaid = 0;
        principalSubOwed = 0;
        principalUnsubOwed = 0;
        principalTotalOwed = 0;
        month = 0;
        capitalizedUnsub = false;
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
        monthlyInterestRate = annualInterestRate / 12.0;
        schoolMonthlyPayment = Utils.takeUserDoubleInput(input, "Monthly payment while in school: ");
        postgradMonthlyPayment = Utils.takeUserDoubleInput(input, "Monthly payment after graduation: ");
    }

    /** Run the simulation and print a summary of results. */
    @Override
    final void runSimulation() {
        simulate();
        totalInterestPaid = roundCurrency(totalInterestPaid);
        String message = "%nTime elapsed: %d months or %.2f years to pay off your $%.2f loan.%n"
                + "Yearly loan amount: $%.2f%n" + "Interest rate: %.3f%%%n" + "Total interest paid: $%.2f%n"
                + "Total amount paid: $%.2f%n";
        System.out.printf(message, month, toYears(month), origTotalLoanAmount, yearlySubOrig + yearlyUnsubOrig,
                annualInterestRate * 100, totalInterestPaid, totalInterestPaid + origTotalLoanAmount);
    }

    /** @return total interest paid computed by the simulator */
    @Override
    public final double getTotalInterestPaid() { return totalInterestPaid; }

    /** @return original total loan amount used by the simulation */
    @Override
    public final double getOriginalLoanAmount() { return origTotalLoanAmount; }
}