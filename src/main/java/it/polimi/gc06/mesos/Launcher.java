package it.polimi.gc06.mesos;

import it.polimi.gc06.mesos.view.GUI;
import it.polimi.gc06.mesos.view.tui.TuiView;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--cli")) {
            TuiView.start();
        } else if (args.length > 0 && args[0].equals("--gui")) {
            Application.launch(GUI.class, args);
        }
    }
}
