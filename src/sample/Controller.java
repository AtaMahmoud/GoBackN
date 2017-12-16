package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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
import java.util.*;


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

    /**
     *
     *
     * Sender Fileds
     *
     *
     * */
    ArrayList<TranslateTransition> sendPackets;
    ArrayList<Button> sentPackets ;
    Button [] sendBuffer;
    int senderStartIndex;
    int senderMaxIndex;

    /**
     *
     * Reciver Fileds
     *
     * */
    Button [] reciveBuffer ;

    /**
     * Acknowlage Fileds
     * */
    ArrayList<TranslateTransition> ackMoving;
    ArrayList<Button> acks ;
    int recervierStartIndex;
    int reciverMaxIndex;
    /**
     *
     * window Size Fileds
     *
     *
     * */
    TranslateTransition windowSizeMove ;
    final int base=2;
    final int packetWidth=78;
    /**
     *
     * Killed Packets
     *
     * */
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
        sendPackets= new ArrayList<TranslateTransition>();
        sentPackets = new ArrayList<Button>();
        sendBuffer = new Button[(int)framesNumber];
        reciveBuffer = new Button[(int) framesNumber];
        acks = new ArrayList<Button>();
        ackMoving= new ArrayList<TranslateTransition>();
        windowSizeMove = new TranslateTransition();

        senderStartIndex=0;
        senderMaxIndex=windowWidth;

        recervierStartIndex=0;
        senderMaxIndex=windowWidth;

        packets.getChildren().addAll(createSendBuffer((int) framesNumber));
        createWindowSize(packetWidth,windowWidth);
        reciverHBox.getChildren().addAll(createReciverBuffer((int) framesNumber));


        transimitedPackets.getChildren().addAll(createTransimitedPackets((int) framesNumber));

        Timer timer = new Timer("MyTimer");
        TimerTask timerTask=new TimerTask()
        {
            @Override
            public void run()
            {   Platform.runLater(new Runnable() {
                @Override
                public void run()
                {

                }
            });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 5000, 5000);
    }
    public void stop(ActionEvent event)
    {

    }

    /**
     *
     *
     * Snder Methods
     *
     * */
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
    private List<Button> createTransimitedPackets(int start,int max)
    {
        Paint paint = Paint.valueOf("#083F87");
        for (int i=start;i<max;i++)
        {
            final int x=i;
            sentPackets.add(new Button("Frame "+i));
            sentPackets.get(i).setPrefWidth(70);
            sentPackets.get(i).setMaxWidth(70);
            sentPackets.get(i).setMinWidth(70);
            sentPackets.get(i).setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event)
                {
                    killPackets(sentPackets.get(x),x);
                }
            });
            sentPackets.get(i).setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
            sentPackets.get(i).setStyle("-fx-text-fill: white");
            this.sendPackets.add(new TranslateTransition());
            this.sendPackets.get(i).setDuration(Duration.seconds(5));
            this.sendPackets.get(i).setToX(0);
            this.sendPackets.get(i).setToY(265);
            this.sendPackets.get(i).setAutoReverse(true);
            this.sendPackets.get(i).setCycleCount(1);
            this.sendPackets.get(i).setNode(sentPackets.get(i));
            this.sendPackets.get(i).play();
            sendPackets.get(i).setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event)
                {
                    responseBuffer.getChildren().addAll(responseAck((int) framesNumber));
                }
            });

        }

        return sentPackets.subList(senderStartIndex,senderMaxIndex);
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

    private void killPackets(Button btn,int index)
    {
        btn.setVisible(false);
        killedPackets.add(index);

    }
    private List<Button> responseAck(int numberOfPackets)
    {

        Paint paint = Paint.valueOf("#329932");
        for (int i=0;i<numberOfPackets;i++)
        {
            acks.add(new Button("RR "+i)) ;
            acks.get(i).setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
            acks.get(i).setStyle("-fx-text-fill: white");
            acks.get(i).setPrefWidth(60);
            acks.get(i).setMaxWidth(70);
            acks.get(i).setMinWidth(70);

            if (killedPackets.contains(i))
                acks.get(i).setVisible(false);

            ackMoving.add(new TranslateTransition());
            ackMoving.get(i).setDuration(Duration.seconds(5));
            ackMoving.get(i).setToX(0);
            ackMoving.get(i).setToY(-265);
            ackMoving.get(i).setAutoReverse(true);
            ackMoving.get(i).setCycleCount(1);
            ackMoving.get(i).setNode(acks.get(i));
            ackMoving.get(i).play();
            ackMoving.get(i).setOnFinished(new EventHandler<ActionEvent>() {
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
