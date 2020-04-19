package netty.examples.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

/**
 * @Author Badribbit
 * @create 2019/4/14 21:46
 */

/**
 * 基于webSocket的长连接的全双工的交互（真是强大，改变了以往多次请求的约束，现在是长连接了，而且支持全双工通信）
 * 客户端发送给服务端，服务端接收并能响应给客户端.客户端通过前端页面text.html来连接
 * 1.基于webSocket，ws协议.会出现一个101状态码，表示由http协议转成ws协议（协议升级）
 * 2.每次刷新后，新建一个连接，而不是以前那个连接
 * 3.不要以为连接就一定能关闭，在断网或者断电情况，是检测不出来是否断连接的。（所以前面要有心跳机制）
 */
public class MyServer {
    public static void main(String[] args) throws Exception {
        //事件循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class).
                    handler(new LoggingHandler(LogLevel.INFO)).
                    childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //因为这是基于http协议的，所以使用http编解码器
                            pipeline.addLast(new HttpServerCodec());
                            //以块的方式去写, 添加 ChunkedWrite处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            /**
                             *  特别重要，http数据在传输过程是分段的
                             *  HttpObjectAggregator,而他就是将多个段聚合起来。
                             *  所以，你有时会看到，当我们客户端发送数据量大时，会发出多次http请求
                             */
                            //设置传输中分段数据的聚合
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            //设置对应映射的WebSocket请求URL
                            //此时，URL的协议应该为ws://IP地址:端口/hello
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                            //添加自定义的信息处理handler
                            pipeline.addLast(new TextWebSocketFrameHandler(channelGroup));

                        }
                    });
            /**
             * 这里的端口绑定，和之前的端口直接绑定是一样的
             */
            ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(8888)).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
