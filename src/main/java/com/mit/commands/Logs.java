package commands;

import java.nio.file.Path;
import utils.Database.Author;
import utils.Database.Commit;
import utils.Database.Tree;


public class Logs {
    private Repository repo;

    public Logs(Path current_path){
        repo = new Repository(current_path);
        repo.initialise();
    }
    public void run(){
        String current_head = repo.REFS.read_head();
        if (current_head.equals("")){
            System.out.println("There are no commits");
            return;
        }
        while (!(current_head.equals(""))){
            Commit commit = new Commit("", "", new Tree(), new Author("", ""));
            commit.parse(repo.DATABASE.readObject(current_head),current_head);
            System.out.printf("%-15s : %s%n", commit.message, commit.getOid());
            current_head = commit.parent;
        }
    }
}
