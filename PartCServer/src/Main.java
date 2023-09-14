import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        Boolean isConnected = false;
        try {
            ServerSocket ss = new ServerSocket(1234);
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = ss.accept();
                InetAddress clientAddress = clientSocket.getInetAddress();
                int clientPort = clientSocket.getPort();
                System.out.println("Client connected from IP: " + clientAddress.getHostAddress() + " on Port: " + clientPort);
                isConnected = true;
                // Create a new thread to handle communication with the client
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Wait for the client to send data before reading it
            String clientMessage = dataInputStream.readUTF();
            System.out.println("Received from client: " + clientMessage);
            String[] s = clientMessage.split(" ");
            String username = s[0];
            String pass = s[1];
          //  System.out.println(username);
          //  System.out.println(pass);
            int UserID =  VerifyUserCard(username,pass);
            System.out.println(UserID);
             if (UserID != 0){
                 dataOutputStream.writeUTF(String.valueOf(UserID));
                 dataOutputStream.flush();
             }
             while(clientSocket.isConnected()){
                 try {
                     getMessage();
                 }catch (Exception ex){
                     break;
                 }
               SendMessage();
             }
        } catch (IOException e) {
            // Handle client disconnection or errors here
            e.printStackTrace();
        } finally {
            try {
                // Close the streams and the socket
                dataInputStream.close();
                dataOutputStream.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public int VerifyUserCard(String userName, String Password){
        if(userName.equals("Client1") && Password.equals("1")){
            return 1;
        } else if (userName.equals("Client2") && Password.equals("2")) {
            return 2;
        }
        return 0;
    }
    public String getMessage() throws IOException {
     String clientMessage = dataInputStream.readUTF();
        System.out.println(clientMessage);
      return clientMessage;
    }
    public void SendMessage() throws IOException{
        dataOutputStream.writeUTF(String.valueOf(getMessage()));
        dataOutputStream.flush();
    }
}

