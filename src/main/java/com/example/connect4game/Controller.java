package com.example.connect4game;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;

    private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];

    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane insertedDiscsPane;
    @FXML
    public Label playerNameLabel;
    @FXML
    public TextField playerOneTextField,playerTwoTextField;
    @FXML
    public Button setNamesBTN;

    private boolean isAllowedToInsert = true;

    public void createPlayground(){
        Shape rectanglesWithHoles = createGamesStructuralGrid();
        rootGridPane.add(rectanglesWithHoles,0,1);
        List<Rectangle> rectangleList = createClickableColumns();
        for (Rectangle rectangle:rectangleList) {
            rootGridPane.add(rectangle,0,1);
        }

        setNamesBTN.setOnAction(event -> {
            PLAYER_ONE = playerOneTextField.getText();
            PLAYER_TWO = playerTwoTextField.getText();
            playerNameLabel.setText(PLAYER_ONE);
        });

    }

    private Shape createGamesStructuralGrid(){
        Shape rectanglesWithHoles = new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
        for (int row=0; row<ROWS; row++){
            for (int col=0; col<COLUMNS; col++){
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER/2);
                circle.setTranslateX(CIRCLE_DIAMETER/2);
                circle.setTranslateY(CIRCLE_DIAMETER/2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER+5) + 50 + 12);
                circle.setTranslateY(row * (CIRCLE_DIAMETER+5) + 50 + 17);

                rectanglesWithHoles = Shape.subtract(rectanglesWithHoles,circle);
            }
        }
        rectanglesWithHoles.setFill(Color.DIMGREY);
        return rectanglesWithHoles;
    }

    private List<Rectangle> createClickableColumns(){
        List<Rectangle> rectangleList = new ArrayList();

        for (int col=0; col<COLUMNS; col++) {
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER+5) +CIRCLE_DIAMETER / 4);
            rectangle.setOnMouseEntered(event->rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;
            rectangle.setOnMouseClicked(event -> {
                if (isAllowedToInsert) {
                    isAllowedToInsert = false;
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }
            });

            rectangleList.add(rectangle);
        }
        return rectangleList;
    }

    private void insertDisc(Disc disc, int column){

        int row = ROWS - 1;
        while(row >= 0){
            if(getDiscIfPresented(row,column) == null) {
                break;
            }
            row--;
        }

        if (row<0){
            return;
        }

        insertedDiscArray[row][column] = disc;
        insertedDiscsPane.getChildren().add(disc);

        int currentRow = row;
        disc.setTranslateX(column* (CIRCLE_DIAMETER+5) +CIRCLE_DIAMETER / 4 + 2);
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER / 4 + 6.95);
        translateTransition.setOnFinished(event -> {

            isAllowedToInsert = true;
            if (gameEnded(currentRow,column)){
                gameOver();
            }

            isPlayerOneTurn =! isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);
        });

        translateTransition.play();
    }

    private boolean gameEnded(int row, int column){

        //Vertical points
        List<Point2D> verticalPoints = IntStream.rangeClosed(row-3, row+3).mapToObj(r -> new Point2D(r,column)).collect(Collectors.toList());


        //Horizontal points
        List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3, column+3).mapToObj(c -> new Point2D(row,c)).collect(Collectors.toList());


        //Diagonal points
        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonal1Point = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint1.add(i, -i))
                .collect(Collectors.toList());


        //Diagonal points
        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Point = IntStream.rangeClosed(0,6).mapToObj(i -> startPoint2.add(i, i)).collect(Collectors.toList());
        boolean isEnded = checkCombination(verticalPoints) || checkCombination(horizontalPoints) ||checkCombination(diagonal1Point) || checkCombination(diagonal2Point);
        return isEnded;
    }
    private boolean checkCombination(List<Point2D> points) {
        int chain = 0;

        for (Point2D point: points) {
            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresented(rowIndexForArray,columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {
                chain++;
                if (chain == 4) {
                    return true;
                }
            }else{
                chain = 0;
            }
        }
        return false;
    }

    private Disc getDiscIfPresented(int row, int column){
        if (row >= ROWS || row<0 || column>=COLUMNS || column<0){
            return null;
        }
        return insertedDiscArray[row][column];
    }

    private void gameOver(){
        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
        System.out.println("Winner is:" + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("The winner is " + winner);
        alert.setContentText("Want to play again?");

        ButtonType yesBTN = new ButtonType("Yes");
        ButtonType noBTN = new ButtonType("No,Exit");
        alert.getButtonTypes().setAll(yesBTN,noBTN);

        Platform.runLater(() -> {

            Optional<ButtonType> btnClicked = alert.showAndWait();
            if (btnClicked.isPresent() && btnClicked.get() == yesBTN){
                resetGame();
            }else {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        insertedDiscsPane.getChildren().clear();
        for (int row=0; row<insertedDiscArray.length;row++){
            for (int column=0; column<insertedDiscArray.length; column++){
                insertedDiscArray[row][column] = null;
            }
        }
        isPlayerOneTurn = true;
        playerOneTextField.setText(""); //reset the name of the player one
        playerTwoTextField.setText(""); //reset the name of the player two
        PLAYER_ONE = "Player One";      //initializing default name
        PLAYER_TWO = "Player Two";      //initializing default name
        playerNameLabel.setText(PLAYER_ONE);

        createPlayground();
    }

    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove= isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER/2);
            setFill(isPlayerOneMove?Color.valueOf(discColor1):Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
