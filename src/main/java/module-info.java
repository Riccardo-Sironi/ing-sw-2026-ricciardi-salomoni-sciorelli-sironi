module it.polimi.gc06.mesos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens it.polimi.gc06.mesos to javafx.fxml;
    exports it.polimi.gc06.mesos;
}