import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Ceated by tooyi on 20/7/8 19:29
 * <p>
 * 实现一个server大概分为
 * 创建一个bossGroup和workGroup，可以先理解为两个线程池
 * bossGroup设置一个线程，用于请求连接请求和建立请求
 * workGroup在连接之后处理IO请求
 */
public class HttpServer {

    public static void main(String[] args) {
        // netty的调度模块，默认提供了NioEventLoopGroup()和OioEventLoopGroup()等实现
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            // 启动辅助类，通过他可以很方便的创建一个Netty服务端
            ServerBootstrap b = new ServerBootstrap();
            // 初始化ServerBootstrap的线程组
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(8081).sync();
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}
