package it.polimi.gc06.mesos.view.tui;

public class Style {
    public static final String RESET = "\u001B[0m";
    public static final String WHITE = "\u001B[37m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";

    public static String COLOR(int r, int g, int b) {
        return String.format("\033[38;2;%d;%d;%dm", r, g, b);
    }
}



