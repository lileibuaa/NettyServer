package com.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by lei on 27/07/2017.
 * Yes, you can.
 */
public class BIOServer {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(10191)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getRemoteSocketAddress().toString());
                new Thread(new CustomRunnable(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class CustomRunnable implements Runnable {

        Socket socket;

        CustomRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                LocalDateTime localDateTime = LocalDateTime.now();
                bufferedWriter.write(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
