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

    @Override
    final void handleInput() {
        while (true) {
            origLoanAmount = Utils.validateUserDoubleInput(input, "Loan amount: ");
            if (origLoanAmount > 0) {
                break;
            }
            System.out.println("Enter a number above zero!");
        }
        principalOwed = origLoanAmount;
        while (true) {
            interestRate = Utils.validateUserDoubleInput(input,
                    "Monthly interest rate (enter as a decimal. ex: 1% = 0.01): ");
            if (interestRate < 1 && interestRate >= 0) {
                break;
            }
            System.out.println("Enter a number between 0 and 1.");
        }
        monthlyPayment = Utils.validateUserDoubleInput(input, "Monthly payment: ");
        if (monthlyPayment <= roundCurrency(origLoanAmount * interestRate)) {
            System.out.println("WARNING: Monthly payment is less than the monthly interest " + "($"
                    + roundCurrency(origLoanAmount * interestRate) + ").");
        }
        System.out.print("\nEnter the interest type (simple or compound): ");
        interestType = input.nextLine().trim();
        while (!interestType.equalsIgnoreCase("simple") && !interestType.equalsIgnoreCase("compound")) {
            System.out.println("Please enter simple or compound:");
            interestType = input.nextLine().trim();
        }
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