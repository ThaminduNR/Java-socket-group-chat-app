import lk.ijse.groupChat.controller.LocalSocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerInitializer {
    public static void main(String[] args) {
        List<LocalSocketHandler> localSocketHandlersList = new ArrayList<>();
        ServerSocket serverSocket;
        Socket socket;

        try{
            serverSocket = new ServerSocket(3310);
            while (!serverSocket.isClosed()){
                System.out.println("Server is Waiting");
                socket = serverSocket.accept();
                System.out.println("A New Client Has Login");

                LocalSocketHandler localSocketHandler = new LocalSocketHandler(socket,localSocketHandlersList);
                localSocketHandlersList.add(localSocketHandler);

                Thread thread = new Thread((Runnable) localSocketHandler);
                System.out.println("Thread Start");
                thread.start();

            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
