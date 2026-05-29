package exilib;

import java.util.Scanner;

public class Utils {
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

    public static double validateUserDoubleInput(Scanner input, String inputMessage) {
        String in;
        do {
            System.out.print(inputMessage);
            in = input.nextLine();
        } while (!isDouble(in));
        return Double.parseDouble(in);
    }

    public static double validateUserIntInput(Scanner input, String inputMessage) {
        String in;
        do {
            System.out.print(inputMessage);
            in = input.nextLine();
        } while (!isInt(in));
        return Integer.parseInt(in);
    }
}