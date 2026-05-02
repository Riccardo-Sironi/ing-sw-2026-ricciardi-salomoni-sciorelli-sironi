module it.polimi.gc06.mesos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.compiler;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.sql;
    requires org.jline;

    opens it.polimi.gc06.mesos to javafx.fxml;
    exports it.polimi.gc06.mesos.model;
    opens it.polimi.gc06.mesos.model to javafx.fxml, com.fasterxml.jackson.databind;
    exports it.polimi.gc06.mesos.model.cards;
    opens it.polimi.gc06.mesos.model.cards to javafx.fxml, com.fasterxml.jackson.databind;
    exports it.polimi.gc06.mesos.model.gameTurnManager;
    opens it.polimi.gc06.mesos.model.gameTurnManager to javafx.fxml, org.mockito, com.fasterxml.jackson.databind;
    exports it.polimi.gc06.mesos.model.cards.events;
    opens it.polimi.gc06.mesos.model.cards.events to javafx.fxml, com.fasterxml.jackson.databind;
    exports it.polimi.gc06.mesos.model.cards.characters;
    opens it.polimi.gc06.mesos.model.cards.characters to javafx.fxml, com.fasterxml.jackson.databind;
    exports it.polimi.gc06.mesos.model.cards.buildings;
    opens it.polimi.gc06.mesos.model.cards.buildings to javafx.fxml, com.fasterxml.jackson.databind;
    exports it.polimi.gc06.mesos.model.gameBoard;
    opens it.polimi.gc06.mesos.model.gameBoard to javafx.fxml, com.fasterxml.jackson.databind;
    exports it.polimi.gc06.mesos.gameExceptions;
    exports it.polimi.gc06.mesos.model.InstancesManager;
    opens it.polimi.gc06.mesos.model.InstancesManager to javafx.fxml, com.fasterxml.jackson.databind;
    exports it.polimi.gc06.mesos;
    opens it.polimi.gc06.mesos.view to javafx.graphics, javafx.fxml;
    exports it.polimi.gc06.mesos.view;
    opens it.polimi.gc06.mesos.view.viewControllers to javafx.fxml;
    exports it.polimi.gc06.mesos.network.rmi to java.rmi;
}