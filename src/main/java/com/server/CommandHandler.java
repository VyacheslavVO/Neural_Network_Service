/*
 Обработчик команд протокола управления сервером
 */
package com.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {

    final Pattern patternConsole = Pattern.compile("(help|get|set) *(\\w+)* *(\\w+)* *(\\w+)*[\\n]+", Pattern.MULTILINE);

    public CommandHandler() {

    }

    public void execution(String command) {
        String action = command.toLowerCase();
        final Matcher matcherConsole = patternConsole.matcher(action);
        while (matcherConsole.find()) {
            System.out.println("Full match: " + matcherConsole.group(0));
            //for (int i = 1; i <= matcherConsole.groupCount(); i++) {
            //    System.out.println("Get data Group " + i + ": " + matcherConsole.group(i));
            //}
            // выполнение инструкций от пользователя, комманды не соответсвующие регулярному
            // выражению игнорируются
            switch (matcherConsole.group(1)) {
                case "help":
                    System.out.println("Command help!");
                    break;
                case "get":
                    System.out.println("Command get!");
                    break;
                case "set":
                    System.out.println("Command set!");
                    break;
            }
        }
    }
}
