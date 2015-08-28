// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import java.util.logging.Level;
import com.vontu.directorycrawler.VontuFileException;
import com.vontu.profiles.manager.IndexerConfig;
import com.vontu.directorycrawler.ProfilerFileSystemCrawler;
import com.vontu.communication.data.CourseMarshallable;
import java.util.logging.Logger;

public class ProfilerFileSystemCrawlerCreator
{
    private static Logger logger;
    
    public ProfilerFileSystemCrawler createCrawler(final String profileName, final CourseMarshallable course) {
        try {
            final ProfilerFileSystemCrawler crawler = new ProfilerFileSystemCrawler(profileName, course, IndexerConfig.getUseJcifsForIDMIndexing());
            crawler.checkCourseConnectivityAndAccess();
            crawler.start();
            return crawler;
        }
        catch (VontuFileException e2) {
            return null;
        }
        catch (Exception e) {
            ProfilerFileSystemCrawlerCreator.logger.log(Level.SEVERE, "failed checking connectivity", e);
            throw new RuntimeException(e);
        }
    }
    
    static {
        ProfilerFileSystemCrawlerCreator.logger = Logger.getLogger(ProfilerFileSystemCrawlerCreator.class.getName());
    }
}
