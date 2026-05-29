package loansim;

import exilib.Utils;
import java.util.Scanner;

public class StudentLoanSimulator extends LoanSimulator {
    private static final double MONTHS_IN_UNI = 48.0;
    private static final double YEARS_IN_UNI = 4.0;
    private static final double AVG_DAYS_IN_MONTH = 30.436875;
    private static final double MAX_REPAYMENT_TERM_MONTHS = 360.0;
    private double annualInterestRate = 0.06283;
    private double dailyInterestRate = annualInterestRate / 365.0;
    private double semesterUnsubOrig = 2000;
    private double unsubsidizedOrig = semesterUnsubOrig * YEARS_IN_UNI;
    private double semesterSubOrig = 3500;
    private double subsidizedOrig = semesterSubOrig * YEARS_IN_UNI;
    private double origLoanAmount = unsubsidizedOrig + subsidizedOrig;
    private double principalUnsubOwed;
    private double principalSubOwed;
    private double dailyUnsubInterest;
    private double dailySubInterest;
    private double monthlyUnsubInterest;
    private double monthlySubInterest;
    private double totalUnsubInterest;
    private double totalSubInterest;
    private double totalInterest;
    private int countSub;
    private int countUnsub;
    private double schoolMonthlyUnsub;
    private double schoolMonthlySub;
    private double postgradMonthlyUnsub;
    private double postgradMonthlySub;
    private String response;
    private boolean unsubFullyPaid;
    private boolean subFullyPaid;
    private final Scanner input;

    public StudentLoanSimulator(Scanner input) {
        resetState();
        this.input = input;
    }

    private void accrueInterestIfPostgrad() {
        if (countSub > MONTHS_IN_UNI) {
            dailySubInterest = dailyInterestRate * principalSubOwed;
            double accruedSubInterest = roundCurrency(dailySubInterest * AVG_DAYS_IN_MONTH);
            monthlySubInterest = roundCurrency(monthlySubInterest + accruedSubInterest);
            totalSubInterest = roundCurrency(totalSubInterest + accruedSubInterest);
        }
    }

    private void simulateSubsidized(double schoolMonthly, double postgradMonthly) {
        countSub = 1;
        while (principalSubOwed > 0) {
            accrueInterestIfPostgrad();
            double totalSub = principalSubOwed + monthlySubInterest;
            if (countSub <= MONTHS_IN_UNI) {
                printMonthlySub(countSub, schoolMonthly, totalSub);
                monthlySubInterest -= schoolMonthly;
            } else {
                printMonthlySub(countSub, postgradMonthly, totalSub);
                monthlySubInterest -= postgradMonthly;
            }
            System.out.printf("Interest balance: $%.2f%n", monthlySubInterest);
            if (monthlySubInterest < 0) {
                principalSubOwed += monthlySubInterest;
                monthlySubInterest = 0;
            }
            System.out.printf("Subsidized principal balance after payment: $%.2f%n%n", principalSubOwed);
            if (principalSubOwed <= 0) {
                subFullyPaid = true;
                return;
            }
            if (countSub >= MAX_REPAYMENT_TERM_MONTHS) {
                System.out.println("WARNING: Unable to repay within repayment term!");
                subFullyPaid = false;
                return;
            }
            countSub++;
        }
    }

    private void simulateUnsubsidized(double schoolMonthly, double postgradMonthly) {
        countUnsub = 1;
        while (principalUnsubOwed > 0) {
            dailyUnsubInterest = dailyInterestRate * principalUnsubOwed;
            double accruedUnsubInterest = roundCurrency(dailyUnsubInterest * AVG_DAYS_IN_MONTH);
            monthlyUnsubInterest = roundCurrency(monthlyUnsubInterest + accruedUnsubInterest);
            totalUnsubInterest = roundCurrency(totalUnsubInterest + accruedUnsubInterest);
            double totalUnsub = principalUnsubOwed + monthlyUnsubInterest;
            if (countUnsub <= MONTHS_IN_UNI) {
                printMonthlyUnsub(countUnsub, schoolMonthly, totalUnsub);
                monthlyUnsubInterest -= schoolMonthly;
            } else {
                printMonthlyUnsub(countUnsub, postgradMonthly, totalUnsub);
                monthlyUnsubInterest -= postgradMonthly;
            }
            System.out.printf("Interest balance: $%.2f%n", monthlyUnsubInterest);
            if (monthlyUnsubInterest < 0) {
                principalUnsubOwed += monthlyUnsubInterest;
                monthlyUnsubInterest = 0;
            }
            System.out.printf("Unsubsidized principal balance after payment: $%.2f%n%n", principalUnsubOwed);
            if (principalUnsubOwed <= 0) {
                unsubFullyPaid = true;
                return;
            }
            if (countUnsub >= MAX_REPAYMENT_TERM_MONTHS) {
                System.out.println("WARNING: Unable to repay within repayment term!");
                unsubFullyPaid = false;
                return;
            }
            countUnsub++;
        }
    }

    private void simulateSubsidizedFast(double schoolMonthly, double postgradMonthly) {
        countSub = 1;
        while (principalSubOwed > 0) {
            accrueInterestIfPostgrad();
            if (countSub <= MONTHS_IN_UNI) {
                monthlySubInterest -= schoolMonthly;
            } else {
                monthlySubInterest -= postgradMonthly;
            }
            if (monthlySubInterest < 0) {
                principalSubOwed += monthlySubInterest;
                monthlySubInterest = 0;
            }
            if (principalSubOwed <= 0) {
                subFullyPaid = true;
                return;
            }
            if (countSub >= MAX_REPAYMENT_TERM_MONTHS) {
                System.out.println("WARNING: Unable to repay within repayment term!");
                subFullyPaid = false;
                return;
            }
            countSub++;
        }
    }

