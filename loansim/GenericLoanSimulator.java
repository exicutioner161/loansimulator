package loansim;

import exilib.Utils;
import java.util.Scanner;

public class GenericLoanSimulator extends LoanSimulator {
    private static final double MAX_REPAYMENT_TERM_MONTHS = 600.0;
    private int count;
    private double principalOwed;
    private double accruedInterest;
    private double interestRate;
    private double monthlyPayment;
    private double monthlyInterest;
    private double totalInterest;
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
        totalInterest = 0;
        interestType = "";
        origLoanAmount = 0;
    }

    private double handleLoanInput() {
        while (true) {
            double loan = Utils.validateUserDoubleInput(input, "Loan amount: ");
            if (loan > 0) {
                return loan;
            }
            System.out.println("Enter a number above zero!");
        }
    }

    private double handleInterestRateInput() {
        while (true) {
            double rate = Utils.validateUserDoubleInput(input,
                    "Monthly interest rate (enter as a decimal. ex: 1% = 0.01): ");
            if (rate < 1 && rate >= 0) {
                return rate;
            }
            System.out.println("Enter a number between 0 and 1.");
        }
    }

    private double handleMonthlyPaymentInput() {
        double payment = Utils.validateUserDoubleInput(input, "Monthly payment: ");
        if (payment <= origLoanAmount * interestRate) {
            System.out.println("WARNING: Monthly payment is less than the monthly interest " + "($"
                    + origLoanAmount * interestRate + ").");
        }
        return roundCurrency(payment);
    }

    private String handleInterestTypeInput() {
        while (true) {
            String type = Utils.validateUserStringInput(input, "Enter the interest type (simple or compound): ");
            if (!interestType.equalsIgnoreCase("simple") && !interestType.equalsIgnoreCase("compound")) {
                return type;
            }
            System.out.println("Please enter simple or compound:");
        }
    }

    @Override
    final void handleInput() {
        origLoanAmount = handleLoanInput();
        principalOwed = origLoanAmount;
        interestRate = handleInterestRateInput();
        monthlyPayment = handleMonthlyPaymentInput();
        interestType = handleInterestTypeInput();
    }

    private void printSimulationStats() {
        String message = "It will take %d months or %.2f years to repay the loan. Total interest paid: $%.2f%n";
        System.out.printf(message, count, toYears(count), totalInterest);
    }

    private void simulateSimple() {
        count = 1;
        while (principalOwed > 0) {
            double interestBase;
            if (principalOwed <= origLoanAmount) {
                interestBase = principalOwed;
            } else {
                interestBase = origLoanAmount;
            }
            monthlyInterest = roundCurrency(interestRate * interestBase);
            totalInterest = roundCurrency(totalInterest + monthlyInterest);
            accruedInterest += monthlyInterest - monthlyPayment;
            if (accruedInterest < 0) {
                principalOwed = roundCurrency(principalOwed + accruedInterest);
            }
            if (principalOwed <= 0) {
                printSimulationStats();
                return;
            }
            if (count >= MAX_REPAYMENT_TERM_MONTHS) {
                String message = "WARNING: Unable to repay within repayment term!"
                        + "%nInterest left to pay: %.2f%nTotal left to pay: %.2f%n";
                System.out.printf(message, accruedInterest, principalOwed + accruedInterest);
                return;
            }
            count++;
        }
    }

    private void simulateCompound() {
        count = 1;
        double amountOwed = origLoanAmount;
        while (amountOwed > 0) {
            double interestBase = amountOwed;
            monthlyInterest = roundCurrency(interestRate * interestBase);
            totalInterest = roundCurrency(totalInterest + monthlyInterest);
            amountOwed = roundCurrency(amountOwed + monthlyInterest - monthlyPayment);
            if (amountOwed <= 0) {
                printSimulationStats();
                return;
            }
            if (count >= MAX_REPAYMENT_TERM_MONTHS) {
                String message = "WARNING: Unable to repay within repayment term!"
                        + "%nInterest left to pay: %.2f%nTotal left to pay: %.2f%n";
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
    public final double getTotalInterest() { return totalInterest; }

    @Override
    public final double getOriginalLoanAmount() { return origLoanAmount; }
}