package it.polimi.gc06.mesos.view.tui;

/**
 * Interface that represents a renderer for the Text-based User Interface.
 * It provides a method to render an object into an array of string rows and
 * a helper method to center string text within a specified width.
 *
 * @param <T> the type of object to render
 */
public interface TuiRenderer<T> {
    /**
     * Renders an item of type T into an array of string rows.
     *
     * @param item the item to render
     * @return an array of strings representing the item's ASCII layout
     */
    String[] render(T item);

    /**
     * Helper method to center text within a fixed character width.
     * Handles null objects, excessive length, and applies uniform spacing properly.
     *
     * @param text  the string to be centered
     * @param width the total character width to assume for centering
     * @return the constrained, padded, and centered string
     */
    default String centerText(String text, int width) {
        // If no text was input, set it to an empty string
        if (text == null) text = "";
        // if the text is larger than the card width, clamp it to the expected width
        if (text.length() >= width) return text.substring(0, width);

        // Calculate the padding from the left, and padding from the right
        int padLeft = (width - text.length()) / 2;
        int padRight = width - text.length() - padLeft;
        // Return the centered text
        return " ".repeat(padLeft) + text + " ".repeat(padRight);
    }

}
