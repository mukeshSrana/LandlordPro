package com.landlordpro;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LandlordProApplication {
    public static void main(String[] args) throws SQLException {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String hashedPassword = encoder.encode("");
//        System.out.println("Hashed Password: " + hashedPassword);
        // Start H2 TCP server to allow remote connections
        Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092", "-tcpDaemon", "-webAllowOthers").start();
        SpringApplication.run(LandlordProApplication.class, args);
    }
}


