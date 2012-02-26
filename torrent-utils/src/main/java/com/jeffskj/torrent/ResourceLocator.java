package com.jeffskj.torrent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

public class ResourceLocator {
    private static final ClassLoader CLASS_LOADER = ResourceLocator.class.getClassLoader();
    private static final String PROPERTY_NAME = "torrents.config.dir";
    private static File baseDir;
    
    static {
        if (System.getProperty(PROPERTY_NAME) != null) {
            baseDir = new File(System.getProperty(PROPERTY_NAME));
        }
    }
    
    public static File locate(String name) {
        if (baseDir != null) {
            File f = new File(baseDir, name);
            if (!f.exists()) {
                return locateFromClasspath(name);
            }
            return f;
        }
        return locateFromClasspath(name);
    }

    private static File locateFromClasspath(String name) {
        return FileUtils.toFile(CLASS_LOADER.getResource(name));
    }
    
    public static InputStream locateAsStream(String name) throws FileNotFoundException {
        File file = locate(name);
        if (file == null) {
            return null;
        }
        return new BufferedInputStream(new FileInputStream(file));
    }
}
