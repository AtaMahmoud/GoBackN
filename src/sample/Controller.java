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
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable
{
    @FXML
    private TextField numberOfBits;

    double framesNumber;

    @FXML
    private TextField windowSize;

    int windowWidth ;
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

    TranslateTransition [] sentPackets;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
    public void start(ActionEvent event)
    {
        int base=2;
        framesNumber=Math.pow(base,Integer.valueOf(numberOfBits.getText()));
        windowWidth =Integer.valueOf(windowSize.getText());
        packets.getChildren().addAll(createTransimitedPackets((int) framesNumber));
        transimitedPackets.getChildren().addAll(createSenderAndReciverBuffer((int) framesNumber));
        createWindowSize(78,windowWidth);
        moveWindowSize(78,windowWidth);
        reciverHBox.getChildren().addAll(createSenderAndReciverBuffer((int) framesNumber));

        sentPackets[0].setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                responseBuffer.getChildren().addAll(responseAck((int) framesNumber));
            }
        });
    }
    public void stop(ActionEvent event)
    {

    }


    private Button[] createSenderAndReciverBuffer(int packetsNumber)
    {
        Button [] sendAndReciveBuffers = new Button[packetsNumber];
        Paint paint = Paint.valueOf("#6495ED");
       for (int i=0;i<packetsNumber;i++)
       {
           sendAndReciveBuffers[i] = new Button("Frame "+i);
           sendAndReciveBuffers[i].setMinWidth(70);
           sendAndReciveBuffers[i].setMaxWidth(70);
           sendAndReciveBuffers[i].setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
           sendAndReciveBuffers[i].setStyle("-fx-text-fill: white");

       }

        return sendAndReciveBuffers;
    }
    private Button[] createTransimitedPackets(int packetsNumber)
    {
        sentPackets = new TranslateTransition[packetsNumber];
        Button [] sentPackets = new Button[packetsNumber];
        Paint paint = Paint.valueOf("#083F87");
        for (int i=0;i<packetsNumber;i++)
        {
            final int x=i;
            sentPackets[i] = new Button("Frame "+i);
            sentPackets[i].setPrefWidth(70);
            sentPackets[i].setMaxWidth(70);
            sentPackets[i].setMinWidth(70);
            sentPackets[i].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    killPackets(sentPackets[x]);
                }
            });
            sentPackets[i].setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
            sentPackets[i].setStyle("-fx-text-fill: white");
            this.sentPackets[i]=new TranslateTransition();
            this.sentPackets[i].setDuration(Duration.seconds(5));
            this.sentPackets[i].setToX(0);
            this.sentPackets[i].setToY(265);
            this.sentPackets[i].setAutoReverse(true);
            this.sentPackets[i].setCycleCount(1);
            this.sentPackets[i].setNode(sentPackets[i]);
            this.sentPackets[i].play();

        }

     return sentPackets;
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
    private void createWindowSize(int frameWidth, int window)
    {
        //Window Size Creating
        rectangle.setWidth(frameWidth*window);
        rectangle.setHeight(40);
        rectangle.setStroke(Color.RED);

    }

    private void moveWindowSize(int frameWidth,int numberOfFramesToMove)
    {
        TranslateTransition windowSizeMove = new TranslateTransition();
        windowSizeMove.setDuration(Duration.seconds(3));
        windowSizeMove.setToX(frameWidth*numberOfFramesToMove+5);
        windowSizeMove.setToY(0);
        windowSizeMove.setAutoReverse(true);
        windowSizeMove.setCycleCount(1);
        windowSizeMove.setNode(rectangle);
        windowSizeMove.play();
    }

}
