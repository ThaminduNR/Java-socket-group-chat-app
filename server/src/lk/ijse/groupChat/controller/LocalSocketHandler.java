package lk.ijse.groupChat.controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;

public class LocalSocketHandler implements Runnable{

    public Socket socket;
    public List<LocalSocketHandler> localSocketHandlersList;
    public DataOutputStream dataOutputStream;
    public DataInputStream dataInputStream;
    public String type;

    public LocalSocketHandler(Socket socket,List<LocalSocketHandler> localSocketHandlersList){
        this.socket = socket;
        this.localSocketHandlersList = localSocketHandlersList;

    }


    @Override
    public void run() {
        try{
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            while (socket.isConnected()){
                type = dataInputStream.readUTF();
                if (type.equalsIgnoreCase("text")){
                    sendText();
                }else {
                    sendFile();
                }
            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void sendFile() {
        try {
            String userName = dataInputStream.readUTF();
            byte [] sizeArray = new byte[4];
            dataInputStream.read(sizeArray);
            int size = ByteBuffer.wrap(sizeArray).asIntBuffer().get();

            byte[] imgArray = new byte[size];
            dataInputStream.read(imgArray);


            for (LocalSocketHandler localSocketHandler: localSocketHandlersList) {
                localSocketHandler.dataOutputStream.writeUTF(type);
                localSocketHandler.dataOutputStream.writeUTF(userName);
                localSocketHandler.dataOutputStream.write(sizeArray);
                localSocketHandler.dataOutputStream.write(imgArray);
                localSocketHandler.dataOutputStream.flush();

            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void sendText() {
        try{
            String userName = dataInputStream.readUTF();
            String message = dataInputStream.readUTF();
            System.out.println(userName);
            System.out.println(message);


            for (LocalSocketHandler localSocketHandler: localSocketHandlersList) {
                localSocketHandler.dataOutputStream.writeUTF(type);
                localSocketHandler.dataOutputStream.writeUTF(userName);
                localSocketHandler.dataOutputStream.writeUTF(message);
                localSocketHandler.dataOutputStream.flush();

            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
