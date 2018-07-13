package cn.zxy.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author Silence 000996
 * @data 18/7/12
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;
    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //将字节解码为 HttpRequest、HttpContent 和 LastHttpContent。并将HttpRequest、HttpContent 和 LastHttpContent编码为字节
        pipeline.addLast(new HttpServerCodec());
        //写入一个文件的内容
        pipeline.addLast(new ChunkedWriteHandler());
        //将一个 HttpMessage 和跟随它的多个 HttpContent 聚合为单个 FullHttpRequest 或者 FullHttpResponse（取决于它是被用来处理请求还是响应）。安装了这个之后，
        //ChannelPipeline 中的下一个 ChannelHandler 将只会 收到完整的 HTTP 请求或响应
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        //处理 FullHttpRequest（那些不发送到/ws URI 的请求）
        pipeline.addLast(new HttpRequestHandler("/ws"));
        //按照 WebSocket 规范的要求，处理 WebSocket 升级握手、PingWebSocketFrame 、 PongWebSocketFrame 和CloseWebSocketFrame
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //处理 TextWebSocketFrame 和握手完成事件
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }
}
