package loansim;

import exilib.Utils;
import java.util.Scanner;

public class StudentLoanSimulator extends LoanSimulator {
    // TODO: STARTED JUNE 5 2026 - COMPLETE REFACTOR
    // TO CORRECTLY SIMULATE REAL-WORLD LOAN REPAYMENT
    // AND TAKE CUSTOM LOAN AMOUNTS AND INTEREST RATES
    private static final double MONTHS_IN_UNI = 48.0;
    private static final double YEARS_IN_UNI = 4.0;
    private static final double AVG_DAYS_IN_MONTH = 30.436875;
    private static final double MAX_REPAYMENT_TERM_MONTHS = 360.0;
    private final Scanner input;
    private double annualInterestRate;
    private double dailyInterestRate;
    private double semesterUnsubOrig;
    private double unsubsidizedOrig;
    private double semesterSubOrig;
    private double subsidizedOrig;
    private double origLoanAmount;
    private double principalUnsubOwed;
    private double principalSubOwed;
    private double principalTotalOwed;
    private double dailyUnsubInterest;
    private double dailySubInterest;
    private double monthlyUnsubInterest;
    private double monthlySubInterest;
    private double totalUnsubInterest;
    private double totalSubInterest;
    private double totalSubBalance;
    private double totalUnsubBalance;
    private double totalInterest;
    private double totalInterestPaid;
    private int count;
    private double schoolMonthlyPayment;
    private double postgradMonthlyPayment;

    public StudentLoanSimulator(Scanner input) {
        resetState();
        this.input = input;
    }

    private void accrueSubPostgradInterest() {
        if (count > MONTHS_IN_UNI) {
            dailySubInterest = dailyInterestRate * principalSubOwed;
            double accruedSubInterest = roundCurrency(dailySubInterest * AVG_DAYS_IN_MONTH);
            monthlySubInterest = roundCurrency(monthlySubInterest + accruedSubInterest);
            totalSubInterest = roundCurrency(totalSubInterest + accruedSubInterest);
        }
    }

    private void accrueUnsubInterest() {
        dailyUnsubInterest = dailyInterestRate * principalUnsubOwed;
        double accruedUnsubInterest = roundCurrency(dailyUnsubInterest * AVG_DAYS_IN_MONTH);
        monthlyUnsubInterest = roundCurrency(monthlyUnsubInterest + accruedUnsubInterest);
        totalUnsubInterest = roundCurrency(totalUnsubInterest + accruedUnsubInterest);
    }

    private void accrueInterest() {
        accrueSubPostgradInterest();
        accrueUnsubInterest();
        principalSubOwed = roundCurrency(principalSubOwed + dailySubInterest * AVG_DAYS_IN_MONTH);
        principalUnsubOwed = roundCurrency(principalUnsubOwed + dailyUnsubInterest * AVG_DAYS_IN_MONTH);
        principalTotalOwed = roundCurrency(principalSubOwed + principalUnsubOwed);
        totalSubBalance = roundCurrency(principalSubOwed + totalSubInterest);
        totalUnsubBalance = roundCurrency(principalUnsubOwed + totalUnsubInterest);
    }

    private void simulate() {
        while (principalTotalOwed > 0) {
            accrueInterest();
            double subProportion = totalSubBalance / (totalUnsubBalance + totalSubBalance);
            double unsubProportion = totalUnsubBalance / (totalUnsubBalance + totalSubBalance);
            double payment = count <= MONTHS_IN_UNI ? schoolMonthlyPayment : postgradMonthlyPayment;
            if (payment > totalUnsubBalance + totalSubBalance) {
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
        annualInterestRate = 0.0;
        dailyInterestRate = annualInterestRate / 365.0;
        semesterUnsubOrig = 0.0;
        unsubsidizedOrig = semesterUnsubOrig * YEARS_IN_UNI;
        semesterSubOrig = 0.0;
        subsidizedOrig = semesterSubOrig * YEARS_IN_UNI;
        principalUnsubOwed = unsubsidizedOrig;
        principalSubOwed = subsidizedOrig;
        origLoanAmount = unsubsidizedOrig + subsidizedOrig;
        dailyUnsubInterest = 0.0;
        dailySubInterest = 0.0;
        monthlyUnsubInterest = 0.0;
        monthlySubInterest = 0.0;
        totalUnsubInterest = 0.0;
        totalSubInterest = 0.0;
        totalInterestPaid = 0.0;
    }

    @Override
    final void handleInput() {
        schoolMonthlyPayment = Utils.validateUserDoubleInput(input, "Monthly subsidized payment while in school: ");
        postgradMonthlyPayment = Utils.validateUserDoubleInput(input,
                "Monthly unsubsidized payment after graduation: ");
    }

    @Override
    final void runSimulation() {
        totalInterestPaid = roundCurrency(totalSubInterest + totalUnsubInterest);
        String message = "Time elapsed: %d months or %.2f years to pay off your $%.2f loan. "
                + "%nTotal interest paid: $%.2f%n";
        System.out.printf(message, count, toYears(count), origLoanAmount, count, toYears(count), totalUnsubInterest,
                totalInterestPaid);
    }

    @Override
    public final double getTotalInterestPaid() { return totalInterestPaid; }

    @Override
    public final double getOriginalLoanAmount() { return origLoanAmount; }
}