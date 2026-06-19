package loansim;

/**
 * Abstract base class for loan simulators.
 *
 * <p>
 * Concrete simulators implement the lifecycle hooks used by the driver:
 * {@link #resetState()}, {@link #handleInput()} and {@link #runSimulation()}.
 */
public abstract class LoanSimulator {
    /** Reset internal simulator state to initial values before a run. */
    abstract void resetState();

    /** Read and validate user input required by the simulator. */
    abstract void handleInput();

    /** Execute the repayment simulation and produce results/output. */
    abstract void runSimulation();

    /**
     * Return the total interest paid computed by the simulator.
     *
     * @return total interest paid in dollars
     */
    public abstract double getTotalInterestPaid();

    /**
     * Return the original loan amount used to seed the simulation.
     *
     * @return original loan principal in dollars
     */
    public abstract double getOriginalLoanAmount();

    /**
     * Convert months to years and round to two decimal places.
     *
     * @param months number of months
     * @return equivalent years rounded to two decimal places
     */
    public static final double toYears(int months) { return Math.round(months / 12.0 * 100.0) / 100.0; }

    /** Run the standard simulator lifecycle: reset, input, then execute. */
    public final void run() {
        resetState();
        handleInput();
        runSimulation();
    }
}