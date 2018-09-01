package chapter2.nio;

/**
 * Created by lintingjie on 2018/9/1.
 */

public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeServer-001").start();
    }
}
