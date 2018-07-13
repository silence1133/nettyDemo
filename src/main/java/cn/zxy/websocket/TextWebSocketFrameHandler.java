package cn.zxy.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * @author Silence 000996
 * @data 18/7/12
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //握手成功
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            //移除处理http的handler
            ctx.pipeline().remove(HttpRequestHandler.class);

            //通知所有已经连接的客户端已经连接上了
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            group.add(ctx.channel());//将把这个新 Channel 加入到该 ChannelGroup 中
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 如果接收到了 TextWebSocketFrame 消息 ，TextWebSocketFrameHandler 将调用
     TextWebSocketFrame 消息上的 retain()方法，并使用 writeAndFlush()方法来将它传
     输给 ChannelGroup，以便所有已经连接的 WebSocket Channel 都将接收到它
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        TextWebSocketFrame textWebSocketFrame = msg.retain();
        System.out.println(">>>>"+textWebSocketFrame.text());
        group.writeAndFlush(textWebSocketFrame);
    }
}
