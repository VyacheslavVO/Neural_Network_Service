package com;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author VO
 *
 */
public class ThreadServer implements Runnable{

    private static final ExecutorService executeIt = Executors.newFixedThreadPool(2);

    static int serverPort;

    public ThreadServer (int port) {
        serverPort = port;
    }

    @Override
    public void run() {
        // стартуем сервер на порту 3345 и инициализируем переменную для обработки консольных команд с самого сервера
        try (ServerSocket server = new ServerSocket( serverPort )) {
            System.out.println( "Server socket created, listening on server port: " + server.getLocalPort() );

            // стартуем цикл при условии что серверный сокет не закрыт
            while (!server.isClosed()) {

                // становимся в ожидание
                // подключения к сокету общения под именем - "clientDialog" на
                // серверной стороне
                Socket client = server.accept();

                // после получения запроса на подключение сервер создаёт сокет
                // для общения с клиентом и отправляет его в отдельную нить
                // в Runnable(при необходимости можно создать Callable)
                // монопоточную нить = сервер - MonoThreadClientHandler и тот
                // продолжает общение от лица сервера
                executeIt.execute( new ThreadClientHandler( client, executeIt ) );
                System.out.println( "Connection accepted" );
            }

            // закрытие пула нитей после завершения работы всех нитей
            executeIt.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}