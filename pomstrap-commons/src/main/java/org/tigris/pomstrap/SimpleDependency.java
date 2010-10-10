package org.tigris.pomstrap;

import java.io.File;
import java.util.List;


public class SimpleDependency
{
    public List<SimpleDependency> childs;
    
    
    public String groupId;
    public String artifactId;
    public String version;
    public File file;
    
    @Override public String toString()
    {
        return groupId+":"+artifactId+":"+version;
    }
}
