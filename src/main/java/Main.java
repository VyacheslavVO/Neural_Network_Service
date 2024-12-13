/*
    Основная точка входа приложения
 */
import com.server.Server;

import java.io.IOException;

/**
 * @author VO
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server(3345);
        (new Thread(server)).start();
    }
}