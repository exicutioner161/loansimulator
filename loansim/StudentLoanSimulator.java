package loansim;

import exilib.Utils;
import java.util.Scanner;

public class StudentLoanSimulator extends LoanSimulator {
    // TODO: STARTED JUNE 5 2026 - COMPLETE REFACTOR
    // TO CORRECTLY SIMULATE REAL-WORLD LOAN REPAYMENT
    // AND TAKE CUSTOM LOAN AMOUNTS AND INTEREST RATES
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
    private double totalSubBalance;
    private double totalUnsubBalance;
    private double totalOwed;
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
            double monthlySubInterest = roundCurrency(dailySubInterest * AVG_DAYS_IN_MONTH);
            accruedSubInterest = roundCurrency(accruedSubInterest + monthlySubInterest);
            totalSubInterest = roundCurrency(totalSubInterest + monthlySubInterest);
        }
    }

    private void accrueUnsubInterest() {
        dailyUnsubInterest = dailyInterestRate * principalUnsubOwed;
        double monthlyUnsubInterest = roundCurrency(dailyUnsubInterest * AVG_DAYS_IN_MONTH);
        accruedUnsubInterest = roundCurrency(accruedUnsubInterest + monthlyUnsubInterest);
        totalUnsubInterest = roundCurrency(totalUnsubInterest + monthlyUnsubInterest);
    }

    private void accrueInterest() {
        accrueSubPostgradInterest();
        accrueUnsubInterest();
        totalInterest = roundCurrency(accruedSubInterest + accruedUnsubInterest);
        totalSubBalance = roundCurrency(principalSubOwed + accruedSubInterest);
        totalUnsubBalance = roundCurrency(principalUnsubOwed + accruedUnsubInterest);
        totalOwed = roundCurrency(totalSubBalance + totalUnsubBalance);
    }

    private boolean makeInterestPayment(double payment, double subProportion, double unsubProportion) {
        if (payment > totalInterest) {
            accruedSubInterest = 0;
            accruedUnsubInterest = 0;
            totalInterestPaid = roundCurrency(totalInterestPaid + totalInterest);
            totalInterest = 0;
            return true;
        } else {
            accruedSubInterest = roundCurrency(accruedSubInterest - payment * subProportion);
            accruedUnsubInterest = roundCurrency(accruedUnsubInterest - payment * unsubProportion);
            totalInterestPaid = roundCurrency(totalInterestPaid + payment);
            totalInterest = roundCurrency(totalInterest - payment);
            return false;
        }
    }

    private void payOffPrincipalAmount(double amountTowardsPrincipal, double subProportion, double unsubProportion) {
        double subPayment = roundCurrency(amountTowardsPrincipal * subProportion);
        double unsubPayment = roundCurrency(amountTowardsPrincipal * unsubProportion);
        principalSubOwed = roundCurrency(principalSubOwed - subPayment);
        principalUnsubOwed = roundCurrency(principalUnsubOwed - unsubPayment);
        if (principalSubOwed < 0) {
            principalSubOwed = 0;
        }
        if (principalUnsubOwed < 0) {
            principalUnsubOwed = 0;
        }
        principalTotalOwed = roundCurrency(principalSubOwed + principalUnsubOwed);
    }

    private void simulate() {
        while (principalTotalOwed > 0) {
            accrueInterest();
            double subProportion = totalSubBalance / totalOwed;
            double unsubProportion = totalUnsubBalance / totalOwed;
            double payment = count <= MONTHS_IN_UNI ? schoolMonthlyPayment : postgradMonthlyPayment;
            double amountTowardsPrincipal = 0;
            boolean interestPaidOff = makeInterestPayment(payment, subProportion, unsubProportion);
            if (interestPaidOff) {
                amountTowardsPrincipal = roundCurrency(payment - totalInterest);
            }
            if (amountTowardsPrincipal > 0) {
                payOffPrincipalAmount(amountTowardsPrincipal, subProportion, unsubProportion);
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
        totalSubBalance = principalSubOwed;
        totalUnsubBalance = principalUnsubOwed;
        totalOwed = origTotalLoanAmount;
        annualInterestRate = Utils.validateUserDoubleInput(input, "Annual interest rate (as a percentage): ") / 100.0;
        dailyInterestRate = annualInterestRate / 365.0;
        schoolMonthlyPayment = Utils.validateUserDoubleInput(input, "Monthly payment while in school: ");
        postgradMonthlyPayment = Utils.validateUserDoubleInput(input, "Monthly payment after graduation: ");
    }

    @Override
    final void runSimulation() {
        totalInterestPaid = roundCurrency(totalSubInterest + totalUnsubInterest);
        simulate();
        String message = "Time elapsed: %d months or %.2f years to pay off your $%.2f loan. "
                + "%nTotal interest paid: $%.2f%n";
        System.out.printf(message, count, toYears(count), origTotalLoanAmount, totalInterestPaid);
    }

    @Override
    public final double getTotalInterestPaid() { return totalInterestPaid; }

    @Override
    public final double getOriginalLoanAmount() { return origTotalLoanAmount; }
}