module com.sigeter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires org.json;
    requires javafx.web;
    requires javafx.graphics;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.sigeter to javafx.fxml;
    opens com.sigeter.controller to javafx.fxml;
    opens com.sigeter.model to javafx.base;

    exports com.sigeter;
}
