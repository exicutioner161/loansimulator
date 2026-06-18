package loansim;

import java.util.Scanner;

/** Factory that constructs {@link LoanSimulator} instances by type. */
public class LoanSimulatorFactory {
    private LoanSimulatorFactory() {
        /* this utility class should not be instantiated */
    }

    /**
     * Create a {@link LoanSimulator} for the requested loan type.
     *
     * @param input scanner passed to simulator constructors
     * @param type  integer code identifying simulator type (1=student, 2=other)
     * @return a new LoanSimulator instance or {@code null} for unknown types
     */
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