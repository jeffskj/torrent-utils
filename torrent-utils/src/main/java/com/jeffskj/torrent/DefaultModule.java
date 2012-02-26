package com.jeffskj.torrent;

import java.lang.reflect.Field;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.jeffskj.torrent.config.Configuration;
import com.jeffskj.torrent.config.ConfigurationProvider;
import com.jeffskj.torrent.config.DBConnectionProvider;
import com.jeffskj.torrent.handler.TorrentHandler;
import com.jeffskj.torrent.handler.WebUITorrentHandler;
import com.jeffskj.torrent.log.LogEntryDAO;
import com.jeffskj.torrent.log.LogEntryDAOImpl;
import com.jeffskj.torrent.showinfo.CacheMiss;
import com.jeffskj.torrent.showinfo.CacheShowInfoProvider;
import com.jeffskj.torrent.showinfo.ShowInfoCache;
import com.jeffskj.torrent.showinfo.ShowInfoProvider;
import com.jeffskj.torrent.showinfo.TVRageShowInfoProvider;
import com.jeffskj.torrent.showinfo.XMLShowInfoCache;

public class DefaultModule extends AbstractModule
{    
    @Override
    protected void configure()
    {
        bind(Configuration.class).toProvider(ConfigurationProvider.class).in(Singleton.class);
        bind(Connection.class).toProvider(DBConnectionProvider.class).in(Singleton.class);
        bind(LogEntryDAO.class).to(LogEntryDAOImpl.class).in(Singleton.class);
        bind(ShowInfoCache.class).to(XMLShowInfoCache.class).in(Singleton.class);
        bind(ShowInfoProvider.class).to(CacheShowInfoProvider.class).in(Singleton.class);
        bind(ShowInfoProvider.class).annotatedWith(CacheMiss.class).to(TVRageShowInfoProvider.class).in(Singleton.class);
        
        bind(TorrentHandler.class).to(WebUITorrentHandler.class).in(Singleton.class);
        
//        bindListener(Matchers.any(), new LoggerInjectorListener());
    }

    public class LoggerInjectorListener implements TypeListener
    {
        public <I> void hear(TypeLiteral<I> literal, TypeEncounter<I> encounter)
        {
            for (Field field : literal.getRawType().getDeclaredFields())
            {
                if (field.getType() == Logger.class)
                {
                    encounter.register(new SLF4JLoggerInjector<I>(field));
                }
            }
        }
    }
    
    public class SLF4JLoggerInjector<I> implements MembersInjector<I>
    {
        private final Field field;

        public SLF4JLoggerInjector(Field field)
        {
            this.field = field;
            field.setAccessible(true);
        }

        public void injectMembers(I instance)
        {
            try
            {
                field.set(instance, LoggerFactory.getLogger(field.getDeclaringClass()));
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
