package com.jeffskj.torrent.showinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.jeffskj.torrent.config.TVShow;
import com.jeffskj.torrent.http.SimpleHttpClient;

public class TVRageShowInfoProviderTest
{
    @Test
    @Ignore
    public void getShowInfo()
    {
        TVRageShowInfoProvider provider = new TVRageShowInfoProvider(new SimpleHttpClient());
        TVShow show = provider.getShowInfo("The Office (US)");
        assertEquals(6061, show.getId());
        assertEquals(30, show.getRuntime());
        assertEquals("US", show.getCountry());
        assertTrue(show.getSeasons().size() >= 5);
    }
    
    @Test
  @Ignore
  public void getShowInfo2()
  {
      TVRageShowInfoProvider provider = new TVRageShowInfoProvider(new SimpleHttpClient());
      TVShow show = provider.getShowInfo("Mythbusters");
      assertEquals(4605, show.getId());
      assertEquals(60, show.getRuntime());
      assertEquals("US", show.getCountry());
      assertTrue(show.getSeasons().size() >= 5);
  }

}
