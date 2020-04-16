package bio;

import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.MappedByteBuffer;
import java.util.Scanner;

/**
 * Date: 2020/4/12
 **/
public class Server {
    private static Scanner scanner = new Scanner(System.in);
    private static String message;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);//1
        Socket clientSocket = serverSocket.accept();             //2
        BufferedReader in = new BufferedReader(                     //3
                new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter out =
                new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println(in.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        do {
            message = scanner.next();
            out.write(message);
            out.newLine();
            out.flush();
        } while (!in.equals("shutdown"));
    }
}


