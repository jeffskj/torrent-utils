package com.jeffskj.torrent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils 
{
    public static String twoDigits(int num)
    {
        return leftPad(String.valueOf(num), 2, '0');
    }
    
    public static String firstMatchingGroup(String regex, String str) {
        return firstMatchingGroup(Pattern.compile(regex), str);
    }
    
    public static String firstMatchingGroup(Pattern regex, String str) {
        Matcher matcher = regex.matcher(str);
        matcher.find();
        try {
            return matcher.group(1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
