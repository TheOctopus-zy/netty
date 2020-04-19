package netty.examples.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import java.time.LocalDateTime;


public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
        group.add(ctx.channel());

    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //注意writeAndFlush需要传入TextWebSocketFrame对象封装文本，如果传入的只是一个String，不会返回出去
        group.writeAndFlush(new TextWebSocketFrame("服务器的时间：" + LocalDateTime.now() + "：" + msg.text()));
    }


    /**
     * web客户连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        /*
        id表示唯一，有长有短，长的asLongText，唯一。短的asShortText()不唯一
         */
        System.out.println("handlerAdded的ID：" + ctx.channel().id().asShortText());
        System.out.println("handlerAdded的ID：" + ctx.channel().id().asLongText());
    }

    /**
     * 连接断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved的ID：" + ctx.channel().id().asLongText());
    }

    /**
     * 发生异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭连接
        ctx.close();
    }
}
