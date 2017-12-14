package sample;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable
{
    @FXML
    private TextField framesNumber;

    @FXML
    private TextField windowSize;

    @FXML
    private TextField timeOut;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private Rectangle rectangle;

    @FXML
    private HBox packets;

    @FXML
    private HBox timeLine;

    @FXML
    private HBox reciverHBox;

    @FXML
    private HBox transimitedPackets;

    @FXML
    private HBox responseBuffer;

    boolean startResponse=false;
    TranslateTransition []  transition1;

    @FXML
    private Text ptn;
    public void start(ActionEvent event)
    {

        packets.getChildren().addAll(createTransimitedPackets(10));
        transimitedPackets.getChildren().addAll(createSenderAndReciverBuffer(10));
        setWindowSize(72,2);
        reciverHBox.getChildren().addAll(createSenderAndReciverBuffer(10));
        transition1[0].setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                responseBuffer.getChildren().addAll(responseAck(10));
            }
        });





    }
    public void stop(ActionEvent event)
    {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
    private Button[] createSenderAndReciverBuffer(int numberOfPacates)
    {
        Button [] rectangles = new Button[numberOfPacates];
        Paint paint = Paint.valueOf("#6495ED");
       for (int i=0;i<numberOfPacates;i++)
       {
           rectangles[i] = new Button("Frame "+i);
           rectangles[i].setMinWidth(70);
           rectangles[i].setMaxWidth(70);
           rectangles[i].setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
           rectangles[i].setStyle("-fx-text-fill: white");

       }

        return rectangles;
    }
    private Button[] createTransimitedPackets(int packetsNumber)
    {
        transition1 = new TranslateTransition[packetsNumber];
        Button [] rectangles1 = new Button[packetsNumber];
        Paint paint = Paint.valueOf("#083F87");
        for (int i=0;i<packetsNumber;i++)
        {
            final int x=i;
            rectangles1[i] = new Button("Frame "+i);
            rectangles1[i].setPrefWidth(70);
            rectangles1[i].setMaxWidth(70);
            rectangles1[i].setMinWidth(70);
            rectangles1[i].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    killPackets(rectangles1[x]);
                }
            });
            rectangles1[i].setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
            rectangles1[i].setStyle("-fx-text-fill: white");
            transition1[i]=new TranslateTransition();
            transition1[i].setDuration(Duration.seconds(5));
            transition1[i].setToX(0);
            transition1[i].setToY(265);
            transition1[i].setAutoReverse(true);
            transition1[i].setCycleCount(1);
            transition1[i].setNode(rectangles1[i]);
            transition1[i].play();

        }
        startResponse=true;
     return rectangles1;
    }
    private void killPackets(Button btn)
    {
        btn.setVisible(false);
    }
    private Button[] responseAck(int numberOfPackets)
    {
        TranslateTransition[] ackMoving= new TranslateTransition[numberOfPackets];
        Button [] acks = new Button[numberOfPackets];
        Paint paint = Paint.valueOf("#329932");
        for (int i=0;i<numberOfPackets;i++)
        {

            acks[i] = new Button("RR "+i);
            acks[i].setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
            acks[i].setStyle("-fx-text-fill: white");
            acks[i].setPrefWidth(60);
            acks[i].setMaxWidth(70);
            acks[i].setMinWidth(70);
            ackMoving[i]=new TranslateTransition();
            ackMoving[i].setDuration(Duration.seconds(15));
            ackMoving[i].setToX(0);
            ackMoving[i].setToY(-265);
            ackMoving[i].setAutoReverse(true);
            ackMoving[i].setCycleCount(1);
            ackMoving[i].setNode(acks[i]);
            ackMoving[i].play();

        }

        return acks ;
    }
    private void setWindowSize(int frameWidth,int numberOfFramesToMove)
    {
        rectangle.setWidth(frameWidth*numberOfFramesToMove);
        rectangle.setHeight(40);
        rectangle.setStroke(Color.RED);
        TranslateTransition windowSizeMove = new TranslateTransition();
        windowSizeMove.setDuration(Duration.seconds(3));
        windowSizeMove.setToX(200);
        windowSizeMove.setToY(0);
        windowSizeMove.setAutoReverse(true);
        windowSizeMove.setCycleCount(1);
        windowSizeMove.setNode(rectangle);
        windowSizeMove.play();
    }
}