    private void simulateUnsubsidizedFast(double schoolMonthly, double postgradMonthly) {
        countUnsub = 1;
        while (principalUnsubOwed > 0) {
            dailyUnsubInterest = dailyInterestRate * principalUnsubOwed;
            double accruedUnsubInterest = roundCurrency(dailyUnsubInterest * AVG_DAYS_IN_MONTH);
            monthlyUnsubInterest = roundCurrency(monthlyUnsubInterest + accruedUnsubInterest);
            totalUnsubInterest = roundCurrency(totalUnsubInterest + accruedUnsubInterest);
            if (countUnsub <= MONTHS_IN_UNI) {
                monthlyUnsubInterest -= schoolMonthly;
            } else {
                monthlyUnsubInterest -= postgradMonthly;
            }
            if (monthlyUnsubInterest < 0) {
                principalUnsubOwed += monthlyUnsubInterest;
                monthlyUnsubInterest = 0;
            }
            if (principalUnsubOwed <= 0) {
                unsubFullyPaid = true;
                return;
            }
            if (countUnsub >= MAX_REPAYMENT_TERM_MONTHS) {
                System.out.println("WARNING: Unable to repay within repayment term!");
                unsubFullyPaid = false;
                return;
            }
            countUnsub++;
        }
    }

    private void printMonthlySub(int month, double payment, double totalSub) {
        String subMsg = "MONTH %d:%nMonthly payment: $%.2f%n"
                + "Outstanding subsidized interest: $%.2f%nTotal subsidized balance: $%.2f%n";
        System.out.printf(subMsg, month, payment, monthlySubInterest, totalSub);
    }

    private void printMonthlyUnsub(int month, double payment, double totalUnsub) {
        String unsubMsg = "MONTH %d:%nMonthly payment: $%.2f%n"
                + "Outstanding unsubsidized interest: $%.2f%nTotal unsubsidized balance: $%.2f%n";
        System.out.printf(unsubMsg, month, payment, monthlyUnsubInterest, totalUnsub);
    }

    @Override
    final void resetState() {
        origLoanAmount = unsubsidizedOrig + subsidizedOrig;
        annualInterestRate = 0.06283;
        dailyInterestRate = annualInterestRate / 365.0;
        semesterUnsubOrig = 2000;
        unsubsidizedOrig = semesterUnsubOrig * YEARS_IN_UNI;
        semesterSubOrig = 3500;
        subsidizedOrig = semesterSubOrig * YEARS_IN_UNI;
        dailyUnsubInterest = 0;
        dailySubInterest = 0;
        monthlyUnsubInterest = 0;
        monthlySubInterest = 0;
        totalUnsubInterest = 0;
        totalSubInterest = 0;
        totalInterest = 0;
        response = "";
        unsubFullyPaid = false;
        subFullyPaid = false;
    }

    @Override
    final void handleInput() {
        schoolMonthlySub = Utils.validateUserDoubleInput(input, "Monthly subsidized payment while in school: ");
        schoolMonthlyUnsub = Utils.validateUserDoubleInput(input, "Monthly unsubsidized payment while in school: ");
        postgradMonthlySub = Utils.validateUserDoubleInput(input, "Monthly subsidized payment after graduation: ");
        postgradMonthlyUnsub = Utils.validateUserDoubleInput(input, "Monthly unsubsidized payment after graduation: ");
        input.nextLine();
        response = Utils.validateUserStringInput(input, "Print monthly stats while running? Y/N: ");
        System.out.println();
    }

    @Override
    final void runSimulation() {
        switch (response.toUpperCase()) {
        case "Y", "YES" -> {
            simulateSubsidized(schoolMonthlySub, postgradMonthlySub);
            simulateUnsubsidized(schoolMonthlyUnsub, postgradMonthlyUnsub);
        }
        case "N", "NO" -> {
            simulateSubsidizedFast(schoolMonthlySub, postgradMonthlySub);
            simulateUnsubsidizedFast(schoolMonthlyUnsub, postgradMonthlyUnsub);
        }
        default -> {
            System.out.println("Not a valid response. Defaulting to NO.");
            simulateSubsidizedFast(schoolMonthlySub, postgradMonthlySub);
            simulateUnsubsidizedFast(schoolMonthlyUnsub, postgradMonthlyUnsub);
        }
        }
        totalInterest = roundCurrency(totalSubInterest + totalUnsubInterest);
        String message = "Time elapsed: %d months or %.2f years for the subsidized loan. "
                + "Fully paid: %b. Subsidized interest paid: $%.2f%n"
                + "Time elapsed: %d months or %.2f years for the unsubsidized loan. "
                + "Fully paid: %b. Unsubsidized interest paid: $%.2f%nTotal interest paid: $%.2f%n";
        System.out.printf(message, countSub, toYears(countSub), subFullyPaid, totalSubInterest, countUnsub,
                toYears(countUnsub), unsubFullyPaid, totalUnsubInterest, totalInterest);
    }

    @Override
    public final double getTotalInterest() { return totalInterest; }

    @Override
    public final double getOriginalLoanAmount() { return origLoanAmount; }
}