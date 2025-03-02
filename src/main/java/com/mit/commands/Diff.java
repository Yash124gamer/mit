package commands;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import core.FileDiff;
import utils.Database.Entry;
import utils.Database.Tree;

public class Diff {
    private Repository repo;
    private Path currentPath;
    private FileDiff diff;

    public Diff(Path path){
        currentPath = path;
        repo = new Repository(path);
        repo.initialise();
    }
    private boolean is_valid_hash(String hash){
        if (!(hash.length() == 40)){
            return false;
        }
        Path filePath = currentPath.resolve(".mit/objects/"+hash.substring(0, 2)+"/"+hash.substring(2));
        return Files.exists(filePath);
    }
    public void run(String[] args){
        String commit_id = repo.REFS.read_head();
        if (args.length >= 2){
            if (is_valid_hash(args[1]))
                commit_id = args[1];
            else
                System.out.println("Please provide a valid commit ID");
        }
        List<Entry> commit_Entries = new Tree().parse(repo.DATABASE.readObject(repo.REFS.get_prevoius_tree(commit_id)), "");
        List<Path> working_list = repo.WORKSPACE.listFiles();
        boolean change = false;
        for (Entry entry : commit_Entries) {
            // If file in working area and commit area are similiar then skip
            if (working_list.contains(entry.getPath())) {
                if (!(entry.getOid().equals(repo.WORKSPACE.get_fileHash(entry.getPath())))) {
                    change = true;
                    diff = new FileDiff();
                    List<String> current_file_lines = ToLines(repo.DATABASE.readObject(entry.getOid()));
                    List<String> old_file_lines = diff.readFile(currentPath.resolve(entry.getName()).toString());
                    List<int[]> shortestEdit = diff.shortestLineEdit(old_file_lines, current_file_lines);
                    diff.backtrack(shortestEdit, current_file_lines.size(), old_file_lines.size(), diff::changes);
                    diff.print(entry.getName(), current_file_lines, old_file_lines);
                }
                working_list.remove(entry.getPath());
            }
        }
        if (!working_list.isEmpty()) {
            System.out.println("\n\033[1;32mNew Files Added:\033[0m"); // Bold Green Heading
            System.out.println("----------------------------------");
            for (Path path : working_list) {
                System.out.println("\033[32m + " + path.toString() + "\033[0m");
            }
            System.out.println("----------------------------------");
        }
        if(!change){
            System.out.println("No differences");
        }
    }
    private List<String> ToLines(byte[] decompressedData) {
        String content = new String(decompressedData, StandardCharsets.UTF_8);
        return Arrays.asList(content.split("\n"));
    }   
}
