/*
 Сервер NIO для основной связи с сторонними приложениями
 */

package com.server;

import com.mysql.MySQLHandler;
import org.json.JSONObject;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.io.IOException;
import java.util.*;

public class Server implements Runnable{

    private final int port;
    private String role_response;
    private final ServerSocketChannel ssc;
    private final Selector selector;
    private final ByteBuffer buf = ByteBuffer.allocate(256);
    CommandHandler commandHandler;
    MySQLHandler db;

    public Server(MySQLHandler db) throws IOException {
        this.db = db;
        JSONObject serverParam = db.getServerData();
        this.port = serverParam.getInt( "port" );
        this.role_response = serverParam.getString( "role_response" );
        this.ssc = ServerSocketChannel.open();
        this.ssc.socket().bind(new InetSocketAddress(port));
        this.ssc.configureBlocking(false);
        this.selector = Selector.open();

        this.ssc.register(selector, SelectionKey.OP_ACCEPT);
        this.commandHandler = new CommandHandler(db);
    }

    @Override
    public void run() {
        try {
            System.out.println("Server starting on port " + this.port);

            Iterator<SelectionKey> iter;
            SelectionKey key;
            while(this.ssc.isOpen()) {
                selector.select();
                iter = this.selector.selectedKeys().iterator();
                while(iter.hasNext()) {
                    key = iter.next();
                    iter.remove();

                    if(key.isAcceptable()) this.handleAccept(key);
                    if(key.isReadable()) this.handleRead(key);
                }
            }
        } catch(IOException e) {
            System.out.println("IOException, server of port " + this.port + " terminating. Stack trace:");
            e.printStackTrace();
        }
    }

    private final ByteBuffer welcomeBuf = ByteBuffer.wrap("Welcome to Neural Network by VO!\r\n".getBytes());

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
        String address = sc.socket().getInetAddress().toString() + ":" + sc.socket().getPort();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ, address);
        sc.write(welcomeBuf);
        welcomeBuf.rewind();
        System.out.println("accepted connection from: " + address);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel ch = (SocketChannel) key.channel();
        StringBuilder sb = new StringBuilder();

        buf.clear();
        int read;
        while( (read = ch.read(buf)) > 0 ) {
            buf.flip();
            byte[] bytes = new byte[buf.limit()];
            buf.get(bytes);
            sb.append(new String(bytes));
            buf.clear();
        }
        String msg;
        if(read < 0) {
            msg = key.attachment() + " client disconnected...\n";
            ch.close();
        }
        else {
            msg = key.attachment() + ": "+ sb.toString();
        }

        System.out.println(msg);

        // TODO: сделать выбор режима отправки данных пользователям (broadcast|unicast)
        if (this.role_response.equals( "broadcast" )) {
            this.commandHandler.execution( msg, this::broadcast );
        } else if (this.role_response.equals( "unicast" )) {
            this.commandHandler.execution( msg, command -> unicast( key, command ) );
        }
    }

    /*
    Отправка ответа персонально одному пользователю
     */
    private void unicast(SelectionKey key, String msg) throws IOException {
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
        if(key.isValid() && key.channel() instanceof SocketChannel) {
            SocketChannel sch = (SocketChannel) key.channel();
            sch.write(msgBuf);
            msgBuf.rewind();
        }
    }

    /*
    Отправка ответа всем пользователям
     */
    private void broadcast(String msg) throws IOException {
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());
        for(SelectionKey key : selector.keys()) {
            if(key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel sch = (SocketChannel) key.channel();
                sch.write(msgBuf);
                msgBuf.rewind();
            }
        }
    }
}
