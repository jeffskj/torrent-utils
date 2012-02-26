package com.jeffskj.torrent.log;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jeffskj.torrent.config.DBConnectionProvider;
import com.jeffskj.torrent.log.LogEntry.Status;

public class LogEntryDAOImplTest
{
    private Connection conn;

    @Before
    public void setup() throws SQLException 
    {
        conn = new DBConnectionProvider().get();
        conn.prepareStatement("delete from log_entry").executeUpdate();
    }
    
    @Test
    @Ignore
    public void testCrudOperations()
    {
        LogEntry entry = new LogEntry("House S01E01", Status.COMPLETED, "http://localhost/");
        
        LogEntryDAO dao = new LogEntryDAOImpl(conn);
        assertEquals(0, dao.findAll().size());
        
        // -- insert --
        dao.insert(entry);
        List<LogEntry> all = dao.findAll();
        assertEquals(1, all.size());
        assertEquals(Status.COMPLETED, all.get(0).getStatus());
        
        // -- update --
        entry.setStatus(Status.FAILURE);
        dao.update(entry);
        assertEquals(Status.FAILURE, dao.findAll().get(0).getStatus());
    }
}
