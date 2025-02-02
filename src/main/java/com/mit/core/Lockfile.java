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

    public Lockfile(Path path,String file){
        this.path = path.resolve(file);
        this.lockpath = path.resolve(file+".lock");
    }
    public boolean isLocked(){
        if(lock == null)
            return false;
        else
            return true;
    }
    public boolean aquire_lock(){
        if(!isLocked()){
            // Checking if a .lock file already exist 
            if(Files.exists(lockpath)){
                throw new RuntimeException("Another git process seems to be running in this repository, e.g.\r\n" + //
                                    "an editor opened by 'git commit'. Please make sure all processes\r\n" + //
                                    "are terminated then try again. If it still fails, a git process\r\n" + //
                                    "may have crashed in this repository earlier:\r\n" + //
                                    "remove the file manually to continue"
                            );
            }
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
                e.printStackTrace();
            }
            
        }
    }
}
