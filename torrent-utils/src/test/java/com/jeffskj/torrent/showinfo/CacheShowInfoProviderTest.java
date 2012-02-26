package com.jeffskj.torrent.showinfo;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Test;

import com.jeffskj.torrent.config.Episode;
import com.jeffskj.torrent.config.Season;
import com.jeffskj.torrent.config.TVShow;


public class CacheShowInfoProviderTest
{
    @Test
    public void getShowFromCacheEvenIfExpiredWhenMissProviderFails()
    {
        TVShow show = new TVShow("House").addSeason(new Season(1).addEpisode(new Episode(1)));
        ShowInfoProvider missProvider = createMock(ShowInfoProvider.class);
        expect(missProvider.getShowInfo("House")).andThrow(new UnsupportedOperationException());
        
        ShowInfoCache cache = createMock(ShowInfoCache.class);
        
        expect(cache.get("House")).andReturn(show);
        expect(cache.getCachedDate(show)).andReturn(DateUtils.addDays(new Date(), -10));

        EasyMock.replay(missProvider, cache);
        
        CacheShowInfoProvider provider = new CacheShowInfoProvider(missProvider, cache);
        assertEquals(show, provider.getShowInfo("House"));
        
        EasyMock.verify(missProvider, cache);
        EasyMock.reset(missProvider, cache);
        
        expect(missProvider.getShowInfo("House")).andReturn(null);
        expect(cache.get("House")).andReturn(show);
        expect(cache.getCachedDate(show)).andReturn(DateUtils.addDays(new Date(), -10));
        
        EasyMock.replay(missProvider, cache);
        
        assertEquals(show, provider.getShowInfo("House"));
        EasyMock.verify(missProvider, cache);
    }
    
    @Test
    public void getShowFromMissProvider()
    {
        TVShow show = new TVShow("House").addSeason(new Season(1).addEpisode(new Episode(1)));
        ShowInfoProvider missProvider = createMock(ShowInfoProvider.class);
        expect(missProvider.getShowInfo("House")).andReturn(show);
        
        ShowInfoCache cache = createMock(ShowInfoCache.class);
        expect(cache.get("House")).andReturn(null);
        cache.put(show);
        
        EasyMock.replay(missProvider, cache);
        CacheShowInfoProvider provider = new CacheShowInfoProvider(missProvider, cache);
        assertEquals(show, provider.getShowInfo("House"));
        
        EasyMock.verify(missProvider, cache);
    }
}