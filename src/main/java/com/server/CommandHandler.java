/*
 Обработчик команд протокола управления сервером
 */
package com.server;

import com.mysql.MySQLHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler{

    final Pattern patternConsole = Pattern.compile("(help|get|set) *(\\w+)* *(\\w+)* *(\\w+)*[\\r\\n]+", Pattern.MULTILINE);
    MySQLHandler db;

    final String help =
            "All commands are case insensitive.\r\n" +
            "Command structure: [help|get|set] [command] [if the command is set, then there must be a value]\r\n" +
            "                                            [if the command is get, then the value will be obtained]\r\n" +
            "help - Description of control commands\r\n" +
            "get  - Get value\r\n" +
            "set  - Set value\r\n" +
            "Description of commands:\r\n" +
            "get srvrParam - Get server connection parameters\r\n" +
            "set srvrPort [num]- Set server port\r\n" +
            "set srvrRespMode [broadcast|unicast]- Set server response mode\r\n";

    public CommandHandler(MySQLHandler db) {
        this.db = db;
    }

    public void execution(String command, CommandHandlerCallback callback) throws IOException {
        String action = command.toLowerCase();
        final Matcher matcherConsole = patternConsole.matcher(action);
        while (matcherConsole.find()) {
            //System.out.println("Full match: " + matcherConsole.group(0));
            //for (int i = 1; i <= matcherConsole.groupCount(); i++) {
            //    System.out.println("Get data Group " + i + ": " + matcherConsole.group(i));
            //}
            // выполнение инструкций от пользователя, комманды не соответсвующие регулярному
            // выражению игнорируются
            switch (matcherConsole.group(1)) {
                case "help":
                    // ...если дальше параметров нет
                    if (matcherConsole.group(2) == null) {
                        callback.commandConsole( String.format( "%s\r\n", help ) );
                    }
                    // ...если есть дополнительные вводные
                    else {
                        ;;
                    }
                    break;
                case "get":
                    switch (matcherConsole.group(2)) {
                        case "srvrparam":
                            JSONObject serverParam = db.getServerData();
                            callback.commandConsole( String.format( "port %d\r\nroleResponse \"%s\" \r\n",
                                    serverParam.getInt( "port" ),
                                    serverParam.getString( "role_response" )
                            ));
                            break;
                        case "scenes":
                            ;;
                            break;
                    }
                    break;
                case "set":
                    switch (matcherConsole.group(2)) {
                        case "srvrport":
                            db.execute( String.format("UPDATE neural_network.tcp_server SET port = %s WHERE id = 1", matcherConsole.group(3)) );
                            callback.commandConsole( String.format( "port %s selected\r\nrestart server for settings to take effect\r\n", matcherConsole.group(3)) );
                            break;
                        case "srvrrespmode":
                            db.execute( String.format("UPDATE neural_network.tcp_server SET role_response = '%s' WHERE id = 1", matcherConsole.group(3)) );
                            callback.commandConsole( String.format( "response mode '%s' selected\r\nrestart server for settings to take effect\r\n", matcherConsole.group(3)) );
                            break;
                    }
                    break;
            }
        }
    }
}
