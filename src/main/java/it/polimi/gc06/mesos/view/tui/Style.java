package it.polimi.gc06.mesos.view.tui;

/**
 * Utility class containing ANSI escape codes for styling console output.
 * Provides predefined basic colors and a method to generate true-color RGB escape sequences.
 */
public class Style {
    // Resets the terminal output to its default styling.
    public static final String RESET = "\u001B[0m";
    // ANSI escape code for basic white text.
    public static final String WHITE = "\u001B[37m";
    // ANSI escape code for basic red text.
    public static final String RED = "\u001B[31m";
    // ANSI escape code for basic green text.
    public static final String GREEN = "\u001B[32m";
    // ANSI escape code for basic yellow text.
    public static final String YELLOW = "\u001B[33m";
    // ANSI escape code for basic blue text.
    public static final String BLUE = "\u001B[34m";
    // ANSI escape code for basic purple text.
    public static final String PURPLE = "\u001B[35m";


    /**
     * Generates an ANSI escape code for a specific RGB true-color.
     *
     * @param r the red component (0-255)
     * @param g the green component (0-255)
     * @param b the blue component (0-255)
     * @return the ANSI escape sequence for the specified color
     */
    public static String COLOR(int r, int g, int b) {
        return String.format("\033[38;2;%d;%d;%dm", r, g, b);
    }
}
