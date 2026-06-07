package loansim;

import exilib.Utils;
import java.util.Scanner;

public class GenericLoanSimulator extends LoanSimulator {
    private double targetRepaymentTermMonths;
    private int months;
    private double accruedInterest;
    private double interestRate;
    private double monthlyPayment;
    private double monthlyInterest;
    private double totalInterestPaid;
    private String interestType;
    private double origLoanAmount;
    private final Scanner input;

    public GenericLoanSimulator(Scanner input) {
        resetState();
        this.input = input;
    }

    @Override
    final void resetState() {
        accruedInterest = 0;
        interestRate = 0;
        monthlyPayment = 0;
        monthlyInterest = 0;
        totalInterestPaid = 0;
        interestType = "";
        origLoanAmount = 0;
    }

    private double handleLoanInput() {
        while (true) {
            double loan = Utils.takeUserDoubleInput(input, "Loan amount: ");
            if (loan > 0) {
                return loan;
            }
            System.out.println("Enter a number above zero!");
        }
    }

    private double handleInterestRateInput() {
        while (true) {
            double rate = Utils.takeUserDoubleInput(input, "Monthly interest rate (as a percentage): ");
            if (rate <= 100 && rate >= 0) {
                return rate / 100.0;
            }
            System.out.println("Enter a number between 0 and 100.");
        }
    }

    private double handleMonthlyPaymentInput() {
        while (true) {
            double payment = Utils.takeUserDoubleInput(input, "Monthly payment: ");
            if (payment > 0) {
                return roundCurrency(payment);
            }
            System.out.println("Enter a number above zero!");
        }
    }

    private String handleInterestTypeInput() {
        while (true) {
            String type = Utils.takeUserStringInput(input, "Enter the interest type (simple or compound): ");
            if (!interestType.equalsIgnoreCase("simple") && !interestType.equalsIgnoreCase("compound")) {
                return type;
            }
            System.out.println("Please enter 'simple' or 'compound':");
        }
    }

    private double handleTargetRepaymentTermInput() {
        while (true) {
            double term = Utils.takeUserDoubleInput(input, "Target repayment term (in years): ");
            if (term > 0) {
                return term * 12;
            }
            System.out.println("Enter a number above zero!");
        }
    }

    @Override
    final void handleInput() {
        origLoanAmount = handleLoanInput();
        interestRate = handleInterestRateInput();
        monthlyPayment = handleMonthlyPaymentInput();
        interestType = handleInterestTypeInput();
        targetRepaymentTermMonths = handleTargetRepaymentTermInput();
    }

    private double getSimpleInterestBase(double principalOwed) {
        if (principalOwed <= origLoanAmount) {
            return principalOwed;
        } else {
            return origLoanAmount;
        }
    }

    private void printSimulationStats() {
        String message = "It will take %d months or %.2f years to repay the loan. Total interest paid: $%.2f%n";
        System.out.printf(message, months, toYears(months), totalInterestPaid);
    }

    private void simulateSimple() {
        double principalOwed = origLoanAmount;
        for (months = 1; months <= targetRepaymentTermMonths; months++) {
            double interestBase = getSimpleInterestBase(principalOwed);
            monthlyInterest = interestRate * interestBase;
            totalInterestPaid = totalInterestPaid + monthlyInterest;
            accruedInterest += monthlyInterest - monthlyPayment;
            if (accruedInterest < 0) {
                principalOwed = principalOwed + accruedInterest;
            }
            if (principalOwed <= 0) {
                printSimulationStats();
                return;
            }
        }
        String message = "Unable to repay within repayment term!"
                + "%nInterest left to pay: $%.2f%nTotal left to pay: $%.2f%n";
        System.out.printf(message, accruedInterest, principalOwed + accruedInterest);
    }

    private void simulateCompound() {
        double amountOwed = origLoanAmount;
        for (months = 1; months <= targetRepaymentTermMonths; months++) {
            monthlyInterest = interestRate * amountOwed;
            totalInterestPaid = totalInterestPaid + monthlyInterest;
            amountOwed = amountOwed + accruedInterest;
            accruedInterest += monthlyInterest - monthlyPayment;
            if (amountOwed <= 0) {
                printSimulationStats();
                return;
            }
        }
        String message = "Unable to repay within repayment term!"
                + "%nInterest left to pay: $%.2f%nTotal left to pay: $%.2f%n";
        System.out.printf(message, accruedInterest, amountOwed + accruedInterest);
    }

    @Override
    final void runSimulation() {
        boolean simpleInterest = interestType.equalsIgnoreCase("simple");
        boolean compoundInterest = interestType.equalsIgnoreCase("compound");
        if (simpleInterest) {
            simulateSimple();
        } else if (compoundInterest) {
            simulateCompound();
        } else {
            throw new IllegalStateException("Both simpleInterest and compoundInterest are false!");
        }
    }

    @Override
    public final double getTotalInterestPaid() { return totalInterestPaid; }

    @Override
    public final double getOriginalLoanAmount() { return origLoanAmount; }
}