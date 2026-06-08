package loansim;

import exilib.Utils;
import java.util.Scanner;

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

    public StudentLoanSimulator(Scanner input) {
        resetState();
        this.input = input;
    }

    private void accrueSubPostgradInterest() {
        if (month > MONTHS_IN_UNI + POSTGRAD_SUB_GRACE_MONTHS) {
            monthlySubInterest = monthlyInterestRate * principalSubOwed;
            accruedSubInterest = accruedSubInterest + monthlySubInterest;
            totalSubInterest = totalSubInterest + monthlySubInterest;
        }
    }

    private void accrueUnsubInterest() {
        monthlyUnsubInterest = monthlyInterestRate * principalUnsubOwed;
        accruedUnsubInterest = accruedUnsubInterest + monthlyUnsubInterest;
        totalUnsubInterest = totalUnsubInterest + monthlyUnsubInterest;
    }

    private void accrueInterest() {
        accrueSubPostgradInterest();
        accrueUnsubInterest();
        totalInterest = accruedSubInterest + accruedUnsubInterest;
    }

    private double makeInterestPayment(double payment) {
        double interestBefore = totalInterest;
        if (interestBefore <= 0) {
            return payment;
        }
        if (payment >= interestBefore) {
            totalInterestPaid = totalInterestPaid + interestBefore;
            double paymentLeft = payment - interestBefore;
            accruedSubInterest = 0;
            accruedUnsubInterest = 0;
            totalInterest = 0;
            return paymentLeft;
        } else {
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
    }

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

    private void disburseSemesterLoanIfNeeded() {
        if (month <= MONTHS_IN_UNI && (month - 1) % MONTHS_IN_SEMESTER == 0) {
            principalSubOwed += semesterSubOrig;
            principalUnsubOwed += semesterUnsubOrig;
            principalTotalOwed += semesterSubOrig + semesterUnsubOrig;
        }
    }

    private void simulate() {
        // TODO: ITERATE PER DAY INSTEAD OF PER MONTH
        for (month = 1; month <= MAX_REPAYMENT_TERM_MONTHS; month++) {
            disburseSemesterLoanIfNeeded();
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
            accrueInterest();
        }
        System.out.println("Unable to repay within repayment term!");
    }

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
    }

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

    @Override
    final void runSimulation() {
        totalInterestPaid = roundCurrency(totalInterestPaid);
        simulate();
        String message = "%nTime elapsed: %d months or %.2f years to pay off your $%.2f loan.%n"
                + "Yearly loan amount: $%.2f%n" + "Interest rate: %.3f%%%n" + "Total interest paid: $%.2f%n"
                + "Total amount paid: $%.2f%n";
        System.out.printf(message, month, toYears(month), origTotalLoanAmount, yearlySubOrig + yearlyUnsubOrig,
                annualInterestRate * 100, totalInterestPaid, totalInterestPaid + origTotalLoanAmount);
    }

    @Override
    public final double getTotalInterestPaid() { return totalInterestPaid; }

    @Override
    public final double getOriginalLoanAmount() { return origTotalLoanAmount; }
}