package loansim;

import exilib.Utils;
import java.util.Scanner;

/**
 * Generic loan simulator supporting simple and compound interest options.
 */
public class GenericLoanSimulator extends LoanSimulator {
    private double targetRepaymentTermMonths;
    private int month;
    private double accruedInterest;
    private double interestRate;
    private double monthlyPayment;
    private double monthlyInterest;
    private double totalInterestPaid;
    private String interestType;
    private double origLoanAmount;
    private final Scanner input;

    /**
     * Create a new simulator that reads input from the provided {@link Scanner}.
     *
     * @param input scanner to read user input from
     */
    public GenericLoanSimulator(Scanner input) {
        resetState();
        this.input = input;
    }

    /** Reset all internal fields to their default starting values. */
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

    /**
     * Prompt for the loan amount and ensure it is positive.
     *
     * @return validated loan principal in dollars
     */
    private double handleLoanInput() {
        while (true) {
            double loan = Utils.takeUserDoubleInput(input, "Loan amount: ");
            if (loan > 0) {
                return loan;
            }
            System.out.println("Enter a number above zero!");
        }
    }

    /**
     * Prompt for a monthly interest rate (percentage) and validate range.
     *
     * @return monthly interest rate as a decimal (e.g. 0.01 for 1%)
     */
    private double handleInterestRateInput() {
        while (true) {
            double rate = Utils.takeUserDoubleInput(input, "Monthly interest rate (as a percentage): ");
            if (rate <= 100 && rate >= 0) {
                return rate / 100.0;
            }
            System.out.println("Enter a number between 0 and 100.");
        }
    }

    /**
     * Prompt for a monthly payment and round to cents.
     *
     * @return monthly payment in dollars (rounded to cents)
     */
    private double handleMonthlyPaymentInput() {
        while (true) {
            double payment = Utils.takeUserDoubleInput(input, "Monthly payment: ");
            if (payment > 0) {
                return Utils.roundTwoDecimals(payment);
            }
            System.out.println("Enter a number above zero!");
        }
    }

    /**
     * Prompt the user for the interest type (simple or compound).
     *
     * @return the validated interest type string
     */
    private String handleInterestTypeInput() {
        while (true) {
            String type = Utils.takeUserStringInput(input, "Enter the interest type (simple or compound): ");
            if (!interestType.equalsIgnoreCase("simple") && !interestType.equalsIgnoreCase("compound")) {
                return type;
            }
            System.out.println("Please enter 'simple' or 'compound':");
        }
    }

    /**
     * Prompt for the target repayment term in years and convert to months.
     *
     * @return target repayment term in months
     */
    private double handleTargetRepaymentTermInput() {
        while (true) {
            double term = Utils.takeUserDoubleInput(input, "Target repayment term (in years): ");
            if (term > 0) {
                return term * 12;
            }
            System.out.println("Enter a number above zero!");
        }
    }

    /** Read required inputs from the user. */
    @Override
    final void handleInput() {
        origLoanAmount = handleLoanInput();
        interestRate = handleInterestRateInput();
        monthlyPayment = handleMonthlyPaymentInput();
        interestType = handleInterestTypeInput();
        targetRepaymentTermMonths = handleTargetRepaymentTermInput();
    }

    /**
     * Compute the base principal used for simple interest calculations.
     *
     * @param principalOwed current principal owed
     * @return the base value used to compute simple interest
     */
    private double getSimpleInterestBase(double principalOwed) {
        if (principalOwed <= origLoanAmount) {
            return principalOwed;
        } else {
            return origLoanAmount;
        }
    }

    /** Print a summary of the simulation results to standard output. */
    private void printSimulationStats() {
        String message = "It will take %d months or %.2f years to repay the loan. Total interest paid: $%.2f%n";
        System.out.printf(message, month, Utils.toYears(month), totalInterestPaid);
    }

    /**
     * Simulate repayment assuming simple interest rules. The method iterates
     * month-by-month until repayment or target term reached.
     */
    private void simulateSimple() {
        double principalOwed = origLoanAmount;
        for (month = 1; month <= targetRepaymentTermMonths; month++) {
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

    /**
     * Simulate repayment assuming monthly compound interest.
     */
    private void simulateCompound() {
        double amountOwed = origLoanAmount;
        for (month = 1; month <= targetRepaymentTermMonths; month++) {
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

    /** Choose and run the appropriate simulation variant based on user input. */
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

    /**
     * @return the total interest paid during the simulation
     */
    @Override
    public final double getTotalInterestPaid() { return totalInterestPaid; }

    /**
     * @return the original loan amount used for the simulation
     */
    @Override
    public final double getOriginalLoanAmount() { return origLoanAmount; }
}