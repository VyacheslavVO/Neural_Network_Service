import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;

public class ThreadClientHandler implements Runnable {

    private static Socket clientDialog;
    private static BufferedReader in;
    private static BufferedWriter out;
    static ExecutorService executeIt;

    public ThreadClientHandler(Socket client, ExecutorService execute) throws IOException {
        // сам сокет
        clientDialog = client;
        // инициируем каналы общения в сокете, для сервера
        // канал чтения из сокета
        in = new BufferedReader( new InputStreamReader( clientDialog.getInputStream() ) );
        // канал записи в сокет
        out = new BufferedWriter( new OutputStreamWriter( clientDialog.getOutputStream() ) );
        // ссылка на нить клиентов
        executeIt = execute;
    }

    @Override
    public void run() {
        String entry;
        try {
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // начинаем диалог с подключенным клиентом в цикле, пока сокет не
            // закрыт клиентом
            while (!clientDialog.isClosed()) {
                System.out.println("Server reading from channel");

                // серверная нить ждёт в канале чтения (inputStream) получения
                // данных клиента после получения данных считывает их
                entry = in.readLine();

                // и выводит в консоль
                System.out.println("READ from clientDialog message - " + entry);
                if (entry == null) {
                    // если клиент разорвал соединение на удалённой стороне
                    // серверной нити
                    System.out.println("The client has terminated the connection ...");
                    Thread.sleep(1000);
                    break;
                }
                // инициализация проверки условия продолжения работы с клиентом
                // по этому сокету по кодовому слову - exit в любом регистре
                else if (entry.equalsIgnoreCase("quit")) {
                    // если кодовое слово получено то инициализируется закрытие
                    // серверной нити
                    System.out.println("Client initialize connections suicide ...");
                    Thread.sleep(1000);
                    break;
                }

                // если условие окончания работы не верно - продолжаем работу -
                // отправляем эхо обратно клиенту
                System.out.println( "Server try writing to channel" );
                send( entry );
                System.out.println( "Server Wrote message to clientDialog." );

                // освобождаем буфер сетевых сообщений
                out.flush();
                // возвращаемся в началло для считывания нового сообщения
            }

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // основная рабочая часть //
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // если условие выхода - верно выключаем соединения
            System.out.println("Client disconnected");
            System.out.println("Closing connections & channels.");

            // закрываем сначала каналы сокета !
            in.close();
            out.close();

            // потом закрываем сокет общения с клиентом в нити моносервера
            clientDialog.close();

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void send(String msg) {
        try {
            out.write( msg + "\n" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}