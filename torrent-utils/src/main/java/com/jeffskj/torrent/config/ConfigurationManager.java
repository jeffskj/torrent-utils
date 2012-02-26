package com.jeffskj.torrent.config;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.jeffskj.torrent.ResourceLocator;
import com.jeffskj.torrent.providers.EZRSSTorrentProvider;
import com.jeffskj.torrent.providers.ThePirateBayTorrentProvider;
import com.jeffskj.torrent.providers.TorrentProvider;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;

public class ConfigurationManager
{
    private static XStream xstream;
    private static final String CONFIG_FILE = "torrent-config.xml"; 
    static
    {
        xstream = new XStream();
        
        xstream.aliasType("configuration", Configuration.class);
        xstream.aliasType("show", TVShow.class);
        xstream.aliasType("torrent-provider", TorrentProvider.class);
        xstream.aliasField("torrent-providers", Configuration.class, "providers");
        xstream.aliasField("tv-shows", Configuration.class, "shows");
        xstream.aliasField("torrent-download-location", Configuration.class, "torrentDownloadLocation");
        xstream.aliasField("finished-download-location", Configuration.class, "finishedDownloadLocation");
        xstream.aliasField("video-library-location", Configuration.class, "videoLibraryLocation");
        xstream.aliasField("webui-host", Configuration.class, "webUiHost");
        xstream.aliasField("webui-port", Configuration.class, "webUiPort");
        xstream.aliasField("webui-user", Configuration.class, "webUiUser");
        xstream.aliasField("webui-pass", Configuration.class, "webUiPass");
        
        for (Field f : TVShow.class.getDeclaredFields())
        {
            String name = f.getName();
            if (!name.equals("name") && !name.equals("startSeason"))
            {
                xstream.omitField(TVShow.class, name);
            }
        }
        
        xstream.aliasAttribute(TVShow.class, "name", "name");
        xstream.aliasAttribute(TVShow.class, "startSeason", "start-season");
        xstream.registerConverter(new TorrentProviderConverter());
    }
    
    public Configuration load() 
    {
        try {
            return (Configuration) xstream.fromXML(ResourceLocator.locateAsStream(CONFIG_FILE));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static class TorrentProviderConverter implements SingleValueConverter
    {
        private static final BiMap<String, Class> torrentProviders = HashBiMap.create();
        
        static
        {
            torrentProviders.put("http://thepiratebay.org", ThePirateBayTorrentProvider.class);
            torrentProviders.put("http://ezrss.it", EZRSSTorrentProvider.class);
        }
        
        public boolean canConvert(Class cls)
        {
            return torrentProviders.containsValue(cls) || cls.equals(TorrentProvider.class);
        }

        public Object fromString(String str)
        {
            Class type = torrentProviders.get(str);
            try
            {
                return type.newInstance();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        public String toString(Object obj)
        {
            return torrentProviders.inverse().get(obj.getClass());
        }
    }
}
