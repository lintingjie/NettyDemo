package chapter2.aio;


/**
 * @description
 * @auther lintingjie
 * @date 2018/9/1 16:28
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8080;

        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHadndler-001")
                .start();
    }


}
