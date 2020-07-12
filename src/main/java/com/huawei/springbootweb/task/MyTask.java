package com.huawei.springbootweb.task;

import com.huawei.springbootweb.netty.performance.MillionUdpHandler;
import com.huawei.springbootweb.netty.performance.MillionUdpHandlerCopy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MyTask {

    @PostConstruct
    public void test() {
        ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
        scheduled.scheduleAtFixedRate(() -> {
            System.out.println("epoll" + MillionUdpHandler.COUNT.get());
            System.out.println("epoll" + MillionUdpHandler.list.toString());
            System.out.println("NIO" + MillionUdpHandlerCopy.COUNT.get());
            System.out.println("NIO" + MillionUdpHandlerCopy.list.toString());
        }, 0, 10, TimeUnit.SECONDS);
    }
}
