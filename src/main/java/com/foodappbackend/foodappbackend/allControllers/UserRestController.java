/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodappbackend.foodappbackend.allControllers;

import com.foodappbackend.foodappbackend.vmm.DBLoader;
import com.foodappbackend.foodappbackend.vmm.RDBMS_TO_JSON;
import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import jakarta.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "http://165.22.211.208,http://localhost:3000")

public class UserRestController {

    @PostMapping("/usersignup")
    public String userSignup(@RequestParam String useremail,
            @RequestParam String username,
            @RequestParam String userpassword,
            @RequestParam String usernumber
    ) {

        try {
            //EXCEPTION ERROR MAKE executeSQL
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM user_signup where user_email ='" + useremail + "' ");

            if (rs.next()) {
                return "fail";
            } else {

                rs.moveToInsertRow();
                //insert workbench coloum name
                rs.updateString("user_email", useremail);
                rs.updateString("user_name", username);
                rs.updateString("user_password", userpassword);
                rs.updateString("user_number", usernumber);

                rs.updateString("user_active", "no");
                rs.insertRow();

                return "success";
            }

        } catch (Exception ex) {
            return ex.toString();

        }
    }

    @PostMapping("/checkuserlogin")

    public String CheckLogin(@RequestParam String useremail, @RequestParam String userpassword, HttpSession session) {
        try {

            //EXCEPTION ERROR MAKE executeSQLs
            ResultSet rs = DBLoader.executeSQL("select * from user_signup where user_email='" + useremail + "' and user_password='" + userpassword + "'");

            if (rs.next()) {
                String user_name = rs.getString("user_name");
                session.setAttribute("user_name", user_name);

                System.out.println(user_name);
                JSONObject response = new JSONObject();
                response.put("status", "success");
                response.put("user_name", user_name);

                return response.toString();
            } else {
                return "{\"status\":\"fail\"}";
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"" + ex.toString() + "\"}";
        }

    }

    @RequestMapping(value = "/getuserdetails", produces = "application/json", method = RequestMethod.GET)
    public String getCities() {
        String ans = new RDBMS_TO_JSON().generateJSON("SELECT * FROM user_signup");
        return ans;
    }

    @PostMapping("/updateuserstatus")
    public String editMenuItem(@RequestParam String useremail, HttpSession session) {

        System.out.println(useremail);
        String ans = "";

        try {
            //EXCEPTION ERROR MAKE executeSQL
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM user_signup where user_email ='" + useremail + "' ");
            if (rs.next()) {

                rs.updateString("user_active", "yes");

                rs.updateRow();

                return "success";
            } else {

                return "fail";
            }

        } catch (Exception ex) {
            return ex.toString();

        }
    }

    @PostMapping("/showusermenuitems")
    public String showUserMenuItems() {
        String query = "SELECT mi.*, mc.category_name "
                + "FROM menu_items mi "
                + "JOIN menu_category mc ON mi.category_id = mc.category_id";

        String ans = new RDBMS_TO_JSON().generateJSON(query);

        return ans;
    }

    @PostMapping("/addtocart")
    public String addToCart(@RequestParam String useremail,
            @RequestParam String name,
            @RequestParam String items_id,
            @RequestParam String price,
            @RequestParam String category_name,
            @RequestParam String photo) {

        System.out.println(useremail);
        System.out.println(items_id);
        System.out.println(name);
        System.out.println(category_name);
        try {
            //EXCEPTION ERROR MAKE executeSQL
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM cart");

            rs.moveToInsertRow();
            //insert workbench coloum name
            rs.updateString("useremail", useremail);
            rs.updateString("item_id", items_id);
            rs.updateString("item_name", name);
            rs.updateString("photo", photo);
            rs.updateString("quantity", "1");
            rs.updateString("category_name", category_name);
            rs.updateString("price", price);
            rs.updateString("total_amount", price);
            rs.insertRow();

            return "success";

        } catch (Exception ex) {
            return ex.toString();

        }

    }

    @PostMapping("/showcartitems")
    public String showCartItems(@RequestParam String useremail) {
        System.out.println(useremail);
//        SELECT * FROM foodfusion.cart where cart_id NOT IN (select cart_id from foodfusion.orders);
        String ans = new RDBMS_TO_JSON().generateJSON("select * from cart where cart_id NOT IN (select cart_id from orders) and useremail='" + useremail + "'");
        System.out.println("items are " + ans);
        return ans;
    }

    @PostMapping("/deletecartitem")
    public String deleteCartItem(@RequestParam String cart_id) {
        try {

            System.out.println(cart_id);
            //EXCEPTION ERROR MAKE executeSQLs
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM cart where cart_id ='" + cart_id + "' ");

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

    @PostMapping("/showuseraddress")
    public String showUserAddresss(@RequestParam String useremail) {
        System.out.println(useremail);
        String ans = new RDBMS_TO_JSON().generateJSON("select * from user_signup where user_email='" + useremail + "'");
        System.out.println("address is " + ans);
        return ans;
    }

    @PostMapping("/showuseraddressdata")
    public String showUserAddresssData(@RequestParam String userid) {
        System.out.println("user id is" + userid);
        String ans = new RDBMS_TO_JSON().generateJSON("select * from addresses where user_signup_id='" + userid + "'"
                + "ORDER BY addresses.id DESC");
        System.out.println("address is " + ans);
        return ans;
    }

    @PostMapping("/addaddress")
    public String addAdress(@RequestParam int userid,
            @RequestParam String flataddress,
            @RequestParam String streetaddress,
            @RequestParam String cityaddress,
            @RequestParam String stateaddress,
            @RequestParam String countryaddress,
            @RequestParam String mapaddress,
            @RequestParam String latitude,
            @RequestParam String longitude) {

        try {

            ResultSet rs = DBLoader.executeSQL("SELECT * FROM addresses ");

            rs.moveToInsertRow();

            rs.updateInt("user_signup_id", userid);
            rs.updateString("flat_no", flataddress);
            rs.updateString("street",
                    streetaddress);
            rs.updateString("city", cityaddress);
            rs.updateString("state", stateaddress);
            rs.updateString("country", countryaddress);
            rs.updateString("map_address", mapaddress);
            rs.updateString("latitude", latitude);
            rs.updateString("longitude", longitude);

            rs.insertRow();

            return "success";

        } catch (SQLException e) {
            e.printStackTrace();
            return "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @PostMapping("/updatecartquantity")
    public String updateCartQuantity(@RequestParam String cart_id, @RequestParam int quantity, @RequestParam int totalAmount) {

        String ans = "";
        try {
            //EXCEPTION ERROR MAKE executeSQL
            ResultSet rs = DBLoader.executeSQL("SELECT * FROM cart where cart_id ='" + cart_id + "' ");
            if (rs.next()) {

                rs.updateInt("quantity", quantity);
                rs.updateInt("total_amount", totalAmount);

                rs.updateRow();

                return "success";
            } else {

                return "fail";
            }

        } catch (Exception ex) {
            return ex.toString();
        }

    }

    @GetMapping("/fetchOrderDetails")
    public String showOrder(@RequestParam String itemIds) {

        String ans = new RDBMS_TO_JSON().generateJSON("select * from cart where cart_id in (" + itemIds + ")");
        System.out.println("hdgjdbrfjkbjk " + ans);
        return ans;

    }

//    @PostMapping("/success")
//    public String PaymentSuccess(
//            @RequestParam String razororderid,
//            @RequestParam String razorsignature,
//            @RequestParam String razorpaymentid,
//            @RequestParam String address,
//            @RequestParam int amount,
//            @RequestParam String cart_id) {
//
//        System.out.println("Payment successful:");
//        System.out.println("razpaymentid: " + razorpaymentid);
//        System.out.println("razorderid: " + razororderid);
//        System.out.println("razsignature: " + razorsignature);
//        System.out.println("amount: " + amount);
//        System.out.println("cart id is: " + cart_id);
//        System.out.println("cart id is: " + razorpaymentid);
//        System.out.println("address: " + address);
//
//        try {
//              String cleanedCartId = cart_id.replaceAll("[^0-9]", "");
//
//            int cartIdInt = Integer.parseInt(cleanedCartId);
//
//            //EXCEPTION ERROR MAKE executeSQL
//            ResultSet rs = DBLoader.executeSQL("SELECT * FROM orders where cart_id ='" + cartIdInt + "' ");
//
//            if (rs.next()) {
//                return "fail";
//            } else {
//
//                rs.moveToInsertRow();
//                //insert workbench coloum name
//                rs.updateInt("cart_id", cartIdInt);
//                rs.updateString("razorpay_payment_id", razorpaymentid);
//                rs.updateString("razorpay_order_id", razororderid);
//                rs.updateString("razorpay_signature", razorsignature);
//                rs.updateInt("amount", amount);
//                rs.updateString("address", address);
//                rs.insertRow();
//
//                return "success";
//            }
//
//        } catch (Exception ex) {
//
//            return ex.toString();
//
//        }
//    }
    @PostMapping("/success")
    public String PaymentSuccess(
            @RequestParam String razororderid,
            @RequestParam String razorsignature,
            @RequestParam String razorpaymentid,
            @RequestParam String address,
            @RequestParam String amount,
            @RequestParam String cart_id) {

        System.out.println("Payment successful:");
        System.out.println("razpaymentid: " + razorpaymentid);
        System.out.println("razorderid: " + razororderid);
        System.out.println("razsignature: " + razorsignature);
        System.out.println("amount: " + amount);
        System.out.println("cart id is: " + cart_id);
        System.out.println("address: " + address);

        try {
            // Clean and parse amount
            String cleanedAmount = amount.replaceAll("[^0-9]", "");
            int amountInt = Integer.parseInt(cleanedAmount);

            // Split cart_id string by commas and process each ID
            String[] cartIds = cart_id.split(",");
            boolean allCartIdsValid = true;

            for (String idStr : cartIds) {
                int cartIdInt = Integer.parseInt(idStr.trim());

                // Check if cart_id exists in the cart table
                ResultSet rsCart = DBLoader.executeSQL("SELECT * FROM cart WHERE cart_id = " + cartIdInt);
                if (!rsCart.next()) {
                    System.out.println("Cart ID " + cartIdInt + " does not exist.");
                    allCartIdsValid = false;
                    continue; // Skip this ID, but continue with other IDs
                }

                // Proceed with inserting into orders table
                ResultSet rsOrders = DBLoader.executeSQL("SELECT * FROM orders WHERE cart_id = " + cartIdInt);

                if (rsOrders.next()) {
                    System.out.println("Order with cart_id " + cartIdInt + " already exists.");
                    continue; // Skip this ID if the order already exists
                } else {
                    rsOrders.moveToInsertRow();
                    rsOrders.updateInt("cart_id", cartIdInt);
                    rsOrders.updateString("razorpay_payment_id", razorpaymentid);
                    rsOrders.updateString("razorpay_order_id", razororderid);
                    rsOrders.updateString("razorpay_signature", razorsignature);
                    rsOrders.updateInt("amount", amountInt);
                    rsOrders.updateString("address", address);
                    rsOrders.updateString("status", "pending");
                    rsOrders.insertRow();

                    System.out.println("Inserted order for cart_id " + cartIdInt);
                }
            }

            if (allCartIdsValid) {
                return "success";
            } else {
                return "Some cart IDs were invalid or already had orders.";
            }

        } catch (NumberFormatException e) {
            return "Invalid number format: " + e.getMessage();
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    @PostMapping("/userorders")
    public String userOrders(@RequestParam String email) {
        System.out.println("email issj........" + email);
        String ans = new RDBMS_TO_JSON().generateJSON("SELECT * FROM orders"
                + " INNER JOIN cart ON orders.cart_id = cart.cart_id "
                + " where cart.useremail='" + email + "'"
                + " ORDER BY orders.created_at DESC ");

        return ans;
    }

}
