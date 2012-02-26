package com.jeffskj.torrent.log;

import java.util.List;


public interface LogEntryDAO
{
    List<LogEntry> findAll();
    void update(LogEntry entry);
    void insert(LogEntry entry);
}
