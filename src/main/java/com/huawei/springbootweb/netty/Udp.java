package com.huawei.springbootweb.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class CLientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String body = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println("body" + body);
    }
}

public class Udp {
    private static int scanPort = 2555;

    private static byte[] CONTENT = {0, 2, 3, 3, 5};

    public static ExecutorService executorService = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        nettyClient();
    }

    private static void nettyClient() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioDatagramChannel.class)
                .handler(new CLientHandler());
        try {
            Channel ch = b.bind(0).sync().channel();

            ByteBuf byteBuf = Unpooled.copiedBuffer(CONTENT);
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", scanPort);

            for (int i = 0; i < 100; i++) {
                DatagramPacket datagramPacket = new DatagramPacket(
                        byteBuf, inetSocketAddress);
                ch.writeAndFlush(datagramPacket).sync();
                byteBuf.release();
            }
            ch.closeFuture().await();

        } catch (Exception ex) {
        }
    }
}
