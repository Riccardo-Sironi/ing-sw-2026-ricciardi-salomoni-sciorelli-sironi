module it.polimi.gc06.mesos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.compiler;

    opens it.polimi.gc06.mesos to javafx.fxml;
    exports it.polimi.gc06.mesos;
    exports it.polimi.gc06.mesos.Model;
    opens it.polimi.gc06.mesos.Model to javafx.fxml;
    exports it.polimi.gc06.mesos.Model.Cards;
    opens it.polimi.gc06.mesos.Model.Cards to javafx.fxml;
    exports it.polimi.gc06.mesos.Model.GameTurnManager;
    opens it.polimi.gc06.mesos.Model.GameTurnManager to javafx.fxml;
    exports it.polimi.gc06.mesos.Model.Cards.Events;
    opens it.polimi.gc06.mesos.Model.Cards.Events to javafx.fxml;
    exports it.polimi.gc06.mesos.Model.Cards.Characters;
    opens it.polimi.gc06.mesos.Model.Cards.Characters to javafx.fxml;
    exports it.polimi.gc06.mesos.Model.Cards.Buildings;
    opens it.polimi.gc06.mesos.Model.Cards.Buildings to javafx.fxml;
    exports it.polimi.gc06.mesos.Model.GameBoard;
    opens it.polimi.gc06.mesos.Model.GameBoard to javafx.fxml;
}