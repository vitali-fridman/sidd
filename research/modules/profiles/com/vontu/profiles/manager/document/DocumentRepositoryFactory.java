// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import com.vontu.profiles.common.ProfilesException;
import com.vontu.directorycrawler.ProfilerFileSystemCrawler;
import com.vontu.model.data.InfoSource;
import com.vontu.model.Model;

public class DocumentRepositoryFactory
{
    public DocumentRepository createDocumentRepository(final Model model, final InfoSource infoSource, final ProfilerFileSystemCrawler crawler) throws ProfilesException, InterruptedException {
        return new DocumentRepository(model, infoSource, crawler);
    }
}
