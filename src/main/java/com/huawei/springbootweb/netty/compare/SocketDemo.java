package com.huawei.springbootweb.netty.compare;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketDemo {
    private static ExecutorService executorService = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());//与主机CPU核心数2倍一致(intel超线程技术)

    private static final int SERVER_SOCKET_BACKLOG = 128;

    private static volatile AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
//        ServerSocket serverSocket = new ServerSocket(6001);
//        while (true) {
//            Socket clientSocket = serverSocket.accept();
//            BioSocketClient bioSocketClient = new BioSocketClient(clientSocket);
//            System.out.println(count.addAndGet(1));
//            executorService.submit(bioSocketClient);
//        }
        long l = 1L * 1024L * 1024L * 1024L * 1024L;
        System.out.println(l);
        System.out.println("Long.MAX_VALUE:" + Long.MAX_VALUE);
        System.out.println(" Double.MAX_VALUE:" + Double.MAX_VALUE);
        System.out.println(Math.max(Long.MAX_VALUE, Double.MAX_VALUE));
    }
}
