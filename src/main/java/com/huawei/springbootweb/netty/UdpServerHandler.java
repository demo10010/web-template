package com.huawei.springbootweb.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final AtomicLong ATOMIC_LONG = new AtomicLong(0);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        System.out.println(ATOMIC_LONG.getAndIncrement());
        ByteBuf content = datagramPacket.content();
        byte[] bytes = new byte[4];
        content.getBytes(0, bytes);
        System.err.println(Arrays.toString(bytes));
//        System.out.println(content.readerIndex());
//        System.out.println(content.readBytes(4).readInt());
//        System.out.println(content.readerIndex());
//        System.out.println(content.readBytes(4).readInt());
//        System.out.println(content.readerIndex());
    }

    private static int scanPort = 2555;

    public static void main(String[] args) {
//        nettyServer();
        byte[] bytes = {1, 2, 3, 4, 5, 6, 8, 9, 9, 10};
        try {
            String s = new String(bytes, "utf-8");
            OutputStream os = new FileOutputStream(new File("E:\\redis\\bytes.txt"));
            os.write(s.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void nettyServer() {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new UdpServerHandler());
        try {
            bootstrap.bind(scanPort).sync().channel().closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
