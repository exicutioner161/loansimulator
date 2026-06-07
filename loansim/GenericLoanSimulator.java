package loansim;

import exilib.Utils;
import java.util.Scanner;

public class GenericLoanSimulator extends LoanSimulator {
    private double targetRepaymentTerm;
    private int count;
    private double principalOwed;
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
        principalOwed = 0;
        accruedInterest = 0;
        interestRate = 0;
        monthlyPayment = 0;
        monthlyInterest = 0;
        totalInterestPaid = 0;
        interestType = "";
        origLoanAmount = 0;
        count = 1;
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
        double payment = Utils.takeUserDoubleInput(input, "Monthly payment: ");
        return roundCurrency(payment);
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
        principalOwed = origLoanAmount;
        interestRate = handleInterestRateInput();
        monthlyPayment = handleMonthlyPaymentInput();
        interestType = handleInterestTypeInput();
        targetRepaymentTerm = handleTargetRepaymentTermInput();
    }

    private double getSimpleInterestBase() {
        if (principalOwed <= origLoanAmount) {
            return principalOwed;
        } else {
            return origLoanAmount;
        }
    }

    private void printSimulationStats() {
        String message = "It will take %d months or %.2f years to repay the loan. Total interest paid: $%.2f%n";
        System.out.printf(message, count, toYears(count), totalInterestPaid);
    }

    private void simulateSimple() {
        while (principalOwed > 0) {
            double interestBase = getSimpleInterestBase();
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
            if (count >= targetRepaymentTerm) {
                String message = "Unable to repay within repayment term!"
                        + "%nInterest left to pay: $%.2f%nTotal left to pay: $%.2f%n";
                System.out.printf(message, accruedInterest, principalOwed + accruedInterest);
                return;
            }
            count++;
        }
    }

    private void simulateCompound() {
        double amountOwed = origLoanAmount;
        while (amountOwed > 0) {
            double interestBase = amountOwed;
            monthlyInterest = interestRate * interestBase;
            totalInterestPaid = totalInterestPaid + monthlyInterest;
            accruedInterest += monthlyInterest - monthlyPayment;
            amountOwed = amountOwed + accruedInterest;
            if (amountOwed <= 0) {
                printSimulationStats();
                return;
            }
            if (count >= targetRepaymentTerm) {
                String message = "Unable to repay within repayment term!"
                        + "%nInterest left to pay: $%.2f%nTotal left to pay: $%.2f%n";
                System.out.printf(message, accruedInterest, amountOwed + accruedInterest);
                return;
            }
            count++;
        }
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