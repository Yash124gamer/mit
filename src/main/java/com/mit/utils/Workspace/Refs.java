package utils.Workspace;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public String get_prevoius_tree(){
        String data = read_head();
        Database db = new Database(pathname.resolve("objects"));
        if (data == null || data.equals("")){
            return "";
        }
        byte[] commitBytes = db.readObject(data);
        byte[] prevTree = new byte[40];
        int pointer=0;
        while(commitBytes[pointer] != (byte)' '){
            pointer++;
        }
        System.arraycopy(commitBytes, pointer+1, prevTree, 0, 40);
        return new String(prevTree , StandardCharsets.UTF_8);
    }
}
