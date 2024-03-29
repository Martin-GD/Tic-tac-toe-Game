package application;

import application.action.Action;
import application.action.log;
import application.controller.ServerHandler;
import application.controller.ServerManger;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;


public class Server_new {
    public static void main(String[] args) throws IOException {
        try {

            // 创建服务端socket
            ServerSocket serverSocket = new ServerSocket(5612);
            System.out.println("服务器端启动!!");
            // 创建客户端socket
            Socket socket, socket2;

            //循环监听等待客户端的连接
            while(true){
                // 监听客户端
                socket = serverSocket.accept();
                System.out.println("one client waiting");
                socket2 = serverSocket.accept();

                ServerHandler thread = new ServerHandler(socket, socket2);
                thread.start();

                InetAddress address=socket.getInetAddress();
                System.out.println("当前客户端的IP："+address.getHostAddress());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }


}
