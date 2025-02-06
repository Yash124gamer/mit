package commands;

import java.nio.file.Path;
import java.nio.file.Paths;

import utils.Database.Database;
import utils.Workspace.Index;
import utils.Workspace.Refs;
import utils.Workspace.Workspace;

public class Repository {
    private Path repository_path;
    public Repository(Path repository_path){
        this.repository_path = repository_path;
    }
    public Repository(String repository_path){
        this.repository_path = Paths.get(repository_path);
    }
    public void initialise(){
        DATABASE  = new Database(repository_path.resolve(".mit/objects"));
        WORKSPACE = new Workspace(repository_path);
        INDEX     = new Index(repository_path.resolve(".mit"));
        REFS      = new Refs(repository_path.resolve(".mit"));
    }

    public  Database  DATABASE;
    public  Workspace WORKSPACE;
    public  Index     INDEX;
    public  Refs      REFS;
}
