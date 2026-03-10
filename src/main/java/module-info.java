module it.polimi.gc06.mesos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens it.polimi.gc06.mesos to javafx.fxml;
    exports it.polimi.gc06.mesos;
    exports it.polimi.gc06.mesos.Model;
    opens it.polimi.gc06.mesos.Model to javafx.fxml;
}