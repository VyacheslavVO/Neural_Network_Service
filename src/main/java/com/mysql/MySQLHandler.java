/**
 * Модуль доступа к серверу СУБД MySQL
 */
package com.mysql;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Scanner;

public class MySQLHandler {

    private java.sql.Connection connection = null;
    protected String driver;                        // драйвер JDBC
    protected String url = null;                    // строка подключения

    private final String DATABASE_CREATE = "CREATE DATABASE IF NOT EXISTS %s " +
            "CHARACTER SET utf8 "               +
            "COLLATE utf8_general_ci "          ;
    private final String DROP_DATABASE   = "DROP DATABASE IF EXISTS %s";
    private final String TABLE_DROP   = "DROP TABLE IF EXISTS %s";

    private final String SELECT_SERVER_PARAM = "SELECT * FROM neural_network.tcp_server WHERE id = 1";


    public MySQLHandler(String driver) {
        this.driver = driver;
        try {
            // регистрация драйвера
            Class.forName( this.driver ).getDeclaredConstructor().newInstance();
            System.out.println("driver: " + this.driver);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Процедура определения строки подключения URL к серверу БД
     * @param host - имя компьютера
     * @param database - наименование БД
     * @param port - порт сервера
     */
    public void setURL (String host, String database, int port) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database +
                "?createDatabaseIfNotExist=true&" +
                "allowPublicKeyRetrieval=true&" +
                "useSSL=false&" +
                "useUnicode=true&" +
                "characterEncoding=UTF-8&" +
                "serverTimezone=UTC";
        System.out.println("url: " + url);
    };


    public Connection getConnection () {
        return this.connection;
    }


    public void Connect (String login, String password) {
        try {
            this.connection = DriverManager.getConnection(this.url, login, password);
        } catch (SQLException e) {
            this.connection = null;
            e.printStackTrace();
        }
    }

    /**
     * Процедура отключения от сервера БД
     */
    public void Disconnect () {
        try {
            this.connection.close();
        } catch (SQLException e) { e.printStackTrace(); }
    };


    public boolean createSchema (final String schema) {
        return execute (String.format(DATABASE_CREATE, schema));
    }


    public boolean dropSchema(final String schema) {
        return execute (String.format(DROP_DATABASE, schema));
    }

    /**
     * Функция выполнения SQL-запроса без получения данных
     * @param sql текст запроса
     * @return результат выполнения запроса
     */
    public boolean execute (final String sql) {
        boolean result = false;
        try {
            if (getConnection() != null) {
                Statement statement = getConnection().createStatement();
                statement.execute(sql);
                statement.close();
                result = true;
            }
        } catch (SQLException e) {
            System.err.println ("SQLException : code = " + String.valueOf(e.getErrorCode()) +
                    " - " + e.getMessage());
            System.err.println ("\tSQL : " + sql);
        }
        return result;
    }

    /**
     * Функция выполнения SQL-запроса с получением данных
     * @param sql текст запроса
     * @return результат выполнения запроса
     */
    public JSONArray executeQuery (final String sql) {
        ResultSet resultSet = null;
        Statement statement = null;
        JSONArray jsonArray = new JSONArray();
        try {
            if (getConnection() != null) {
                statement = getConnection().createStatement();
                resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    JSONObject obj = new JSONObject();
                    int total_rows  = resultSet.getMetaData().getColumnCount();
                    for ( int i = 0; i < total_rows ; i++ ) {
                        obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                                .toLowerCase(), resultSet.getObject(i + 1));
                    }
                    jsonArray.put(obj);
                }
            }
        } catch (SQLException e) {
            System.err.println ("SQLException : code = " + String.valueOf(e.getErrorCode()) +
                    " - " + e.getMessage());
            System.err.println ("\tSQL : " + sql);
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) { e.getErrorCode(); };
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) { e.getErrorCode(); }
            }
        }
        return jsonArray;
    }

    /**
     * Функция загрузки данных из файла SQL
     * @param inputStream данные из внешнего файла
     * @return результат выполнения запроса
     */
    public boolean importSQL(InputStream inputStream) throws SQLException
    {
        boolean result = false;
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("(;(\r)?\n)|((\r)?\n)?(--)?.*(--(\r)?\n)");
        Statement statement = null;
        try
        {
            statement = this.connection.createStatement();
            while (scanner.hasNext())
            {
                String line = scanner.next();
                if (line.startsWith("/*!") && line.endsWith("*/"))
                {
                    int i = line.indexOf(' ');
                    line = line.substring(i + 1, line.length() - " */".length());
                }

                if (line.trim().length() > 0)
                {
                    statement.execute(line);
                }
            }
        } finally {
            if (statement != null) statement.close();
            result = true;
        }
        return result;
    }

    /*
     * Фенкция получения текущего порта из настроек сервера
     * @return номер порта из базы
     */
    public JSONObject getServerData() {
        JSONArray server_data = executeQuery( "SELECT * FROM neural_network.tcp_server WHERE id = 1" );
        JSONObject data = new JSONObject(server_data.getJSONObject( 0 ).toString());
        //return data.getInt( "port" );
        return data;
    }
}
