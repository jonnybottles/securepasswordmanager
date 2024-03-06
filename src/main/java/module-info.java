module com.jgm.securepasswordmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jgm.securepasswordmanager to javafx.fxml;
    exports com.jgm.securepasswordmanager;
    exports com.jgm.securepasswordmanager.controllers;
    opens com.jgm.securepasswordmanager.controllers to javafx.fxml;
}