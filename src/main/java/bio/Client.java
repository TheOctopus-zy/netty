package bio;

import sun.nio.cs.ext.MS874;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Date: 2020/4/16
 **/
public class Client {

    private static Scanner scanner = new Scanner(System.in);

    private static String message;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader in = new BufferedReader(                     //3
                new InputStreamReader(socket.getInputStream()));
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
            writer.write(message);
            //这里需要加上换行
            writer.newLine();
            writer.flush();
        } while (!in.equals("shutdown"));
    }

}
