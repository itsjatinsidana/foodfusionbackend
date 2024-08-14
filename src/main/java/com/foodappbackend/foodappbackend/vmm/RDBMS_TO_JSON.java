package com.foodappbackend.foodappbackend.vmm;

import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RDBMS_TO_JSON 
{

    public String generateJSON(String SQLStatement) 
    {
        String JSONans = "";

        try 
        {
            
            
            ResultSet rs = DBLoader.executeSQL(SQLStatement);
            
            System.out.println("ResultSet Created");

            ResultSetMetaData rsmd = rs.getMetaData();

            int n = rsmd.getColumnCount();

            JSONObject ansobject = new JSONObject();

            //Define JSONArray
            JSONArray jsonArray = new JSONArray();

            ansobject.put("ans", jsonArray);

            while (rs.next()) 
            {
                //Create object for single row 
                JSONObject singlerow = new JSONObject();

                for (int j = 1; j <= n; j++) 
                {
                    String clname = rsmd.getColumnLabel(j);
                    //System.out.println(clname);

                    //Fill data in Single Row Object
                    singlerow.put(clname, rs.getString(clname));
                }

                //Add single object in array
                jsonArray.add(singlerow);
            }

            return (ansobject.toJSONString());

            ///////////////////////
        } catch (Exception e) {
            e.printStackTrace();
        }

        return JSONans;
    }

  
}
