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
    TimerTask senderTask;
    Button [] sendBuffer;
    int senderStartIndex;
    int senderMaxIndex;
    boolean flag=false;
    int timeout;

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
    ArrayList<Integer>killedAcks=new ArrayList<Integer>();
    ArrayList<Button> acks ;
    int recervierStartIndex;
    int reciverMaxIndex;
    TimerTask ackTask;
    /**
     *
     * window Size Fileds
     *
     *
     * */
    TranslateTransition windowSizeMove ;
    final int base=2;
    final int packetWidth=85;
    int windowSizerMover=1;

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
        VarIntialization();
        
        GBNTasks();
    }

    private void VarIntialization()
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
        reciverMaxIndex=windowWidth;

    }

    public void GBNTasks()
    {
        //Variable Intialization



        packets.getChildren().addAll(createSendBuffer((int) framesNumber));
        createWindowSize(packetWidth,windowWidth);
        reciverHBox.getChildren().addAll(createReciverBuffer((int) framesNumber));
        transimitedPackets.getChildren().addAll(startSender((int) framesNumber));
        long de = 0;
        long peroid = 6 * 1000;
        senderTask = new TimerTask() {
            @Override
            public void run()
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        System.out.println("Thread Start");

                        for (int i=senderStartIndex;i<senderMaxIndex;i++)
                        {
                            sendPackets.get(i).play();
                        }
                        sendPackets.get(senderMaxIndex-1).setOnFinished(new EventHandler<ActionEvent>()
                        {

                            @Override
                            public void handle(ActionEvent event)
                            {
                                System.out.println("Inside On Finish");

                                if(killedPackets.size()==windowWidth)
                                {
                                    System.out.println("inside if");
                                    senderMaxIndex-=windowWidth;
                                    senderStartIndex-=windowWidth;
                                    for (int j=senderStartIndex;j<senderMaxIndex;j++)
                                    {
                                        System.out.println("inside for");
                                        sentPackets.get(j).setTranslateY(0);
                                        sendPackets.get(j).getNode().setVisible(true);
                                    }
                                    killedPackets.clear();
                                    senderTask.run();



                                }
                                else if(killedPackets.size()>0&&killedPackets.size()<windowWidth)
                                {

                                    System.out.println("inside  else if");
                                    //ackTask.run();
                                    if (killedPackets.get(killedPackets.indexOf(Collections.min(killedPackets)))==0)
                                    {
                                        senderStartIndex=killedPackets.get(killedPackets.indexOf(Collections.min(killedPackets)));
                                        senderMaxIndex=windowWidth;
                                        for (int j=senderStartIndex;j<senderMaxIndex;j++)
                                        {
                                            System.out.println("inside for");
                                            sentPackets.get(j).setTranslateY(0);
                                            sendPackets.get(j).getNode().setVisible(true);
                                        }
                                        killedPackets.clear();
                                        senderTask.run();
                                    }
                                    else
                                    {
                                        senderStartIndex=killedPackets.get(killedPackets.indexOf(Collections.min(killedPackets)));
                                        senderMaxIndex = senderStartIndex+windowWidth;
                                        for (int j=senderStartIndex;j<senderMaxIndex;j++)
                                        {
                                            System.out.println("inside for");
                                            sentPackets.get(j).setTranslateY(0);
                                            sendPackets.get(j).getNode().setVisible(true);
                                        }
                                        killedPackets.clear();
                                        senderTask.run();
                                    }

                                }
                                else
                                {
                                    ackTask.run();
                                }


                            }


                        });

                       /* senderMaxIndex+=windowWidth;
                        senderStartIndex+=windowWidth;*/
                    }
                });
            }
        };

        /*Timer anotherTimer = new Timer();
        anotherTimer.scheduleAtFixedRate(senderTask, de,peroid);*/
        senderTask.run();




        ackTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        System.out.println("ACK Thread Start ");
                        ArrayList<Button>buttons=responseAck(recervierStartIndex,reciverMaxIndex);
                        for (int i=recervierStartIndex;i<reciverMaxIndex;i++)
                        {
                            responseBuffer.getChildren().add(buttons.get(i));
                            recervierStartIndex++;

                        }
                        reciverMaxIndex+=windowWidth;
                    }
                });

            }
        };

           /* Timer timer = new Timer();
            long delay = 0;
            long intevalPeriod = 6 * 1000;
            timer.scheduleAtFixedRate(ackTask, delay,intevalPeriod);*/
        flag=false;




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
    private ArrayList<Button> startSender(int framesNumber)
    {
        double delay=1;
        Paint paint = Paint.valueOf("#083F87");
        for (int i=0;i<framesNumber;i++)
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
            sendPackets.get(i).setDelay(new Duration(delay*1000));
            delay+=.25;

            }

        return sentPackets;
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
    private void killAcks(Button btn,int index)
    {
        btn.setVisible(false);
        killedAcks.add(index);

    }
    private ArrayList<Button> responseAck(int start,int max)
    {
        double delay=1;
        Paint paint = Paint.valueOf("#329932");
        for (int i=start;i<max;i++)
        {
            final int x=i;
            acks.add(new Button("RR "+i)) ;
            acks.get(i).setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
            acks.get(i).setStyle("-fx-text-fill: white");
            acks.get(i).setPrefWidth(60);
            acks.get(i).setMaxWidth(70);
            acks.get(i).setMinWidth(70);
            acks.get(i).setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    killAcks(acks.get(x),x);
                }
            });
            if (killedPackets.contains(i))
                acks.get(i).setVisible(false);

            ackMoving.add(new TranslateTransition());
            ackMoving.get(i).setDuration(Duration.seconds(5));
            ackMoving.get(i).setToX(0);
            ackMoving.get(i).setToY(-265);
            ackMoving.get(i).setAutoReverse(true);
            ackMoving.get(i).setCycleCount(1);
            ackMoving.get(i).setNode(acks.get(i));
            ackMoving.get(i).setDelay(new Duration(delay*1000));
            delay+=0.25;
            ackMoving.get(i).play();
            if (i==max-1)
            {
                ackMoving.get(i).setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event)
                    {
                        moveWindowSize(packetWidth, windowSizerMover);
                        windowSizerMover += windowWidth;
                    }
                });
            }
        }

        return acks;
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
                senderMaxIndex+=windowWidth;
                senderStartIndex+=windowWidth;
                senderTask.run();
            }
        });
    }

}
