package it.polimi.gc06.mesos.view.tui;

public interface TuiRenderer<T> {
    String[] render(T item);

    // Helper method to center text in a fixed width
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
