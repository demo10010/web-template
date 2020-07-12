package com.huawei.springbootweb.netty.performance;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MillionUdpHandlerCopy extends SimpleChannelInboundHandler<DatagramPacket> {

    public static volatile AtomicLong COUNT = new AtomicLong(0);
    public static volatile CopyOnWriteArraySet<String> list = new CopyOnWriteArraySet<>();

    public BlockingQueue<Map<String, Object>> queue = new LinkedBlockingQueue<>(990000);

    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        // 因为Netty对UDP进行了封装，所以接收到的是DatagramPacket对象。
        String result = msg.content().toString(CharsetUtil.UTF_8);
        Map<String, Object> getMap = new HashMap<>();
//        log.info(result);
        //处理数据
        queue.put(getMap);
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("messageReceived结果：", CharsetUtil.UTF_8), msg.sender()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        // 因为Netty对UDP进行了封装，所以接收到的是DatagramPacket对象。
        COUNT.getAndIncrement();
        String result = datagramPacket.content().toString(CharsetUtil.UTF_8);
        Map<String, Object> getMap = new HashMap<>();
        list.add(Thread.currentThread().getName());

        //处理数据
//        queue.put(getMap);
        channelHandlerContext.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("channelRead0 结果：", CharsetUtil.UTF_8), datagramPacket.sender()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
