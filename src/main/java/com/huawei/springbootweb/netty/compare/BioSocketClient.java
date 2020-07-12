package com.huawei.springbootweb.netty.compare;

import java.io.InputStream;
import java.net.Socket;

public class BioSocketClient implements Runnable {

    private Socket client;

    public BioSocketClient(Socket client) {
        this.client = client;
    }

    public void run() {
        try {
            InputStream inputStream = this.client.getInputStream();
            int available = inputStream.available();
            byte[] buf = new byte[available];
            int cnt = inputStream.read(buf, 0, available);
            if (cnt > 0) {
                System.out.println("receive msg from client:" + new String(buf));
                Thread.sleep(200);
                this.client.getOutputStream().write(buf, 0, cnt);
            } else {
                System.out.println("return done");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
