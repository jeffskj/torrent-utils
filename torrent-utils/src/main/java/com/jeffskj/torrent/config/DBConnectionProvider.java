/**
 * 
 */
package com.jeffskj.torrent.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.google.inject.Provider;
import com.jeffskj.torrent.ResourceLocator;

public class DBConnectionProvider implements Provider<Connection>
{
    public Connection get()
    {
        try
        {
            Class.forName("org.postgresql.Driver", true, Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        Properties props = new Properties();
        try
        {
            props.load(ResourceLocator.locateAsStream("connection.properties"));
        }
        catch (IOException e)
        {
            throw new RuntimeException("could not load connection properties!", e);
        }
        
        try
        {
            return DriverManager.getConnection(props.getProperty("url"), props.getProperty("username"), props.getProperty("password"));
        }
        catch (SQLException e)
        {
            throw new RuntimeException("could not create db connection!", e);
        }
    }}