package com.example.connect4game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jdk.nashorn.internal.runtime.ECMAErrors;

public class Main extends Application {
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridpane = loader.load();
        controller = loader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane = (Pane)rootGridpane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridpane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu(){
        //File Menu
        Menu fileMenu = new Menu("File");


        //Contents fo FileMenu
        MenuItem newGame = new MenuItem("New game");

        //Setting Action to newGame
        newGame.setOnAction(event -> controller.resetGame());
        MenuItem resetGame = new MenuItem("Reset game");

        //Setting action to resetGame
        resetGame.setOnAction(event -> controller.resetGame());
        SeparatorMenuItem separator = new SeparatorMenuItem();

        MenuItem exitGame = new MenuItem("Quit game");

        //Setting action to exitGame
        exitGame.setOnAction(event -> exitGame());

        //Adding MenuItem in the FileMenu
        fileMenu.getItems().addAll(newGame,resetGame,separator,exitGame);

        //helpMenu
        Menu helpMenu = new Menu("Help");

        MenuItem aboutGame = new MenuItem("About Connect4");
        aboutGame.setOnAction(event -> aboutConnect4());
        separator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGame, separator, aboutMe);


        //Adding FileMenu to the MenuBar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);

        return menuBar;
    }

    private void resetGame(){

    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About The Developer");
        alert.setHeaderText("Sahil Khubchandani");
        alert.setContentText("I love to play with codes and create games."+
                "Connect4 is one of them. In free time "+
                "I like to spend time with nears and dears.");
        alert.show();
    }

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How to Play?");
        alert.setContentText("Connect Four is a two-player connection game in which the" +
                "players first choose a color and then take turns dropping colored discs" +
                "from the top into a seven-column, six-row vertically suspended grid." +
                "The pieces fall straight down, occupying the next available space within the column." +
                "The objective of the game is to be the first to form a horizontal, vertical, " +
                "or diagonal line of four of one's own discs. Connect Four is a solved game." +
                "The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
