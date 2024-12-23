package com.landlordpro;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class LandlordProApplication {
    public static void main(String[] args) throws SQLException {
        // Start H2 TCP server to allow remote connections
        Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092", "-tcpDaemon", "-webAllowOthers").start();
        SpringApplication.run(LandlordProApplication.class, args);
    }
}


