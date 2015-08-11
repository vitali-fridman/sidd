// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.profileindexer.database;

import com.vontu.nlp.lexer.pattern.SystemPattern;

public interface DatabaseProfileColumn
{
    int index();
    
    SystemPattern systemPattern();
}
