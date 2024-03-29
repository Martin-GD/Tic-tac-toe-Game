package application.controller;

import application.action.Action;
import application.action.move;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller2 implements Initializable {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = 2;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;

    public ArrayList<Action> actions = new ArrayList<>();



    @FXML
    private Pane base_square;

    @FXML
    private Rectangle game_panel;

    private static boolean TURN = false;

    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ClientHandler clientHandler = new ClientHandler("");

        new Thread(() -> {
            try {
                while (true) {
                    Action re = clientHandler.getActions();
                    if (re != null) {
//                        System.out.println("change");

                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                chessBoard[i][j] = re.getBoard()[i][j];
                                flag[i][j] = re.getFlag()[i][j];
                            }
                        }
                        TURN = re.getTurn();
                        drawChess();
                        if (!re.getStatus().equals("going")) {
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Information Dialog");
                                alert.setHeaderText("Game Over");
//                            alert.setContentText();
                                if (re.getStatus().equals("1"))
                                    alert.setContentText("Circle win");
//                                System.out.println("Circle win");
                                else if (re.getStatus().equals("2"))
                                    alert.setContentText("Line win");
//                                System.out.println("Line win");
                                else alert.setContentText("Draw");
                                alert.showAndWait();
                                clientHandler.closeClient();

                            });
                            break;
                        }
                    }
                    Thread.sleep(1);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        game_panel.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / BOUND);
            int y = (int) (event.getY() / BOUND);
            if (refreshBoard(x, y) && TURN == clientHandler.getMyTurn()) {
//                TURN = !TURN;
                Action action = new move("move",chessBoard,TURN,flag);
//                System.out.println("send : "+action);
                clientHandler.send(action);

            }
        });
    }

    private boolean refreshBoard (int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
//            drawChess();
            return true;
        }
        return false;
    }

    private void drawChess () {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j);
                        break;
                    case PLAY_2:
                        drawLine(i, j);
                        break;
                    case EMPTY:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle (int i, int j) {
        Platform.runLater(() -> {
            Circle circle = new Circle();
            base_square.getChildren().add(circle);
            circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
            circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
            circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
            circle.setStroke(Color.RED);
            circle.setFill(Color.TRANSPARENT);
            flag[i][j] = true;
        });

    }

    private void drawLine (int i, int j) {
        Platform.runLater(() -> {
            Line line_a = new Line();
            Line line_b = new Line();
            base_square.getChildren().add(line_a);
            base_square.getChildren().add(line_b);
            line_a.setStartX(i * BOUND + OFFSET * 1.5);
            line_a.setStartY(j * BOUND + OFFSET * 1.5);
            line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
            line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
            line_a.setStroke(Color.BLUE);

            line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
            line_b.setStartY(j * BOUND + OFFSET * 1.5);
            line_b.setEndX(i * BOUND + OFFSET * 1.5);
            line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
            line_b.setStroke(Color.BLUE);
            flag[i][j] = true;
        });

    }
}
