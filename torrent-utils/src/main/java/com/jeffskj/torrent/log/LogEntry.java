/**
 * 
 */
package com.jeffskj.torrent.log;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class LogEntry
{
    public enum Status
    {
        SUCCESS, FAILURE, NOTFOUND, INVALID, COMPLETED
    }

    public static final Comparator<LogEntry> BY_DATE = new Comparator<LogEntry>(){
        public int compare(LogEntry lhs, LogEntry rhs)
        {
            return rhs.date.compareTo(lhs.date);
        }};
    
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
    private String id;
    private Date date = new Date();
    private Status status;
    private String url;

    public LogEntry()
    {}

    public LogEntry(String id, Status status, String url)
    {
        this.id = id;
        this.status = status;
        this.url = url;
    }

    public void setShowId(String id)
    {
        this.id = id;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }

    public Status getStatus()
    {
        return status;
    }

    public Date getDate()
    {
        return date;
    }

    public String getShowId()
    {
        return id;
    }
    
    @Override
    public String toString()
    {
        return id + "\t" + DATE_FORMAT.format(date) + "\t" + getStatus().name() + (url == null ? "" : "\t" + url); 
    }
}