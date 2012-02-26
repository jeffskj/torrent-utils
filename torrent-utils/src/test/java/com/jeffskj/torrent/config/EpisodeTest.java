package com.jeffskj.torrent.config;

import static org.junit.Assert.assertEquals;

import java.text.MessageFormat;

import junit.framework.AssertionFailedError;

import org.junit.Test;

import com.jeffskj.torrent.testing.AbstractTVShowTest;

public class EpisodeTest extends AbstractTVShowTest
{
    @Test
    public void canBeConstructedFromIDString()
    {
        Episode e = new Episode("House S06E03");
        assertEquals(3, e.getEpisodeNum());
        assertEquals("House", e.getShowName());
        assertEquals(6, e.getSeason().getNumber());
    }
    
    @Test
    public void getIdString()
    {
        Episode e = getEpisode("House", 1, 1);
        System.out.println(e.getIdString());
        assertEquals("House S01E01", e.getIdString());
    }
    
    @Test
    public void fileNameTest()
    {
        Episode episode = getEpisode("House", 6, 2, "Epic Fail");
        assertEquals("House - S06E02 - Epic Fail", episode.getFileName());
        
        episode = getEpisode("House", 6, 3, "Dr House: An Asshole");
        assertEquals("House - S06E03 - Dr House An Asshole", episode.getFileName());
        
        episode = getEpisode("The Big Bang Theory", 3, 2);
        assertEquals("The Big Bang Theory - S03E02 - Untitled", episode.getFileName());
    }
    
    @Test
    public void pathTest()
    {
        Episode episode = getEpisode("House", 6, 2, "Epic Fail");
        assertEquals("House/Season 06/House - S06E02 - Epic Fail", episode.getPath());
        
        episode = getEpisode("The Big Bang Theory", 3, 2);
        assertEquals("The Big Bang Theory/Season 03/The Big Bang Theory - S03E02 - Untitled", episode.getPath());
    }
    
    @Test 
    public void matchesTest()
    {
        Episode e = getEpisode("House", 1, 1);
        matchesAll(e, "House S01E01", "House 101", "House 01x01");
        matchesNone(e, "House S01E02");
        
        e = getEpisode("Hell's Kitchen (US)", 1, 1, "16 Chefs Remain");
        matchesAll(e, "hells.kitchen.s01e01", "Hells.Kitchen.US.101", 
                   "Hells.Kitchen.(US).S01E01", "Hells.Kitchen.US.S01E01");
        matchesNone(e, "Hells.Kitchen.Something(US).S01E01", "Hells.Kitchen.UK.S01E01");
        
        e = getEpisode("Hell's Kitchen (US)", 6, 13, "4 Chefs Compete");
        matchesAll(e, "Hell's Kitchen (US) - S06E13 - 4 Chefs compete - XviD.avi", 
                   "Hell's Kitchen (US) - S06E13.avi", 
                   "Hells.Kitchen.US.S06E13.WS.PDTV.XviD-2HD.avi");
        
        matchesNone(e, "Hells.Kitchen.UK.S06E13.WS.PDTV.XviD-2HD.avi");
        e = getEpisode("The Office", 6, 4, "Niagra");
        matchesAll(e, "The Office US S06E04 Niagara HDTV XviD-FQM [www.torrentsforall.net]", 
                   "The Office s06e04", "The.Office.US.S06E04.Niagara.HDTV.XviD-FQM.avi",
                   "The Office US - Niagra - S06E04 HDTV XviD-FQM megaups.com", 
                   "NBC - The Office - 604 - Niagra.avi.torrent", 
                   "the.office.604.hdtv-0tv.torrent",
                   "The Office Season 6 Episode 04", 
                   "The.Office.S06E04.HDTV.XviD-LOL.[VTV].avi");
        
        e = getEpisode("It's Always Sunny in Philadelphia", 5, 4);
        matchesAll(e, "Its.Always.Sunny.In.Philadelphia.S05E04.WS.PDTV.XviD-SYS.avi");
        
        e = getEpisode("Lost", 4, 1);
        matchesAll(e, "Lost - 4x01 - The beginning of the end");
        matchesNone(e, "Lost - 4x11 - The beginning of the end", "Lost - 4x11");
        
        e = getEpisode("The Office", 7, 24, "");
        matchesAll(e, "The.Office.S07E24.HDTV.XviD-LOL.[VTV].avi");        
    }
    
    protected void matchesAll(Episode episode, String... inputs)
    {
        matches(episode, true, inputs);
    }
    
    protected void matchesNone(Episode episode, String... inputs)
    {
        matches(episode, false, inputs);
    }
    
    private void matches(Episode episode, boolean expectation, String... inputs)
    {
        for (String input : inputs)
        {
            if (expectation != episode.matches(input))
            {
                String format = "expected input ''{0}'' to {1}match show ''{2}''";
                
                throw new AssertionFailedError(MessageFormat.format(
                    format, input, expectation ? "" : "NOT ", episode.getIdString()));
            }
        }
    }
    
}
