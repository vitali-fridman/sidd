// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profiles.manager.document;

import java.util.Collection;
import com.vontu.directorycrawler.VontuFileException;
import com.vontu.directorycrawler.VontuFile;
import com.vontu.model.data.DocSourceDoc;

public interface IDocSourceDocStore
{
    void add(DocSourceDoc p0, VontuFile p1);
    
    DocSourceDocWrapper update(VontuFile p0) throws VontuFileException, InterruptedException;
    
    boolean contains(VontuFile p0) throws VontuFileException, InterruptedException;
    
    Collection<DocSourceDocWrapper> getSortedDocuments();
}
