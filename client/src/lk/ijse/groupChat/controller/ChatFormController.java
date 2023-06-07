package lk.ijse.groupChat.controller;


import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ChatFormController {
    public Label lblUserName;
    public VBox vBox;
    public String userName;
    public ImageView toBeSentImg;
    public FontAwesomeIconView msgCancelBtn;

    public Socket socket;
    public DataInputStream dataInputStream;
    public DataOutputStream dataOutputStream;

    public File file;
    public ScrollPane scrollPane;
    public FontAwesomeIconView fileAttached;
    public TextField txtMessage;
    public FontAwesomeIconView cancelBtn;
    String type;

    public void initialize(){
        new Thread(() ->{
            try{
               socket = new Socket("localhost",3310);
               dataInputStream = new DataInputStream(socket.getInputStream());
               dataOutputStream= new DataOutputStream(socket.getOutputStream());


               while (socket.isConnected()){
                   type=dataInputStream.readUTF();
                   if (type.equalsIgnoreCase("text")){
                       setText();
                   }else {
                       setFile();
                   }
               }
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();

            }catch (IOException e){
                e.printStackTrace();
                throw  new RuntimeException(e);
            }
        }).start();
        toBeSentImg.setVisible(false);
        cancelBtn.setVisible(true);

        vBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scrollPane.setVvalue((Double) newValue);
            }
        });


    }

    private void setFile() {
        try{
            String userName = dataInputStream.readUTF();
            byte[] sizeArray = new byte[4];
            dataInputStream.read(sizeArray);
            int size = ByteBuffer.wrap(sizeArray).asIntBuffer().get();

            byte[] imgArray = new byte[size];
            dataInputStream.read(imgArray);

            HBox hBox = new HBox();
            if (userName.equalsIgnoreCase(this.userName)){
                hBox.setAlignment(Pos.CENTER_RIGHT);
            }else {
                hBox.setAlignment(Pos.CENTER_LEFT);
            }

            hBox.setPadding(new Insets(5,5,5,10));
            Image image = new Image(new ByteArrayInputStream(imgArray));
            ImageView imageView = new ImageView(image);

            hBox.getChildren().add(imageView);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBox.getChildren().add(hBox);
                }
            });

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void setText() {
        try{
            String userName = dataInputStream.readUTF();
            String message = dataInputStream.readUTF();
            if (!message.isEmpty()){
                HBox hBox = new HBox();
                Text text=new Text(message);
                TextFlow textFlow=new TextFlow(text);

                if (userName.equalsIgnoreCase(this.userName)){
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5,5,5,10));

                    textFlow.setStyle("-fx-color : rgb(239, 242, 255);" +
                            "-fx-background-color: rgb(15, 125, 242);" +
                            "-fx-background-radius: 20px");

                    text.setFill(Color.color(0.934, 0.945, 0.996));
                }else {
                    text.setText(userName + " : " + message);

                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" +
                            "-fx-background-radius: 20px");
                }
                text.setStyle("-fx-font-size: 20px;");
                textFlow.setPadding(new Insets(5, 10, 8, 10));
                hBox.getChildren().add(textFlow);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        vBox.getChildren().add(hBox);
                    }
                });
            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    public void sendMessageOnAction(MouseEvent mouseEvent) {
        try{
            if (!txtMessage.getText().isEmpty()){
                dataOutputStream.writeUTF("text".trim());
                dataOutputStream.writeUTF(this.userName.trim());
                dataOutputStream.writeUTF(txtMessage.getText().trim());
                dataOutputStream.flush();
                txtMessage.clear();
            }else if (null!= this.file){
                BufferedImage bufferedImage = ImageIO.read(this.file);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage,"png",byteArrayOutputStream);

                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

                dataOutputStream.writeUTF("picture".trim());
                dataOutputStream.writeUTF(this.userName.trim());
                dataOutputStream.write(size);
                dataOutputStream.write(byteArrayOutputStream.toByteArray());

                dataOutputStream.flush();
                txtMessage.setVisible(true);
                msgCancelBtn.setVisible(true);
                toBeSentImg.setVisible(false);
                file = null;

                System.out.println("Flushed: " + System.currentTimeMillis());

                System.out.println("Closing: " + System.currentTimeMillis());
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void attachedFileOnAction(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) lblUserName.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        this.file = fileChooser.showOpenDialog(stage);

        if (null != this.file){
            BufferedImage bufferedImage = ImageIO.read(this.file);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",byteArrayOutputStream);

            byte[] array = byteArrayOutputStream.toByteArray();
            Image image = new Image(new ByteArrayInputStream(array));
            toBeSentImg.setImage(image);
            toBeSentImg.setVisible(true);
            cancelBtn.setVisible(true);

        }

    }

    public void messageCancelBtn(MouseEvent mouseEvent) {
        txtMessage.clear();
        toBeSentImg.setImage(null);

    }

    public void setUserName(String userName){
         this.userName = userName;
         lblUserName.setText(this.userName);
    }
}
