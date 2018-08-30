package chapter2.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * 多路复用类，负责轮询多路复用器Selector
 *
 * Created by lintingjie on 2018/8/30.
 */

public class MultiplexerTimeServer implements Runnable{

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定监听端口
     *
     * @param port
     */
    public MultiplexerTimeServer(int port){
        try {
            //创建多路复用器
            selector = Selector.open();
            // 打开ServerSocketChannel,用于监听客户端的连接
            serverSocketChannel = ServerSocketChannel.open();
            //设置连接为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //绑定监听端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            //将ServerSocketChannel注册到多路复用器上，监听ACCETP事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server starts in port:"+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
