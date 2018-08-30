package chapter2.bio2;

import chapter2.bio.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 伪异步IO的TimeServer
 * Created by ltj on 18-8-28
 */
public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("Time server starts at port:"+port);
            Socket socket = null;
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 1000);//创建IO线程任务池
            while(true){
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } finally {
            if(server!=null){
                server.close();
                System.out.println("Time server close");
                server = null;
            }
        }
    }
}
