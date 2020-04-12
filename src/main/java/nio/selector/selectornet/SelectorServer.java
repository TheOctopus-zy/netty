package nio.selector.selectornet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务器端
 **/
public class SelectorServer {

    public static void main(String[] args) throws IOException {
        //创建ServerSocketChannel
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        //构建一个Selector对象
        Selector selector = Selector.open();
        //监听端口7000
        socketChannel.socket().bind(new InetSocketAddress(7000));
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //把ServerSocketChannel注册到Selector上
        //监听事件为OP_ACCEPT，客户端连接时触发
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //循环等待客户端连接
        while (true) {
            //等待一秒，如果没有事情，返回
            if (selector.select(1000) == 0) {
                System.out.println("服务器等待了一秒，无连接");
                continue;
            }
            //获取SelectionKey集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //遍历
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //如果是OP_ACCEPT，新客户端连接，生成一个SocketChannel
                if (key.isAcceptable()) {
                    SocketChannel accept = socketChannel.accept();
                    System.out.println("客户端新连接" + accept.hashCode());
                    //设置非阻塞
                    accept.configureBlocking(false);
                    //注册，设置事件为OP_READ，关联一个Buffer
                    accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }
                //发生OP_READ
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);
                    System.out.println("客户端信息：" + new String(buffer.array()));
                }

                //移除当前Selectkey，避免重复操作
                iterator.remove();
            }


        }


    }
}
