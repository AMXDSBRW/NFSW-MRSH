package nfsw.rsh.util;

public class ConsoleColors {

    public static final String RESET = "\u001B[0m";
    public static final String DARK_PURPLE = "\u001B[38;5;55m";
    public static final String BLUE = "\u001B[38;5;33m";
    public static final String CYAN = "\u001B[38;5;39m";
    public static final String MAGENTA = "\u001B[38;5;125m";
    public static final String WHITE = "\u001B[38;5;15m";
    public static final String BACKGROUND_PURPLE = "\u001B[48;5;55m";
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINED = "\u001B[4m";
    public static final String REVERSED = "\u001B[7m";
    public static final String SUCCESS_COLOR = "\u001B[38;5;82m";
    public static final String ERROR_COLOR = "\u001B[38;5;160m";
    public static final String INFO_COLOR = "\u001B[38;5;33m";
    public static final String WARNING_COLOR = "\u001B[38;5;214m";
    public static final String DEFAULT_BACKGROUND = BACKGROUND_PURPLE;
    public static String success(String message) {
        return DEFAULT_BACKGROUND + SUCCESS_COLOR + BOLD + "[ERFLOG] " + message + RESET;
    }
    public static String error(String message) {
        return DEFAULT_BACKGROUND + ERROR_COLOR + BOLD + UNDERLINED + "[FEHLER] " + message + RESET;
    }
    public static String info(String message) {
        return DEFAULT_BACKGROUND + INFO_COLOR + "[INFO] " + message + RESET;
    }
    public static String warning(String message) {
        return DEFAULT_BACKGROUND + WARNING_COLOR + "[WARNUNG] " + message + RESET;
    }
    public static String purple(String message) {
        return DEFAULT_BACKGROUND + DARK_PURPLE + "[LILA] " + message + RESET;
    }
    public static String formatted(String message, String color, String style) {
        return DEFAULT_BACKGROUND + color + style + message + RESET;
    }
    public static String white(String message) {
        return DEFAULT_BACKGROUND + WHITE + "" + message + RESET;
    }
    public static String bold(String message) {
        return DEFAULT_BACKGROUND + BOLD + message + RESET;
    }
    public static String underlined(String message) {
        return DEFAULT_BACKGROUND + UNDERLINED + message + RESET;
    }
    public static String reversed(String message) {
        return DEFAULT_BACKGROUND + REVERSED + message + RESET;
    }
}
