package com.jeffskj.torrent.log;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.google.inject.Inject;

public class LogEntryDAOImpl implements LogEntryDAO
{
    private final QueryRunner runner = new QueryRunner();
    private static final RowProcessor ROW_PROCESSOR = new BasicRowProcessor(new CustomBeanProcessor());
    private final Connection connection;
    
    @Inject
    public LogEntryDAOImpl(Connection connection)
    {
        this.connection = connection;
    }
    
    @SuppressWarnings("unchecked")
    public List<LogEntry> findAll()
    {
        try
        {
            return (List<LogEntry>) runner.query(connection, "select * from log_entry", 
                new BeanListHandler(LogEntry.class, ROW_PROCESSOR));
        }
        catch (SQLException e)
        {
            throw new RuntimeException("error reading download log!", e);
        }
    }

    public void insert(LogEntry entry)
    {
        String sql = "INSERT INTO log_entry(show_id, status, date, url) VALUES (?, ?, ?, ?);";
        runSql(sql, entry.getShowId(), entry.getStatus().name(), asTimeStamp(entry.getDate()), entry.getUrl());
    }

    public void update(LogEntry entry)
    {
        String sql = "UPDATE log_entry SET status=?, date=?, url=? WHERE show_id=?";
        runSql(sql, entry.getStatus().name(), asTimeStamp(entry.getDate()), entry.getUrl(), entry.getShowId());
    }
    
    private Timestamp asTimeStamp(Date date)
    {
        return new Timestamp(date.getTime());
    }
    
    private void runSql(String sql, Object... params)
    {
        try
        {
            runner.update(connection, sql, params);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("error running sql!", e);
        }
    }
    
    private static final class CustomBeanProcessor extends BeanProcessor
    {
        @Override
        protected int[] mapColumnsToProperties(ResultSetMetaData rsmd, PropertyDescriptor[] props)
                throws SQLException
        {
            int cols = rsmd.getColumnCount();
            int columnToProperty[] = new int[cols + 1];
            Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);
   
            for (int col = 1; col <= cols; col++) {
                String columnName = rsmd.getColumnName(col);
                for (int i = 0; i < props.length; i++) {
   
                    if (columnName.replaceAll("_", "").equalsIgnoreCase(props[i].getName())) {
                        columnToProperty[col] = i;
                        break;
                    }
                }
            }
            
            return columnToProperty;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected Object processColumn(ResultSet rs, int index, Class propType) throws SQLException
        {
            if (propType.isEnum()) 
            {
                return Enum.valueOf(propType, rs.getString(index));
            }
            return super.processColumn(rs, index, propType);
        }
    }
}
