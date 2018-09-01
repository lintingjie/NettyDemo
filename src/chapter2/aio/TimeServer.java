package chapter2.aio;

import java.io.IOException;

/**
 * @Description: AIO时间服务器服务端
 * @Auther: lintingjie
 * @CreateTime: 2018/9/1 15:34
 */
public class TimeServer {


    public static void main(String[] args) throws IOException{
        int port = 8080;
        AsyncTimeServerHandler timeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(timeServerHandler, "AIO-AsyncTimeServerHandler-001").start();
    }
}
