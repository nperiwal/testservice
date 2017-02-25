package com.example.application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/")
public class DataPersistApi {

    private static final Logger log = LoggerFactory.getLogger(DataPersistApi.class);

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    String signInQueryValidate = "Select * FROM signupinfo WHERE emailid ='%s' AND password = '%s'";
    String signUpQuery = "INSERT INTO signupinfo (username, password, emailid, phonenumber) VALUES ('%s','%s','%s','%s')";
    String checkIfEmailIdExistsQuery = "Select * FROM signupinfo WHERE emailid = '%s'";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return GSON.toJson("Welcome to the dashboard. Currently in Construction Phase!");

    }

    @Path("signin")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String signIn(@QueryParam("emailid") String emailId,
                         @QueryParam("password") String password) {

        if(emailId == null || emailId.trim().isEmpty() || password == null || password.trim().isEmpty() ) {
            return GSON.toJson("Please supply valid emailid and password query parameter");
        }

        Connection connection = null;
        try {
            BasicDataSource dataSource = DataBaseUtility.getDataSource();
            connection = dataSource.getConnection();

            String checkQuery = String.format(signInQueryValidate, emailId, password);
            PreparedStatement pstmt = connection.prepareStatement(checkQuery);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (!resultSet.isBeforeFirst() ) {
                    return GSON.toJson("Fail");
                }
            } catch (Exception e) {
                //connection.rollback();
                log.error("error in query execution", e);
                return GSON.toJson("Try later");
            }
        } catch (SQLException e) {
            log.error("sql query error", e);
            return GSON.toJson("Try later");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("error while closing the connection", e);
                }
            }
        }
        return GSON.toJson("Pass");
    }

    @Path("signup")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String signUp(@QueryParam("username") String username,
                         @QueryParam("password") String password,
                         @QueryParam("emailid") String emailId,
                         @QueryParam("phonenumber") String phoneNumber) {

        if(emailId == null || emailId.trim().isEmpty() || password == null || password.trim().isEmpty() ) {
            return GSON.toJson("Please supply valid emailid and password query parameter");
        }

        if (username == null) {
            username = emailId;
        }

        Connection connection = null;
        try {
            BasicDataSource dataSource = DataBaseUtility.getDataSource();
            connection = dataSource.getConnection();

            String checkQuery = String.format(checkIfEmailIdExistsQuery, emailId);
            PreparedStatement pstmt = connection.prepareStatement(checkQuery);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.isBeforeFirst() ) {
                    return GSON.toJson("Email Id already exists");
                }
            } catch (Exception e) {
                //connection.rollback();
                log.error("error in query execution", e);
                return GSON.toJson("query execution error");
            }

            String insertQuery = String.format(signUpQuery, username, password, emailId, phoneNumber);
            pstmt = connection.prepareStatement(insertQuery);
            int rows = pstmt.executeUpdate();
            log.info("number of rows affected: {}", rows);
        } catch (SQLException e) {
            log.error("sql query error", e);
            return GSON.toJson("query execution error");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("error while closing the connection", e);
                }
            }
        }
        return GSON.toJson("successful");
    }

    @Path("test1")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String test1() {
        return GSON.toJson("/test1 api hit!");
    }

    @Path("test2")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String test2() {
        return GSON.toJson("/test2 api hit!");
    }

}

