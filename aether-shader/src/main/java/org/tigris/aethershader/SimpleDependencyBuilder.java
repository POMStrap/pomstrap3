package org.tigris.aethershader;

import java.util.ArrayList;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.tigris.pomstrap.SimpleDependency;

public class SimpleDependencyBuilder
{
    
    public static void main(String[]args ) throws Exception
    {
        new SimpleDependencyBuilder().build( args[0], "run" );
    }
    
    private static RepositorySystem newRepositorySystem()
    throws Exception
    {
        return new DefaultPlexusContainer().lookup( RepositorySystem.class );
    }
    
    private static RepositorySystemSession newSession( RepositorySystem system )
    {
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();

        LocalRepository localRepo = new LocalRepository( System.getProperty( "user.home" ) + "/.m2/repository" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );

        return session;
    }
    
    public SimpleDependency build( String artifact, String scope ) throws Exception
    {
        RepositorySystem repoSystem = newRepositorySystem();

        RepositorySystemSession session = newSession( repoSystem );
        
        Dependency dependency = new Dependency( new DefaultArtifact( artifact ), scope );

        RemoteRepository central = new RemoteRepository( "central", "default", "http://repo1.maven.org/maven2/" );

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( dependency );
        collectRequest.addRepository( central );
        DependencyNode node = repoSystem.collectDependencies( session, collectRequest ).getRoot();
        repoSystem.resolveDependencies( session, node, null );
        
        return buildDependencyTree( node );

        //PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
        //node.accept( nlg );
        //System.out.println( nlg.getClassPath() );
    }
    
    
    public static SimpleDependency buildDependencyTree( DependencyNode node )
    {
        
        SimpleDependency dependency = new SimpleDependency();
        
        Artifact artifact = node.getDependency().getArtifact();
        dependency.groupId = artifact.getGroupId();
        dependency.artifactId = artifact.getArtifactId();
        dependency.version = artifact.getVersion();
        dependency.file = artifact.getFile();
        
        dependency.childs = new ArrayList<SimpleDependency>();
        for( DependencyNode child : node.getChildren() )
        {
            dependency.childs.add( buildDependencyTree( child ) );
        }
        
        return dependency;
    }
    
}
