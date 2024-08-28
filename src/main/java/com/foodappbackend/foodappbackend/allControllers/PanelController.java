/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodappbackend.foodappbackend.allControllers;

import com.foodappbackend.foodappbackend.vmm.DBLoader;
import com.foodappbackend.foodappbackend.vmm.RDBMS_TO_JSON;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "http://165.22.211.208")
public class PanelController {

//    @PostMapping("/createpanel")
//    public String userSignup(@RequestParam String email,
//            @RequestParam String password,
//            @RequestParam String companyname,
//            @RequestParam String domainname,
//            @RequestParam String databasename
//    ) {
//
//        try {
//            //EXCEPTION ERROR MAKE executeSQL
//            ResultSet rs = DBLoader.executeSQL("SELECT * FROM panels where email ='" + email + "' ");
//
//            if (rs.next()) {
//                return "fail";
//            } else {
//
//                rs.moveToInsertRow();
//                //insert workbench coloum name
//                rs.updateString("email", email);
//                rs.updateString("password", password);
//                rs.updateString("company_name", companyname);
//                rs.updateString("domain_name", domainname);
//
//                rs.updateString("database_name", databasename);
//                rs.insertRow();
//
//                return "success";
//            }
//
//        } catch (Exception ex) {
//            return ex.toString();
//
//        }
//    }
    @GetMapping("/fetchpannel")
    public String FetchPanel() {
        String ans = new RDBMS_TO_JSON().generateJSON("SELECT * FROM panels");
        return ans;
    }

    @PostMapping("/createpanel")
    public String createPanel(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String companyname,
            @RequestParam String domainname,
            @RequestParam String databasename) {

        try {
            // Check if a panel already exists with the given email
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM panels WHERE email ='" + email + "' ");
            if (rs.next()) {
                return "fail";
            } else {
                // Generate a random password for the database user
                String dbPassword = generateRandomPassword();

                // Create database and user with the generated password
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "root_password"); // Update with your root credentials
                Statement stmt = conn.createStatement();

                // Create database
                stmt.executeUpdate("CREATE DATABASE " + databasename);

                // Create user and grant privileges
//                String createUserSQL = String.format(
//                        "CREATE USER '%s'@'localhost' IDENTIFIED BY '';"
//                        + "GRANT ALL PRIVILEGES ON %s.* TO '%s'@'localhost';"
//                        + "FLUSH PRIVILEGES;",
//                        databasename, dbPassword, databasename, databasename
//                );
//                stmt.executeUpdate(createUserSQL);

                // Insert panel details into the database
                rs.moveToInsertRow();
                rs.updateString("email", email);
                rs.updateString("password", password);
                rs.updateString("company_name", companyname);
                rs.updateString("domain_name", domainname);
                rs.updateString("database_name", databasename);
//                rs.updateString("db_user", databasename); // Assuming database user name is the same as the database name
//                rs.updateString("db_password", dbPassword);
                rs.insertRow();

                return "success";
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }

// Helper method to generate a random password
    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

}
