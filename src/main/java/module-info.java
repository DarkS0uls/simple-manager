module com.sr.simplemanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.sr.simplemanager to javafx.fxml;
    exports com.sr.simplemanager;
}