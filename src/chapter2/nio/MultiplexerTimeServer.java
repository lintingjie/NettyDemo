package chapter2.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

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

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {
        while(!stop){
            try {
                selector.select(1000);
                // 轮循准备就绪的Key
                Set selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                while(it.hasNext()){
                    key = it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    }catch (Exception e){
                        if(key!= null){
                            key.cancel();
                            if(key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
        if(selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            // 处理新接入的请求消息
            if(key.isAcceptable()){
                // Accept the new connection
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                // Add the new connection to the selector
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
            // Read the data
            if(key.isReadable()){
               SocketChannel socketChannel = (SocketChannel) key.channel();
               ByteBuffer readBuffer = ByteBuffer.allocate(1024);
               int readBytes = socketChannel.read(readBuffer);
               if(readBytes>0){
                   //将缓冲区当前的limit设置为position,position设置为0,用于后续对缓冲区的读取操作
                   readBuffer.flip();
                   byte[] bytes = new byte[readBuffer.remaining()];
                   readBuffer.get(bytes);
                   String body = new String(bytes, "UTF-8");
                   System.out.println("The time server receive order:"+body);
                   String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?
                           new Date(System.currentTimeMillis()).toString():"BAD ORDER";
                   doWrite(socketChannel, currentTime);
               }else if(readBytes<0){
                   //对链路关闭
                   key.cancel();
                   socketChannel.close();
               }else {
                   // 读到0字节，忽略
               }

            }
        }
    }

    private void doWrite(SocketChannel socketChannel, String response) throws IOException {
        if(response!=null && response.trim().length()>0){
            byte[] bytes = response.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        }
    }
}
