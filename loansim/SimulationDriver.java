package loansim;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimulationDriver {
    private SimulationDriver() {
        /* This class should not be instantiated */
    }

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

    private static void chooseAndRunSimulation(Scanner input, List<Double> interestList, List<Double> amountList) {
        int loanType = chooseLoanType(input);
        do {
            LoanSimulator sim = LoanSimulatorFactory.getSimulator(input, loanType);
            if (sim == null) {
                throw new IllegalStateException("LoanSimulatorFactory returned null for loanType: " + loanType);
            }
            sim.run();
            interestList.add(sim.getTotalInterest());
            amountList.add(sim.getOriginalLoanAmount());
        } while (continueRunning(input));
    }

    private static void printSimulationStats(List<Double> interestList, List<Double> amountList) {
        for (int i = 0; i < interestList.size(); i++) {
            double totalToBePaid = interestList.get(i) + amountList.get(i);
            System.out.printf("Simulation %d interest: $%.2f, Total to be paid: $%.2f%n", i + 1, interestList.get(i),
                    totalToBePaid);
        }
    }

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