/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodappbackend.foodappbackend.allControllers;

import com.foodappbackend.foodappbackend.vmm.DBLoader;
import com.foodappbackend.foodappbackend.vmm.RDBMS_TO_JSON;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PanelController {

    @PostMapping("/createpanel")
    public String userSignup(@RequestParam String email,
            @RequestParam String password,
            @RequestParam String companyname,
            @RequestParam String domainname,
            @RequestParam String databasename
    ) {

        try {
            //EXCEPTION ERROR MAKE executeSQL
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM panels where email ='" + email + "' ");

            if (rs.next()) {
                return "fail";
            } else {

                rs.moveToInsertRow();
                //insert workbench coloum name
                rs.updateString("email", email);
                rs.updateString("password", password);
                rs.updateString("company_name", companyname);
                rs.updateString("domain_name", domainname);

                rs.updateString("database_name", databasename);
                rs.insertRow();

                return "success";
            }

        } catch (Exception ex) {
            return ex.toString();

        }
    }

    @GetMapping("/fetchpannel")
    public String FetchPanel() {
        String ans = new RDBMS_TO_JSON().generateJSON("SELECT * FROM panels");
        return ans;
    }

}
