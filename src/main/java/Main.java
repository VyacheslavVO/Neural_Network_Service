/*
    Основная точка входа приложения
 */
import com.mysql.MySQLHandler;
import com.server.CreateConnectionMySQL;
import com.server.Server;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;


/**
 * @author VO
 */
public class Main {

    public static void main(String[] args) throws IOException, SQLException {

        MySQLHandler db = new CreateConnectionMySQL( "src/main/resources/templates/connection.properties" ).getDb();
        // получим порт сервера из базы
        JSONArray server_data = db.executeQuery( "SELECT * FROM neural_network.tcp_server WHERE id = 1" );
        JSONObject data = new JSONObject(server_data.getJSONObject( 0 ).toString());

        Server server = new Server(data.getInt( "port" ));
        (new Thread(server)).start();
    }
}