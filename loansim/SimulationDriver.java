package loansim;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Program entry point and interactive driver for running loan simulators.
 */
public class SimulationDriver {
    private SimulationDriver() {
        /* This class should not be instantiated */
    }

    /**
     * Prompt the user to select a loan type from the menu.
     *
     * @param input scanner to read user responses
     * @return selected loan type as an integer
     */
    private static int chooseLoanType(Scanner input) {
        String in;
        System.out.println("Which type of loan would you like to simulate?");
        while (true) {
            System.out.println("1. Federal student loans\n2. Other");
            in = input.nextLine().trim();
            if (in.equals("1") || in.equals("2")) {
                break;
            }
            System.out.println("Invalid input: " + in + "\nPlease try again.");
        }
        return Integer.parseInt(in);
    }

    /**
     * Ask the user whether to continue or quit the interactive session.
     *
     * @param input scanner to read user responses
     * @return {@code true} to continue, {@code false} to quit
     */
    private static boolean continueRunning(Scanner input) {
        while (true) {
            System.out.print("Enter Q to quit or C to continue: ");
            String in = input.nextLine().trim().toUpperCase();
            if (in.equals("Q")) {
                return false;
            }
            if (in.equals("C")) {
                return true;
            }
            System.out.println("Invalid input! Please try again.");
        }
    }

    /**
     * Create and run a simulator based on the selected loan type, collecting
     * summary statistics for each run.
     *
     * @param input        scanner to read user input
     * @param interestList list that will receive total interest paid values
     * @param amountList   list that will receive original loan amounts
     */
    private static void chooseAndRunSimulation(Scanner input, List<Double> interestList, List<Double> amountList) {
        int loanType = chooseLoanType(input);
        do {
            LoanSimulator sim = LoanSimulatorFactory.getSimulator(input, loanType);
            if (sim == null) {
                throw new IllegalStateException("LoanSimulatorFactory returned null for loanType: " + loanType);
            }
            sim.run();
            interestList.add(sim.getTotalInterestPaid());
            amountList.add(sim.getOriginalLoanAmount());
        } while (continueRunning(input));
    }

    /** Print a short summary for each completed simulation. */
    private static void printSimulationStats(List<Double> interestList, List<Double> amountList) {
        for (int i = 0; i < interestList.size(); i++) {
            double totalToBePaid = interestList.get(i) + amountList.get(i);
            System.out.printf("Simulation %d interest: $%.2f, Total to be paid: $%.2f%n", i + 1, interestList.get(i),
                    totalToBePaid);
        }
    }

    /**
     * Main entry point: run the interactive driver and display summaries when the
     * user finishes.
     */
    public static void main() {
        try (Scanner input = new Scanner(System.in)) {
            List<Double> interestList = new ArrayList<>();
            List<Double> amountList = new ArrayList<>();
            chooseAndRunSimulation(input, interestList, amountList);
            System.out.println();
            printSimulationStats(interestList, amountList);
        }
    }
}