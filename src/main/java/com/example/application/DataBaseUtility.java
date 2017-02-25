package com.example.application;


import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.SQLException;

public class DataBaseUtility implements ServletContextListener {

    private final Logger log = LoggerFactory.getLogger(DataBaseUtility.class);

    private static BasicDataSource dataSource;

    public static BasicDataSource getDataSource() {

        if (dataSource == null) {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName("com.mysql.jdbc.Driver");
            ds.setUrl("jdbc:mysql://localhost:3306/users");
            ds.setUsername("root");
            ds.setPassword("mysqlpwd");

            ds.setMinIdle(5);
            ds.setMaxIdle(10);
            ds.setMaxOpenPreparedStatements(100);

            dataSource = ds;
        }
        return dataSource;
    }


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("DataBaseUtility: initializing context");
        //getDataSource();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            log.info("DataBaseUtility: destroying  context");
            dataSource.close();
        } catch (SQLException e) {
            log.error("Error while closing data source", e);
        }
    }
}
