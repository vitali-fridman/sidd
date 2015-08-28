// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindex;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamFactory
{
    InputStream getInputStream() throws IOException;
    
    String name();
}
