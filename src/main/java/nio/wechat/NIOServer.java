package nio.wechat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NIOServer {
    private static Map<String, SocketChannel> clientMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        //创建服务器端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置非阻塞
        serverSocketChannel.configureBlocking(false);
        //绑定监听端口
        serverSocketChannel.socket().bind(new InetSocketAddress(8899));
        //获取Selector
        Selector selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            try {
                //select()是一个阻塞式的方式，如果当前没有任何channel连接，则一直处于等待状态
                //知道出现有channel连接时才会释放，即下面代码才会执行
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                selectionKeys.forEach(selectionKey -> {
                    final SocketChannel client;
                    try {
                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) selectionKey.channel();
                            client = serverSocketChannel1.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                            String key = "[" + UUID.randomUUID().toString() + "]";
                            clientMap.put(key, client);
                        } else if (selectionKey.isReadable()) {
                            client = (SocketChannel) selectionKey.channel();
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                            int count = client.read(readBuffer);
                            if (count > 0) {
                                readBuffer.flip();
                                Charset charset = Charset.forName("utf-8");
                                String reciveString = String.valueOf(charset.decode(readBuffer).array());
                                System.out.println(client + ":" + reciveString);
                                String senderKey = null;
                                for (Map.Entry<String, SocketChannel> entry : clientMap.entrySet()) {
                                    if (client == entry.getValue()) {
                                        senderKey = entry.getKey();
                                        break;
                                    }
                                }
                                for (Map.Entry<String, SocketChannel> entry : clientMap.entrySet()) {
                                    SocketChannel socketChannel = entry.getValue();
                                    ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                                    writeBuffer.put((senderKey + ":" + reciveString).getBytes());

                                    writeBuffer.flip();
                                    socketChannel.write(writeBuffer);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                //将key清空，相当于Itertor一次性remove
                selectionKeys.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

