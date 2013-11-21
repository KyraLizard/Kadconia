package de.dhbw.serverstatus;

/**
 * Created by Mark on 21.11.13.
 */

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerPortOpenChecker {

    private static final int timeout = 200;

    public static boolean isServerPortOpen(String ip, int port) throws InterruptedException, ExecutionException{
        ExecutorService es = Executors.newFixedThreadPool(20);
        Future<Boolean> future = portIsOpen(es, ip, port, timeout);
        es.shutdown();
        return future.get();
    }

    private static Future<Boolean> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout)
    {
        return es.submit(new Callable<Boolean>()
        {
            @Override
            public Boolean call()
            {
                try
                {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    return true;
                }
                catch (Exception ex)
                {
                    return false;
                }
            }
        });
    }
}

