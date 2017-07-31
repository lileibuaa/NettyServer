package com.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lei on 28/07/2017.
 * Yes, you can.
 */
public class NIOServer {

    public static void main(String[] args) {
        new NIOServer().start();
    }

    public void start() {
        try (Selector selector = Selector.open(); ServerSocketChannel channel = ServerSocketChannel.open()) {
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(10191), 1024);
            channel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();
                if (keySet.isEmpty()) {
                    continue;
                }
                Iterator<SelectionKey> keyIterator = keySet.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    keyIterator.remove();
                    try {
                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel tmpServerChannel = (ServerSocketChannel) selectionKey.channel();
                            SocketChannel tmpChannel = tmpServerChannel.accept();
                            tmpChannel.configureBlocking(false);
                            tmpChannel.register(selector, SelectionKey.OP_READ);
                        }
                        if (selectionKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            int readL = socketChannel.read(byteBuffer);
                            if (readL > 0) {
                                byteBuffer.clear();
                                byteBuffer.put(LocalDateTime.now().format(
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")).getBytes());
                                byteBuffer.flip();
                                socketChannel.write(byteBuffer);
                            }
//                            socketChannel.close();
                        }
                    } catch (Exception ex) {
                        selectionKey.cancel();
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
