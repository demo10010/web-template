package com.huawei.springbootweb.netty;


import com.huawei.springbootweb.netty.compare.NettyDemoHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*预热次数，间隔时间*/
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
/*实际测试次数，间隔1s测试一次*/
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
public class IoCompareTest {
    private static ExecutorService executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());//与主机CPU核心数2倍一致(intel超线程技术)

    private static final int SERVER_SOCKET_BACKLOG = 128;

    private static volatile AtomicInteger count = new AtomicInteger(0);

    /**
     * 必须是public
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void testSocket() throws Exception {
        Socket socket = new Socket("localhost", 6001);
        //创建一个socket绑定的端口和地址为：9977，本机。
        OutputStream os = socket.getOutputStream();
        //发送数据
        String str = "This TCP,im comming";
        os.write(str.getBytes());
        //释放
        socket.close();
    }

//    @Benchmark
//    @BenchmarkMode(Mode.AverageTime)
//    @OutputTimeUnit(TimeUnit.MICROSECONDS)
//    public void testHashMapWithoutSize() throws Exception {
//        EventLoopGroup group = new NioEventLoopGroup();
//        Bootstrap bootstrap = new Bootstrap();
//        bootstrap.group(group)
//                .channel(NioSocketChannel.class)
//                .remoteAddress(new InetSocketAddress("127.0.0.1", 6001))
//                .handler(new NettyDemoHandler());
//        ChannelFuture channelFuture = bootstrap.connect().sync();
//        channelFuture.channel().closeFuture().sync();
//    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IoCompareTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }


}
