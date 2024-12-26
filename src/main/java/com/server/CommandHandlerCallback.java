package com.server;

import java.io.IOException;

public interface CommandHandlerCallback {
    void commandConsole(String command) throws IOException;
}
