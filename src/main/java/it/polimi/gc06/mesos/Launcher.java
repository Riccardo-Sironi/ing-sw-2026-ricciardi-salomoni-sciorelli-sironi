package it.polimi.gc06.mesos;

import it.polimi.gc06.mesos.view.tui.TuiView;

public class Launcher {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--cli")) {
            TuiView.start();
        }
    }
}
