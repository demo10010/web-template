package com.huawei.springbootweb.netty.performance;

import com.huawei.springbootweb.netty.UdpServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class NioUdpServer {
    int DEFAULT_PORT = 15209;
    int DEFAULT_PORT2 = 16209;
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @PostConstruct
    private void init() {
        epoll();
        nio();
    }

    private void epoll() {
        boolean available = Epoll.isAvailable();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 10)
                .handler(new MillionUdpHandler());
        EventLoopGroup workerGroup;

        if (available) {
            workerGroup = new EpollEventLoopGroup();
            bootstrap.group(workerGroup).channel(EpollDatagramChannel.class) // NioServerSocketChannel -> EpollDatagramChannel
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(EpollChannelOption.SO_REUSEPORT, true) // 配置EpollChannelOption.SO_REUSEPORT
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 10);
        } else {
            workerGroup = new NioEventLoopGroup();
            bootstrap.group(workerGroup).channel(NioDatagramChannel.class);
        }

        log.info("netty初始化开始！");
        executorService.submit(() -> {
            try {
                if (available) {
                    // linux系统下使用SO_REUSEPORT特性，使得多个线程绑定同一个端口
                    int cpuNum = Runtime.getRuntime().availableProcessors();
                    log.info("using epoll reuseport and cpu:" + cpuNum);
                    for (int i = 0; i < cpuNum; i++) {
                        ChannelFuture future = bootstrap.bind(DEFAULT_PORT).sync();
                        if (!future.isSuccess()) {
                            log.error("bootstrap bind fail port is {}", DEFAULT_PORT);
                        }
                    }
                } else {
                    bootstrap.bind(DEFAULT_PORT).sync().channel().closeFuture().sync();
                }
                log.info("netty初始化成功！");
            } catch (InterruptedException e) {
                e.printStackTrace();
                workerGroup.shutdownGracefully();
                log.info("shutdownGracefully ！");
            }
        });
    }


    private void nio() {
        boolean available = Epoll.isAvailable();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 10)
                .handler(new MillionUdpHandlerCopy());
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup).channel(NioDatagramChannel.class);
        try {
            log.info("DEFAULT_PORT2初始化成功！");
            bootstrap.bind(DEFAULT_PORT2).sync().channel().closeFuture().sync();
            log.info("DEFAULT_PORT2  END！");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
