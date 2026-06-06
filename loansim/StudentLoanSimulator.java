package loansim;

import exilib.Utils;
import java.util.Scanner;

public class StudentLoanSimulator extends LoanSimulator {
    private static final double MONTHS_IN_UNI = 48.0;
    private static final double YEARS_IN_UNI = 4.0;
    private static final int SEMESTERS_IN_YEAR = 2;
    private static final double AVG_DAYS_IN_MONTH = 30.436875;
    private static final double MAX_REPAYMENT_TERM_MONTHS = 120.0;
    private final Scanner input;
    private double annualInterestRate;
    private double dailyInterestRate;
    private double semesterSubOrig;
    private double yearlySubOrig;
    private double semesterUnsubOrig;
    private double yearlyUnsubOrig;
    private double origTotalLoanAmount;
    private double principalSubOwed;
    private double principalUnsubOwed;
    private double principalTotalOwed;
    private double dailyUnsubInterest;
    private double dailySubInterest;
    private double accruedUnsubInterest;
    private double accruedSubInterest;
    private double totalUnsubInterest;
    private double totalSubInterest;
    private double totalInterest;
    private double totalInterestPaid;
    private double schoolMonthlyPayment;
    private double postgradMonthlyPayment;
    private int count;

    public StudentLoanSimulator(Scanner input) {
        resetState();
        this.input = input;
    }

    private void accrueSubPostgradInterest() {
        if (count > MONTHS_IN_UNI) {
            dailySubInterest = dailyInterestRate * principalSubOwed;
            double monthlySubInterest = dailySubInterest * AVG_DAYS_IN_MONTH;
            accruedSubInterest = accruedSubInterest + monthlySubInterest;
            totalSubInterest = totalSubInterest + monthlySubInterest;
        }
    }

    private void accrueUnsubInterest() {
        dailyUnsubInterest = dailyInterestRate * principalUnsubOwed;
        double monthlyUnsubInterest = dailyUnsubInterest * AVG_DAYS_IN_MONTH;
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
            double subShare = accruedSubInterest / interestBefore;
            double unsubShare = accruedUnsubInterest / interestBefore;
            double subPaid = payment * subShare;
            double unsubPaid = payment * unsubShare;
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

    private void simulate() {
        while (principalTotalOwed > 0) {
            accrueInterest();
            double payment = count <= MONTHS_IN_UNI ? schoolMonthlyPayment : postgradMonthlyPayment;
            double amountTowardsPrincipal = makeInterestPayment(payment);
            if (amountTowardsPrincipal > 0) {
                double principalSubPortion = principalTotalOwed > 0 ? principalSubOwed / principalTotalOwed : 0;
                double principalUnsubPortion = 1.0 - principalSubPortion;
                payOffPrincipalAmount(amountTowardsPrincipal, principalSubPortion, principalUnsubPortion);
            }
            if (principalTotalOwed <= 0) {
                return;
            }
            if (count >= MAX_REPAYMENT_TERM_MONTHS) {
                System.out.println("Unable to repay within repayment term!");
                return;
            }
            count++;
        }
    }

    @Override
    final void resetState() {
        dailyUnsubInterest = 0;
        dailySubInterest = 0;
        accruedUnsubInterest = 0;
        accruedSubInterest = 0;
        totalUnsubInterest = 0;
        totalSubInterest = 0;
        totalInterestPaid = 0;
        count = 1;
    }

    @Override
    final void handleInput() {
        semesterSubOrig = Utils.validateUserDoubleInput(input, "Subsidized loan amount per semester: ");
        semesterUnsubOrig = Utils.validateUserDoubleInput(input, "Unsubsidized loan amount per semester: ");
        yearlySubOrig = semesterSubOrig * SEMESTERS_IN_YEAR;
        yearlyUnsubOrig = semesterUnsubOrig * SEMESTERS_IN_YEAR;
        origTotalLoanAmount = (yearlySubOrig + yearlyUnsubOrig) * YEARS_IN_UNI;
        principalSubOwed = yearlySubOrig * YEARS_IN_UNI;
        principalUnsubOwed = yearlyUnsubOrig * YEARS_IN_UNI;
        principalTotalOwed = origTotalLoanAmount;
        annualInterestRate = Utils.validateUserDoubleInput(input, "Annual interest rate (as a percentage): ") / 100.0;
        dailyInterestRate = annualInterestRate / 365.0;
        schoolMonthlyPayment = Utils.validateUserDoubleInput(input, "Monthly payment while in school: ");
        postgradMonthlyPayment = Utils.validateUserDoubleInput(input, "Monthly payment after graduation: ");
    }

    @Override
    final void runSimulation() {
        totalInterestPaid = roundCurrency(totalSubInterest + totalUnsubInterest);
        simulate();
        String message = "%nTime elapsed: %d months or %.2f years to pay off your $%.2f loan.%n"
                + "Yearly loan amount: $%.2f%n" + "Interest rate: %.3f%%%n" + "Total interest paid: $%.2f%n"
                + "Total amount paid: $%.2f%n";
        System.out.printf(message, count, toYears(count), origTotalLoanAmount, yearlySubOrig + yearlyUnsubOrig,
                annualInterestRate * 100, totalInterestPaid, totalInterestPaid + origTotalLoanAmount);
    }

    @Override
    public final double getTotalInterestPaid() { return totalInterestPaid; }

    @Override
    public final double getOriginalLoanAmount() { return origTotalLoanAmount; }
}