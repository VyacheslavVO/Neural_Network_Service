package com.server;

import com.mysql.MySQLHandler;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class CreateConnectionMySQL {

    FileInputStream inputStream;
    Properties properties = new Properties();
    MySQLHandler db;
    String host;
    int port;
    String schemaName;
    String username;
    String password;

    public CreateConnectionMySQL(String propertiesPath) throws IOException, SQLException {
        this.inputStream = new FileInputStream( propertiesPath );
        this.properties.load( inputStream );
        inputStream.close();

        this.host = properties.getProperty( "datasource.host" );
        this.port = Integer.parseInt( properties.getProperty( "datasource.port" ) );
        this.schemaName = properties.getProperty( "datasource.schema" );
        this.username = properties.getProperty( "datasource.username" );
        this.password = properties.getProperty( "datasource.password" );

        this.db = new MySQLHandler( properties.getProperty( "datasource.driver-class-name" ) );
        this.db.setURL( this.host, this.schemaName, this.port );
        // создание и наполнение базы данных
        this.db.Connect( this.username, this.password );
        System.out.println("Connection: " + this.db.getConnection());
        if ( this.db.createSchema( this.schemaName ) ) {
            // TODO: выполнить SQL script
            if (this.db.importSQL( new FileInputStream( "src/main/resources/db.migration/DDL_create_tables.sql" ) ) ) {
                this.db.importSQL( new FileInputStream( "src/main/resources/db.migration/DML_insert_tables.sql" ) );
            }
            //System.out.println("Create schema: " + this.schemaName);
        }
    }

    public MySQLHandler getDb() {
        return this.db;
    }
}
