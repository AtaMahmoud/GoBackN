package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.util.*;


public class Controller
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
    TimerTask movingWindowSize;
    final int base=2;
    final int packetWidth=85;
    int windowSizerMover=1;

    /**
     * Killed Packets
     * */
    ArrayList<Integer> killedPackets=new ArrayList<Integer>();

    /**
     *
     *Time Line Vars
     *
     */
    @FXML
    private Line senderLine;
    @FXML
    private Line reciverLine;
    @FXML
    private VBox timeLineContainer;
    int startPacketLine;
    int maxPacketLine;
    int startAcktLine;
    int maxAckLine;

    public void start(ActionEvent event)
    {
        VarIntialization();
        CreateUI();
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
    private void CreateUI()
    {
        packets.getChildren().addAll(createSendBuffer((int) framesNumber));
        createWindowSize(packetWidth,windowWidth);
        reciverHBox.getChildren().addAll(createReciverBuffer((int) framesNumber));
        transimitedPackets.getChildren().addAll(startSender((int) framesNumber));
        responseBuffer.getChildren().addAll(responseAck((int) framesNumber));
        senderLine.setVisible(true);
        reciverLine.setVisible(true);
    }
    public void GBNTasks()
    {

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
                                    for (int i=senderStartIndex;i<senderMaxIndex;i++)
                                    createPacketLine(i);
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
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        System.out.println("ACK Thread Start ");
                        for (int i=recervierStartIndex;i<reciverMaxIndex;i++)
                        {
                            ackMoving.get(i).play();
                        }
                        ackMoving.get(reciverMaxIndex-1).setOnFinished(new EventHandler<ActionEvent>()
                        {
                            @Override
                            public void handle(ActionEvent event)
                            {
                                if (killedAcks.size()==windowWidth)
                                {
                                    senderStartIndex=recervierStartIndex;
                                    senderMaxIndex=reciverMaxIndex;
                                    for (int i=recervierStartIndex;i<reciverMaxIndex;i++)
                                    {
                                        System.out.println("inside for of when Killed Ack == window width");
                                        acks.get(i).setTranslateY(0);
                                        ackMoving.get(i).getNode().setVisible(true);
                                        sentPackets.get(i).setTranslateY(0);
                                        sentPackets.get(i).setVisible(true);
                                    }
                                    killedAcks.clear();
                                    senderTask.run();
                                }
                               else if (!killedAcks.contains(reciverMaxIndex-1)&&killedAcks.size()<windowWidth&&killedAcks.size()!=0 )
                                {
                                    Paint paint = Paint.valueOf("#329932");
                                    for (int i=recervierStartIndex;i<reciverMaxIndex;i++)
                                    {
                                        System.out.println("Color Changing");
                                        sentPackets.get(i).setBackground(new Background(new BackgroundFill(paint, CornerRadii.EMPTY, Insets.EMPTY)));
                                    }
                                    windowSizerMover += reciverMaxIndex;
                                    movingWindowSize.run();

                                }
                                else if (killedAcks.size()>0&&killedAcks.size()<windowWidth)
                                {
                                    senderStartIndex=killedAcks.get(killedAcks.indexOf(Collections.min(killedAcks)));
                                    senderMaxIndex=senderStartIndex+windowWidth;
                                    for (int i=senderStartIndex;i<senderMaxIndex;i++)
                                    {
                                        System.out.println("Inside kill ack for");
                                        sentPackets.get(i).setTranslateY(0);
                                        sendPackets.get(i).getNode().setVisible(true);
                                    }
                                    killedAcks.clear();
                                    senderTask.run();
                                }
                                else
                                {
                                    System.out.println("I'm Here before acks For Time Line");
                                    for (int i=recervierStartIndex;i<reciverMaxIndex;i++)
                                         createAckLine(i);

                                    windowSizerMover += reciverMaxIndex;
                                    movingWindowSize.run();
                                }

                            }
                        });


                    }
                });

            }
        };
        movingWindowSize= new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        //TODo : Invoke WIndowSize Method Here
                        moveWindowSize(packetWidth,windowSizerMover);
                        windowSizeMove.play();
                        windowSizeMove.setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event)
                            {
                                senderMaxIndex+=windowWidth;
                                senderStartIndex+=windowWidth;
                                reciverMaxIndex+=windowWidth;
                                recervierStartIndex+=windowWidth;
                                senderTask.run();
                            }
                        });
                    }
                });
            }
        };
           /* Timer timer = new Timer();
            long delay = 0;
            long intevalPeriod = 6 * 1000;
            timer.scheduleAtFixedRate(ackTask, delay,intevalPeriod);*/

    }
    public void stop(ActionEvent event)
    {
        System.exit(0);
    }
    /**
     *Snder Methods
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
        killedPacketTimeLine(index);

    }
    private void killAcks(Button btn,int index)
    {
        btn.setVisible(false);
        killedAcks.add(index);
        killedAckTimeLine(index);

    }
    private ArrayList<Button> responseAck(int framesNumber)
    {
        double delay=1;
        Paint paint = Paint.valueOf("#329932");
        for (int i=0;i<framesNumber;i++)
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
        windowSizeMove.setToX((frameWidth*numberOfFramesToMove)+5);
        windowSizeMove.setToY(0);
        windowSizeMove.setAutoReverse(true);
        windowSizeMove.setCycleCount(1);
        windowSizeMove.setNode(rectangle);


    }
    /**
     * Time Line Part
     */
    private void createPacketLine(int start)
    {
            Label packet=new Label();
            packet.setPadding(new Insets(35,0,0,0));
            packet.setTextFill(Color.BLACK);
            packet.setStyle("-fx-font: 18 arial;");
            packet.setText("------------------------------------------------------------------------->Packet"+start);
            timeLineContainer.getChildren().add(packet);
    }
    private void createAckLine(int start)
    {

             Label ack=new Label();
             ack.setPadding(new Insets(35,0,0,0));
             ack.setText("Ack "+start+"<---------------------------------------------------------------------");
             ack.setTextFill(Color.BLACK);
             ack.setStyle("-fx-font: 18 arial;");
             timeLineContainer.getChildren().add(ack);

    }
    private void killedPacketTimeLine(int start)
    {
        Label KilledPacket=new Label();
        KilledPacket.setPadding(new Insets(35,0,0,0));
        KilledPacket.setTextFill(Color.BLACK);
        KilledPacket.setStyle("-fx-font: 18 arial;");
        KilledPacket.setText("-------------------------------------------------------------------------X Packet"+start);
        timeLineContainer.getChildren().add(KilledPacket);
    }
    private void killedAckTimeLine(int start)
    {
        Label KilledAck=new Label();
        KilledAck.setPadding(new Insets(35,0,0,0));
        KilledAck.setText("Ack "+start+" X---------------------------------------------------------------------");
        KilledAck.setTextFill(Color.BLACK);
        KilledAck.setStyle("-fx-font: 18 arial;");
        timeLineContainer.getChildren().add(KilledAck);
    }
}
