module it.polimi.gc06.mesos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.compiler;

    opens it.polimi.gc06.mesos to javafx.fxml;
    exports it.polimi.gc06.mesos;
    exports it.polimi.gc06.mesos.model;
    opens it.polimi.gc06.mesos.model to javafx.fxml;
    exports it.polimi.gc06.mesos.model.cards;
    opens it.polimi.gc06.mesos.model.cards to javafx.fxml;
    exports it.polimi.gc06.mesos.model.gameTurnManager;
    opens it.polimi.gc06.mesos.model.gameTurnManager to javafx.fxml;
    exports it.polimi.gc06.mesos.model.cards.events;
    opens it.polimi.gc06.mesos.model.cards.events to javafx.fxml;
    exports it.polimi.gc06.mesos.model.cards.characters;
    opens it.polimi.gc06.mesos.model.cards.characters to javafx.fxml;
    exports it.polimi.gc06.mesos.model.cards.buildings;
    opens it.polimi.gc06.mesos.model.cards.buildings to javafx.fxml;
    exports it.polimi.gc06.mesos.model.gameBoard;
    opens it.polimi.gc06.mesos.model.gameBoard to javafx.fxml;
    exports it.polimi.gc06.mesos.gameExceptions;
}