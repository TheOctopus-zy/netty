package nio.selector.selectornet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 客户端
 **/
public class SelectorClient {


    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //设置端口和IP地址
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 7000);
        //连接服务器
        if (!socketChannel.connect(inetSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("因为连接需要时间，客户端不会阻塞，可以做其他工作");
            }

        }
        //连接成功，发送数据
        String message = "HelloWorld";
        //使用wrap，匹配合适的字节数组大小
        ByteBuffer wrap = ByteBuffer.wrap(message.getBytes());
        socketChannel.write(wrap);
        //保持客户端不断开连接
        System.in.read();


    }
}
