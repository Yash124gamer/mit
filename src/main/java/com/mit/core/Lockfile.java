package core;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class Lockfile {
    private Path path,lockpath;
    private FileChannel lock=null;

    public Lockfile(Path path){
        this.path = path.resolve("HEAD");
        this.lockpath = path.resolve("HEAD.lock");
    }
    public boolean isLocked(){
        if(lock == null)
            return false;
        else
            return true;
    }
    public boolean aquire_lock(){
        if(!isLocked()){
            try {
                FileChannel fileChannel = FileChannel.open(
                    lockpath,
                    StandardOpenOption.CREATE,   // CREATE the file if it does not exist
                    StandardOpenOption.WRITE,    // Open the file for writing
                    StandardOpenOption.CREATE_NEW // CREATE_NEW ensures the file is created exclusively
                );
                this.lock = fileChannel;
                return true;
            }catch(Exception e){
                return false;
            }
        }else{
            return false;
        }
        
    }
    public void write(ByteBuffer data){
        if(isLocked()){
            try {
                while(data.hasRemaining()){
                    lock.write(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void commit(){
        if(isLocked()){
            try {
                lock.close();
                Files.move(lockpath, path, StandardCopyOption.REPLACE_EXISTING);                
                lock = null;
            } catch (Exception e) {
                
            }
            
        }
    }
}
