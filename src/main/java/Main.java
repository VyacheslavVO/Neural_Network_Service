/*
    Основная точка входа приложения
 */
import com.mysql.MySQLHandler;
import com.server.CreateConnectionMySQL;
import com.server.Server;

import java.io.IOException;
import java.sql.SQLException;


/**
 * @author VO
 */
public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        // Подключаемся, создаём БД и таблицы
        MySQLHandler db = new CreateConnectionMySQL( "src/main/resources/templates/connection.properties" ).getDb();
        // Запускаем сервер
        Server server = new Server(db);
        (new Thread(server)).start();
    }
}