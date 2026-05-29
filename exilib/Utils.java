package exilib;

import java.util.Scanner;

public final class Utils {
    private Utils() {
        /* this utility class should not be instantiated */
    }

    public static boolean isInt(String in) {
        try {
            Integer.valueOf(in);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }

    public static boolean isDouble(String in) {
        try {
            Double.valueOf(in);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }

    public static boolean isNotNum(String in) { return !in.isEmpty() && !isInt(in) && !isDouble(in); }

    public static double validateUserDoubleInput(Scanner input, String inputMessage) {
        String in;
        do {
            System.out.print(inputMessage);
            in = input.nextLine().trim();
        } while (!isDouble(in));
        return Double.parseDouble(in);
    }

    public static int validateUserIntInput(Scanner input, String inputMessage) {
        String in;
        do {
            System.out.print(inputMessage);
            in = input.nextLine().trim();
        } while (!isInt(in));
        return Integer.parseInt(in);
    }

    public static String validateUserStringInput(Scanner input, String inputMessage) {
        String in;
        do {
            System.out.print(inputMessage);
            in = input.nextLine().trim();
        } while (!isNotNum(in));
        return in;
    }
}