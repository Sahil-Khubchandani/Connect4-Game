module com.example.connect4game {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.scripting.nashorn;


    opens com.example.connect4game to javafx.fxml;
    exports com.example.connect4game;
}