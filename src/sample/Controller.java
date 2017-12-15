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
import java.util.ArrayList;
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

    TranslateTransition [] sendPackets;
    Button [] sentPackets ;
    Button [] sendBuffer;
    Button [] reciveBuffer ;
    TranslateTransition[] ackMoving;
    Button [] acks ;
    TranslateTransition windowSizeMove ;
    final int base=2;
    final int packetWidth=78;
    ArrayList<Integer> killedPackets=new ArrayList<Integer>();
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
    public void start(ActionEvent event)
    {

        init();



    }
    public void init()
    {
        framesNumber=Math.pow(base,Integer.valueOf(numberOfBits.getText()));
        windowWidth =Integer.valueOf(windowSize.getText());
        sendPackets= new TranslateTransition[(int) framesNumber];
        sentPackets = new Button[(int) framesNumber];
        sendBuffer = new Button[(int)framesNumber];
        reciveBuffer = new Button[(int) framesNumber];
        acks = new Button[(int) framesNumber];
        ackMoving= new TranslateTransition[(int) framesNumber];
        windowSizeMove = new TranslateTransition();

        packets.getChildren().addAll(createSendBuffer((int) framesNumber));
        createWindowSize(packetWidth,windowWidth);
        reciverHBox.getChildren().addAll(createReciverBuffer((int) framesNumber));


        transimitedPackets.getChildren().addAll(createTransimitedPackets((int) framesNumber));
    }
    public void stop(ActionEvent event)
    {

    }


    private Button[] createSendBuffer(int packetsNumber)
    {

        Paint paint = Paint.valueOf("#6495ED");
       for (int i=0;i<packetsNumber;i++)
       {
           sendBuffer[i] = new Button("Frame "+i);
           sendBuffer[i].setMinWidth(70);
           sendBuffer[i].setMaxWidth(70);
           sendBuffer[i].setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
           sendBuffer[i].setStyle("-fx-text-fill: white");

       }

        return sendBuffer;
    }
    private Button[] createReciverBuffer(int packetsNumber)
    {

        Paint paint = Paint.valueOf("#6495ED");
        for (int i=0;i<packetsNumber;i++)
        {
            reciveBuffer[i] = new Button("Frame "+i);
            reciveBuffer[i].setMinWidth(70);
            reciveBuffer[i].setMaxWidth(70);
            reciveBuffer[i].setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
            reciveBuffer[i].setStyle("-fx-text-fill: white");
        }

        return reciveBuffer;
    }
    private Button[] createTransimitedPackets(int packetsNumber)
    {
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
                    killPackets(sentPackets[x],x);
                }
            });
            sentPackets[i].setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
            sentPackets[i].setStyle("-fx-text-fill: white");
            this.sendPackets[i]=new TranslateTransition();
            this.sendPackets[i].setDuration(Duration.seconds(5));
            this.sendPackets[i].setToX(0);
            this.sendPackets[i].setToY(265);
            this.sendPackets[i].setAutoReverse(true);
            this.sendPackets[i].setCycleCount(1);
            this.sendPackets[i].setNode(sentPackets[i]);
            this.sendPackets[i].play();
            sendPackets[i].setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event)
                {
                    responseBuffer.getChildren().addAll(responseAck((int) framesNumber));
                }
            });

        }

     return sentPackets;
    }
    private void killPackets(Button btn,int index)
    {
        btn.setVisible(false);
        killedPackets.add(index);

    }
    private Button[] responseAck(int numberOfPackets)
    {

        Paint paint = Paint.valueOf("#329932");
        for (int i=0;i<numberOfPackets;i++)
        {
            acks[i] = new Button("RR "+i);
            acks[i].setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
            acks[i].setStyle("-fx-text-fill: white");
            acks[i].setPrefWidth(60);
            acks[i].setMaxWidth(70);
            acks[i].setMinWidth(70);

            if (killedPackets.contains(i))
                acks[i].setVisible(false);

            ackMoving[i]=new TranslateTransition();
            ackMoving[i].setDuration(Duration.seconds(5));
            ackMoving[i].setToX(0);
            ackMoving[i].setToY(-265);
            ackMoving[i].setAutoReverse(true);
            ackMoving[i].setCycleCount(1);
            ackMoving[i].setNode(acks[i]);
            ackMoving[i].play();
            ackMoving[i].setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    moveWindowSize(packetWidth,windowWidth);
                }
            });
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

        windowSizeMove.setDuration(Duration.seconds(3));
        windowSizeMove.setToX(frameWidth*numberOfFramesToMove+5);
        windowSizeMove.setToY(0);
        windowSizeMove.setAutoReverse(true);
        windowSizeMove.setCycleCount(1);
        windowSizeMove.setNode(rectangle);
        windowSizeMove.play();

        windowSizeMove.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
                transimitedPackets.getChildren().addAll(createTransimitedPackets((int) framesNumber));
            }
        });
    }

}
