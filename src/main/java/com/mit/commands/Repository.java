package commands;

import java.nio.file.Path;
import java.nio.file.Paths;

import utils.Database.Database;
import utils.Workspace.Index;
import utils.Workspace.Workspace;

public class Repository {
    private Path repository_path;
    public Repository(Path repository_path){
        this.repository_path = repository_path;
    }
    public Repository(String repository_path){
        this.repository_path = Paths.get(repository_path);
    }

    public final Database  DATABASE  = new Database(repository_path.resolve("/.mit/objects"));
    public final Workspace WORKSPACE = new Workspace(repository_path);
    public final Index     INDEX     = new Index(repository_path.resolve("/.mit"));
}
