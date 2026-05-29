package loansim;

import java.util.Scanner;

public class LoanSimulatorFactory {
    private LoanSimulatorFactory() {
        /* this utility class should not be instantiated */
    }

    public static final LoanSimulator getSimulator(Scanner input, int type) {
        if (type == 1) {
            return new StudentLoanSimulator(input);
        }
        if (type == 2) {
            return new GenericLoanSimulator(input);
        }
        return null;
    }
}