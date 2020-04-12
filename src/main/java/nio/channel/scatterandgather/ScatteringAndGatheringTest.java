package nio.channel.scatterandgather;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Scattering：将数据写入到buffer中，可以采用buffer数组，依次写入
 * Gathering：从buffer读数据时，可以采用buffer数组，依次读取
 **/
public class ScatteringAndGatheringTest {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(8899);
        serverSocketChannel.socket().bind(address);

        int messageLength = 2 + 3 + 5;

        ByteBuffer[] byteBuffers = new ByteBuffer[3];
        byteBuffers[0] = ByteBuffer.allocate(2);
        byteBuffers[1] = ByteBuffer.allocate(3);
        byteBuffers[2] = ByteBuffer.allocate(5);

        SocketChannel socketChannel = serverSocketChannel.accept();
        while (true) {
            int byteRead = 0;
            //接受客户端写入的的字符串
            while (byteRead < messageLength) {
                long r = socketChannel.read(byteBuffers);
                byteRead += r;
                System.out.println("byteRead:" + byteRead);
                //通过流打印
                Arrays.asList(byteBuffers).stream().
                        map(buffer -> "postiton:" + buffer.position() + ",limit:" + buffer.limit()).
                        forEach(System.out::println);

            }
            //将所有buffer都flip。
            Arrays.asList(byteBuffers).
                    forEach(buffer -> {
                        buffer.flip();
                    });
            //将数据读出回显到客户端
            long byteWrite = 0;
            while (byteWrite < messageLength) {
                long r = socketChannel.write(byteBuffers);
                byteWrite += r;
            }
            //将所有buffer都clear
            Arrays.asList(byteBuffers).
                    forEach(buffer -> {
                        buffer.clear();
                    });

            System.out.println("byteRead:" + byteRead + ",byteWrite:" + byteWrite + ",messageLength:" + messageLength);
        }
    }
}
