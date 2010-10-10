package org.tigris.pomstrap;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Bootstrap
{
    public static void main( String[] args )  throws Exception
    {
        new Bootstrap().bootstrap();
    }
    
    public void bootstrap() throws Exception
    {
        
        // build class loader for aether and get dependency
        
        File aetherShaderJar = new File( System.getProperty( "user.home" )+"/.m2/repository/org/tigris/pomstrap/aethershader/1.0-SNAPSHOT/aethershader-1.0-SNAPSHOT.jar" );
        URL[] urls = { aetherShaderJar.toURI().toURL() };
        
        ClassLoader prevClassloader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = new URLClassLoader( urls, Bootstrap.class.getClassLoader() );
        Thread.currentThread().setContextClassLoader( classLoader );
        
        Class<?> classz = classLoader.loadClass( "org.tigris.aethershader.SimpleDependencyBuilder" );
        Constructor<?> ctor = classz.getConstructor( (Class[])null );
        Object o = ctor.newInstance( (Object[])null );
        
        Object[] methodArgs = { "net.ndjin:model-ndjin:1.1", "run" };
        Class<?>[] parametersType = { String.class, String.class };
        
        Method method = o.getClass().getMethod( "build", parametersType );
        SimpleDependency dep = (SimpleDependency)method.invoke( o, methodArgs );
        
        Thread.currentThread().setContextClassLoader( prevClassloader );

        printNode( dep, 0 );

    }
    
    public static void printNode( SimpleDependency dep, int deep )
    {
        for( int i = 0; i<deep; i++ ) System.out.print( "    " );
        System.out.print( dep.toString() );
        System.out.println( " --- " + dep.file );
        
        for( SimpleDependency child : dep.childs )
        {
            printNode( child, deep+1 );
        }
    }

}
