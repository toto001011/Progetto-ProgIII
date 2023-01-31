/*module com.example.esercizigui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.esercizigui to javafx.fxml;
    exports com.example.esercizigui;
    exports com.example.esercizigui.model;
    opens com.example.esercizigui.model to javafx.fxml;
    exports com.example.esercizigui.model.main;
    opens com.example.esercizigui.model.main to javafx.fxml;
}*/

module com.example.fx {
    requires javafx.controls;
    requires javafx.fxml;



    opens com.example.fx.main to javafx.fxml;
    exports com.example.fx.main;

    opens com.example.fx.model to javafx.fxml;
    exports com.example.fx.model;

    opens com.example.fx.ui to javafx.fxml;
    exports com.example.fx.ui;

}