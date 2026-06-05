package loansim;

public abstract class LoanSimulator {
    abstract void resetState();

    abstract void handleInput();

    abstract void runSimulation();

    public abstract double getTotalInterestPaid();

    public abstract double getOriginalLoanAmount();

    public static final double roundCurrency(double amount) { return Math.round(amount * 100.0) / 100.0; }

    public static final double toYears(int months) { return Math.round(months / 12.0 * 100.0) / 100.0; }

    public final void run() {
        resetState();
        handleInput();
        runSimulation();
    }
}