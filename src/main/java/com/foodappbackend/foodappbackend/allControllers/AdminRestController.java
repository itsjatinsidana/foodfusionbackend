/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodappbackend.foodappbackend.allControllers;

import ch.qos.logback.core.model.Model;
import com.foodappbackend.foodappbackend.vmm.DBLoader;
import com.foodappbackend.foodappbackend.vmm.RDBMS_TO_JSON;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "http://165.22.211.208,http://localhost:3000")

public class AdminRestController {

    @PostMapping(value = "/checklogin", produces = "text/plain")

    public String CheckLogin(@RequestParam String un, @RequestParam String ps, HttpSession session) {
        try {
            System.out.println(un);
            System.out.println(ps);
            //EXCEPTION ERROR MAKE executeSQLs
            ResultSet rs = DBLoader.executeSQL("select * from adminlogin where username='" + un + "' and password='" + ps + "'");

            if (rs.next()) {
                session.setAttribute("username", un);

                return "success";
            } else {
                return "fail";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }

    }

    @PostMapping("/addmenucategory")
    public String addMenuCategory(@RequestParam String category) {
        String ans = "";

        System.out.println(category);

        try {
            //EXCEPTION ERROR MAKE executeSQL
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM menu_category where category_name ='" + category + "' ");

            if (rs.next()) {
                return "fail";
            } else {

                rs.moveToInsertRow();
                //insert workbench coloum name
                rs.updateString("category_name", category);

                rs.insertRow();

                return "success";
            }

        } catch (Exception ex) {
            return ex.toString();

        }
    }

    @PostMapping("/showcategory")
    public String showCities() {
        String ans = new RDBMS_TO_JSON().generateJSON("select * from menu_category");

        return ans;
    }

//    @PostMapping("/addcategory")
//    public String addDoctor(@RequestParam String name,
//            @RequestParam String description,
//            @RequestParam String price,
//            @RequestParam String categoryname,
//            @RequestPart MultipartFile photo) 
//   {
//        String ans = "";
//
//        String oname = photo.getOriginalFilename();
//
//        try {
//            //EXCEPTION ERROR MAKE executeSQL
//            ResultSet rs = DBLoader.executeSQL("SELECT * FROM menu_items");
//
//            byte b[] = photo.getBytes();
//            String abspath = "src/main/resources/static/myuploads/";
//            FileOutputStream fos = new FileOutputStream(abspath + oname);
//            fos.write(b);
//            System.out.println("In Insert Row");
//            rs.moveToInsertRow();
//            //insert workbench coloum name
//            rs.updateString("name", name);
//            rs.updateString("price", price);
//            rs.updateString("category_id", categoryname);
//            rs.updateString("photo", "myuploads/" + oname);
//            rs.updateString("description", description);
//
//            rs.insertRow();
//
//            System.out.println("Insert Successfull");
//
//            return "success";
//
//        } catch (Exception ex) {
//            return ex.toString();
//
//        }
//    }
    @PostMapping("/addmenuitem")
    public String addCategory(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            @RequestParam String categoryname,
            @RequestPart MultipartFile photo) {
        String ans = "";
        String oname = photo.getOriginalFilename();

        try {
            // Find the correct category_id based on categoryname
            ResultSet categoryRs = DBLoader.executeSQL("SELECT category_id FROM menu_category WHERE category_name='" + categoryname + "'");
            if (!categoryRs.next()) {
                return "Invalid category name";
            }
            int categoryId = categoryRs.getInt("category_id");

            // Load the menu_items table to insert a new item
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM menu_items");

            byte[] b = photo.getBytes();
            String abspath = new File ("src/main/resources/static/myuploads/").getAbsolutePath();
            File file = new File(abspath + File.separator + oname);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(b);
            fos.close();

            rs.moveToInsertRow();
            rs.updateString("name", name);
            rs.updateString("price", price);
            rs.updateInt("category_id", categoryId); // Use the integer ID
            rs.updateString("photo", "myuploads/" + oname);
            rs.updateString("description", description);

            rs.insertRow();

            return "success";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    @PostMapping("/showmenuitems")
    public String showMenuItems() {
        String query = "SELECT mi.*, mc.category_name "
                + "FROM menu_items mi "
                + "JOIN menu_category mc ON mi.category_id = mc.category_id";

        String ans = new RDBMS_TO_JSON().generateJSON(query);

        return ans;
    }

    @PostMapping("/deletemenuitem")
    public String deleteMenuItem(@RequestParam String items_id, HttpSession session) {
        try {

            System.out.println(items_id);

            session.setAttribute("items_id", items_id);

            //EXCEPTION ERROR MAKE executeSQLs
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM menu_items where items_id ='" + items_id + "' ");

            if (rs.next()) {

                rs.deleteRow();
                System.out.println("deleted succusfully");
                return "success";
            } else {
                return "fail";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }

    }

    @PostMapping("/editmenuitem")
    public String editMenuItem(@RequestParam String itemname, @RequestParam String itemprice, @RequestParam String itemdescription, @RequestParam MultipartFile itemphoto, @RequestParam String itemcategoryname, HttpSession session) {
        String oname = itemphoto.getOriginalFilename();

        String ans = "";
        String items_id = (String) session.getAttribute("items_id");

        try {
            //EXCEPTION ERROR MAKE executeSQL
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM menu_items where items_id ='" + items_id + "' ");

            byte[] b = itemphoto.getBytes();
            String abspath = "src/main/resources/static/myuploads/";
            FileOutputStream fos = new FileOutputStream(abspath + oname);
            fos.write(b);
            fos.close();

            if (rs.next()) {

                rs.updateString("name", itemname);
                rs.updateString("price", itemprice);

                rs.updateString("photo", "myuploads/" + oname);
                rs.updateString("category_id", itemcategoryname);
                rs.updateString("description", itemdescription);
                rs.updateRow();

                return "success";
            } else {

                return "fail";
            }

        } catch (Exception ex) {
            return ex.toString();

        }
    }

    @PostMapping("/getmenueditdetail")
    public String getMenuEditDetail(@RequestParam String items_id) {
        try {

            System.out.println(items_id);
            String ans = "";
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM menu_items WHERE items_id='" + items_id + "'");
            if (rs.next()) {
                ans = ans + rs.getString("name") + ";";
                ans = ans + rs.getString("price") + ";";
                ans = ans + rs.getString("photo") + ";";
                ans = ans + rs.getString("category_id") + ";";
                ans = ans + rs.getString("description");

                System.out.println(ans);

                return ans;
            } else {
                return "fail";
            }
        } catch (Exception ex) {
            return ex.toString();

        }

    }

    @PostMapping("/showuserorders")
    public String showUserOrders() {
        String ans = new RDBMS_TO_JSON().generateJSON("SELECT * FROM orders "
                + "INNER JOIN cart ON orders.cart_id = cart.cart_id"
                + " ORDER BY orders.created_at DESC ");
     

        return ans;
    }

    @PostMapping("/handleconfirm")
    public String handleConfirm(@RequestParam int id) {

        try {
            //EXCEPTION ERROR MAKE executeSQL
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM orders where id ='" + id + "' ");

            if (rs.next()) {

                rs.updateString("status", "Accepted");
                rs.updateRow();

                return "success";
            } else {

                return "fail";
            }

        } catch (Exception ex) {
            return ex.toString();

        }
    }

    @PostMapping("/handlereject")
    public String handleReject(@RequestParam int id) {

        try {
            //EXCEPTION ERROR MAKE executeSQL
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM orders where id ='" + id + "' ");

            if (rs.next()) {

                rs.updateString("status", "Rejected");
                rs.updateRow();

                return "success";
            } else {

                return "fail";
            }

        } catch (Exception ex) {
            return ex.toString();

        }
    }
}
