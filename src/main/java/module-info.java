module com.jgm.securepasswordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    // Add ZXing modules here
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires org.apache.commons.codec;
    requires totp;
//    requires org.commons.codec;


    opens com.jgm.securepasswordmanager to javafx.fxml;
    opens com.jgm.securepasswordmanager.datamodel to com.google.gson;
    exports com.jgm.securepasswordmanager.datamodel;

    exports com.jgm.securepasswordmanager;
    exports com.jgm.securepasswordmanager.controllers;
    opens com.jgm.securepasswordmanager.controllers to javafx.fxml;
}