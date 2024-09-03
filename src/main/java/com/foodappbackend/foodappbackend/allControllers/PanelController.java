/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodappbackend.foodappbackend.allControllers;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.URI;
import com.foodappbackend.foodappbackend.vmm.DBLoader;
import com.foodappbackend.foodappbackend.vmm.RDBMS_TO_JSON;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
@CrossOrigin(origins = "http://localhost:3000,http://165.22.211.208")
//@CrossOrigin(origins = "http://165.22.211.208")
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
        String ans = new RDBMS_TO_JSON().generateJSON("SELECT * FROM panels ORDER BY id DESC");
        return ans;
    }

      @GetMapping("/helo")
    public String Helo() {
        String ans = "hello";
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

                // Create database
//            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/foodfusion", "root", "system"); 
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/serverdbfood",
                        "jjatin",
                        "Jatin$123"
                );
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("CREATE DATABASE " + domainname);

                // Path to MySQL and the dump file
//                String mysqlPath = "C:/Program Files/MySQL/MySQL Server 8.0/bin/mysql.exe";
//                String dumpFilePath = "C:/Users/user/Documents/dumps/serverdbfood.sql"; 
                String mysqlPath = "/usr/bin/mysql";
                String dumpFilePath = "/home/serverdbfood.sql";

                // Command to import the dump file
                String[] importCommand = {
                    mysqlPath,
                    "-u", "root",
                    "-psystem",
                    domainname,
                    "-e",
                    "source " + dumpFilePath
                };

                // Execute the command using ProcessBuilder
                ProcessBuilder processBuilder = new ProcessBuilder(importCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                // Wait for the process to complete
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    return "Error importing database dump. Exit code: " + exitCode;
                }

                int dbCount = countDatabases(conn);
                int port = 8081 + dbCount;

                // Insert panel details into the database
                rs.moveToInsertRow();
                rs.updateString("email", email);
                rs.updateString("password", password);
                rs.updateString("company_name", companyname);
                rs.updateString("domain_name", domainname);
                rs.updateString("database_name", databasename);
//            rs.updateString("db_user", databasename); // Assuming database user name is the same as the database name
//            rs.updateString("db_password", dbPassword);
                rs.insertRow();

//                Connection newDbConn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + domainname, "root", "system");
                Connection newDbConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + domainname, "jjatin", "Jatin$123");
                String insertAdminLoginSQL = "INSERT INTO adminlogin (username, password) VALUES (?, ?)";
                PreparedStatement pstmt = newDbConn.prepareStatement(insertAdminLoginSQL);
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.executeUpdate();

                String nginxConfig = createNginxConfig(domainname, port);
                String nginxConfigPath = "/etc/nginx/sites-available/" + domainname;
                Files.write(Paths.get(nginxConfigPath), nginxConfig.getBytes());

                // Create a symbolic link to sites-enabled
                String enabledPath = "/etc/nginx/sites-enabled/" + domainname;
                ProcessBuilder linkBuilder = new ProcessBuilder("ln", "-s", nginxConfigPath, enabledPath);
                linkBuilder.start().waitFor();

                // Reload Nginx
//                ProcessBuilder reloadBuilder = new ProcessBuilder("nginx", "-s", "reload");
//                reloadBuilder.start().waitFor();  
                // Add DNS record using DigitalOcean API
//                String doToken = "dop_v1_025bce7b61133eca58d2f4d434940c67f9648505449be0804727365b85338f59";
//
////                String doToken = dotenv.get("DO_API_TOKEN");
//
//
//                String apiEndpoint = "https://api.digitalocean.com/v2/domains/" + domainname + "/records";
//                String recordData = "{ \"type\": \"A\", \"name\": \"@\", \"data\": \"YOUR_SERVER_IP\", \"ttl\": 1800 }";
//
//                HttpClient client = HttpClient.newHttpClient();
//                HttpRequest request;
//                try {
//                    request = HttpRequest.newBuilder()
//                            .uri(new URI(apiEndpoint)) // Use the URI constructor directly
//                            .header("Authorization", "Bearer " + doToken)
//                            .header("Content-Type", "application/json")
//                            .POST(HttpRequest.BodyPublishers.ofString(recordData))
//                            .build();
//
//                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//                    if (response.statusCode() != 201) {
//                        return "Failed to create DNS record: " + response.body();
//                    }
//
//                } catch (Exception e) {
//                    return "Error creating DNS record: " + e.getMessage();
//                }
                return "success";
            }
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    private int countDatabases(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SHOW DATABASES");
        int count = 0;
        while (rs.next()) {
            count++;
        }
        return count - 1; // Subtracting 1 to exclude the default databases (e.g., information_schema, mysql, performance_schema)
    }

    private String createNginxConfig(String domainname, int port) {
        return "server {\n"
                + "    listen 80;\n"
                + "    server_name " + domainname + ";\n"
                + "    location / {\n"
                + "        proxy_pass http://localhost:" + port + ";\n"
                + "        proxy_set_header Host $host;\n"
                + "        proxy_set_header X-Real-IP $remote_addr;\n"
                + "        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\n"
                + "        proxy_set_header X-Forwarded-Proto $scheme;\n"
                + "    }\n"
                + "}";
    }

// Helper method to generate a random password
    private String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

}
