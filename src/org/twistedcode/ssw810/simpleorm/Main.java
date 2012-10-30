package org.twistedcode.ssw810.simpleorm;

import sun.awt.CausedFocusEvent;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tracyde
 * Date: 10/29/12
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    private static final String DBFILE = "coffees.db";
    private static final String DBNAME = "coffees";

    private static void loadDB() {
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();

            // drop the table if it already exists then create a new one
            stmt.executeUpdate("drop table if exists " + DBNAME);
            stmt.executeUpdate("create table " + DBNAME + " (cof_name string, price double)");

            // actually enter values
            stmt.executeUpdate("insert into coffees values ('Cafe Mocha', 3.52)");
            stmt.executeUpdate("insert into coffees values ('Americano', 1.99)");
            stmt.executeUpdate("insert into coffees values ('Latte', 3.15)");
            stmt.executeUpdate("insert into coffees values ('Cappuccino', 5.45)");

            closeConnection(conn);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + DBFILE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void printResultSet(ResultSet rs) throws SQLException {
        System.out.println("--------------------------------------------------");
        while (rs.next()) {
            // read the result set
            System.out.println("name = " + rs.getString("cof_name"));
            System.out.println("price = " + rs.getDouble("price"));
            System.out.println("--------------------------------------------------");
        }
    }

    private static void printObjects(ResultSet rs) throws SQLException {
        System.out.println("--------------------------------------------------");
        while (rs.next()) {
            Coffee c = (Coffee) SimpleORM.Rehydrate(rs, Coffee.class);
            // read the result set
            System.out.println("name = " + c.getCOF_NAME());
            System.out.println("price = " + c.getPRICE());
            System.out.println("--------------------------------------------------");
        }
    }

    private static ResultSet genResultSet(Connection conn) {
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from " + DBNAME);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return rs;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("Creating Test Database...");
        loadDB();

        System.out.println("Generating ResultSet...");
        ResultSet rs = genResultSet(getConnection());

        System.out.println("Printing Objects...");
        try {
            printObjects(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
