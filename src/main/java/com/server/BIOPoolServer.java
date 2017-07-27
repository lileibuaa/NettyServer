package com.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by lei on 27/07/2017.
 * Yes, you can.
 */
public class BIOPoolServer {

    private Executor executor;

    public BIOPoolServer() {
        this.executor = Executors.newFixedThreadPool(100);
    }

    public static void main(String[] args) {
        new BIOPoolServer().start(10191);
    }

    private void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executor.execute(new CustomRunnable(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class CustomRunnable implements Runnable {

        Socket socket;

        CustomRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                bufferedWriter.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
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
