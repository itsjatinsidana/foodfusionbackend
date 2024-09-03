package com.foodappbackend.foodappbackend.vmm;

import java.sql.*;

public class DBLoader {

    public static ResultSet executeSQL(String sql) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        System.out.println("Driver Loading Done");                      //change schema name here

<<<<<<< HEAD
//         Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/foodfusion", "root", "system");
=======
//        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/foodfusion", "root", "system");
>>>>>>> a7878633b016fa2c4245fe82a6b9a31565946e52
//         Connection conn = DriverManager.getConnection(
//    "jdbc:mysql://165.22.211.208:3306/serverdbfood",
//    "jatin",
//    "new_password"
//);
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/serverdbfood",
                "jjatin",
                "Jatin$123"
        );

        System.out.println("connection done");
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        System.out.println("Statement Done");
        ResultSet rs = stmt.executeQuery(sql);
        System.out.println("Statement Created");

        return rs;
    }

    public static ResultSet executeSql(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
