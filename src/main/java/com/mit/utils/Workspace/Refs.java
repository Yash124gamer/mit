package utils.Workspace;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import core.Lockfile;
import utils.Database.Database;

public class Refs {
    private Path pathname;
    
    public Refs(Path name){
        this.pathname = name;
    }
    public Path head_path(){
        return this.pathname.resolve("HEAD");     
    }
    public void update_head(String content){
        try {
            if(!Files.exists(head_path())){
                Files.createFile(head_path());
            }
            Lockfile lock = new Lockfile(this.pathname,"HEAD");
            ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
            try {
                if(lock.aquire_lock()){
                    lock.write(buffer);
                    lock.commit();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }
        } catch (Exception e) {
            
        }
    }
    public String read_head(){
        if(Files.exists(head_path())){
            try {
                return new String(Files.readAllBytes(head_path()),"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }
    // Funtion that will return the previous commit's tree object id
    public String get_prevoius_tree(String commit_id){
        Database db = new Database(pathname.resolve("objects"));
        if (commit_id == null || commit_id.equals("")){
            return "";
        }
        byte[] commitBytes = db.readObject(commit_id);
        byte[] prevTree = new byte[40];
        int pointer=0;
        while(commitBytes[pointer] != (byte)' '){
            pointer++;
        }
        System.arraycopy(commitBytes, pointer+1, prevTree, 0, 40);
        return new String(prevTree , StandardCharsets.UTF_8);
    }

    public String get_previous_commit(String commit_id){
        Database db = new Database(pathname.resolve("objects"));
        byte[] commit_bytes = db.readObject(commit_id);
        int null_count = 0;
        int pointer = 0;
        while (null_count >= 2){
            if(commit_bytes[pointer] == 0)
                null_count++;
        }
        if(!(commit_bytes[pointer] == (byte)'p')){
            return "";
        }
        while (commit_bytes[pointer] != 0){
            pointer++;
        }
        return new String(Arrays.copyOfRange(commit_bytes, pointer+1, pointer+41) , StandardCharsets.UTF_8);
    }
}
