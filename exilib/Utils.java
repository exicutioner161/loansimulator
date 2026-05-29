package exilib;

import java.util.Scanner;

/**
 * Utility helpers for parsing and validating user input.
 *
 * <p>
 * Provides static helper methods to check whether a string represents an
 * integer or double, to test whether a string is non-numeric, and to prompt for
 * validated input using a {@link java.util.Scanner}.
 *
 * <p>
 * This class is not instantiable.
 */
public final class Utils {
    /** Prevent instantiation of this utility class. */
    private Utils() {
        /* this utility class should not be instantiated */
    }

    /**
     * Returns {@code true} if the given string can be parsed as an {@link Integer}.
     *
     * @param in the string to test; must not be {@code null}
     * @return {@code true} if {@code in} represents a valid integer, {@code false}
     *         otherwise
     * @throws NullPointerException if {@code in} is {@code null}
     */
    public static boolean isInt(String in) {
        try {
            Integer.valueOf(in);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the given string can be parsed as a {@link Double}.
     *
     * @param in the string to test; must not be {@code null}
     * @return {@code true} if {@code in} represents a valid double, {@code false}
     *         otherwise
     * @throws NullPointerException if {@code in} is {@code null}
     */
    public static boolean isDouble(String in) {
        try {
            Double.valueOf(in);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the input is non-empty and is not a number (neither
     * {@link #isInt(String)} nor {@link #isDouble(String)}).
     *
     * @param in the string to test; must not be {@code null}
     * @return {@code true} when {@code in} is non-empty and not numeric
     * @throws NullPointerException if {@code in} is {@code null}
     */
    public static boolean isNotNum(String in) { return !in.isEmpty() && !isInt(in) && !isDouble(in); }

    /**
     * Prompt the user with {@code inputMessage} until a valid double is entered.
     *
     * @param input        the {@link Scanner} to read user input from; must not be
     *                     {@code null}
     * @param inputMessage the prompt message printed to standard output
     * @return the parsed {@code double} entered by the user
     * @throws NullPointerException if {@code input} is {@code null}
     */
    public static double validateUserDoubleInput(Scanner input, String inputMessage) {
        String in;
        do {
            System.out.print(inputMessage);
            in = input.nextLine().trim();
        } while (!isDouble(in));
        return Double.parseDouble(in);
    }

    /**
     * Prompt the user with {@code inputMessage} until a valid integer is entered.
     *
     * @param input        the {@link Scanner} to read user input from; must not be
     *                     {@code null}
     * @param inputMessage the prompt message printed to standard output
     * @return the parsed {@code int} entered by the user
     * @throws NullPointerException if {@code input} is {@code null}
     */
    public static int validateUserIntInput(Scanner input, String inputMessage) {
        String in;
        do {
            System.out.print(inputMessage);
            in = input.nextLine().trim();
        } while (!isInt(in));
        return Integer.parseInt(in);
    }

    /**
     * Prompt the user with {@code inputMessage} until a non-numeric string is
     * entered.
     *
     * @param input        the {@link Scanner} to read user input from; must not be
     *                     {@code null}
     * @param inputMessage the prompt message printed to standard output
     * @return the validated non-numeric string entered by the user
     * @throws NullPointerException if {@code input} is {@code null}
     */
    public static String validateUserStringInput(Scanner input, String inputMessage) {
        String in;
        do {
            System.out.print(inputMessage);
            in = input.nextLine().trim();
        } while (!isNotNum(in));
        return in;
    }
}