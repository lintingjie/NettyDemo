package chapter2.nio;

/**
 * NIO的TimeServer
 * Created by ltj on 18-8-28
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
    }
}
