package utils.Workspace;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import utils.FileHandler.File;
import core.Lockfile;

public class Index {
    private Path path;
    private Map<String,entry> entries = new HashMap<>();
    private Lockfile lock;
    private boolean changed;

    public Index(Path path){
        checkFile(path);
        this.path = path.resolve("index");
        this.lock = new Lockfile(path,"index");
    }
    private void checkFile(Path path){
        if(!Files.exists(path.resolve("index"))){
            File fu = new File(path);   
            fu.createFile("index", path);
        }
    }
    public void add(Path path,String Oid,BasicFileAttributes stat){
        entry entry = new entry();
        entry = entry.create(path, Oid,stat);
        this.entries.put(path.toString(), entry);
    }
    public void write_updates(){
        if(lock.aquire_lock()){
            ByteBuffer header = ByteBuffer.allocate(8);
            header.put("DIRC".getBytes());      // DIRC ,              4 bytes
            header.putInt(this.entries.size()); // number of entries , 4 bytes
            header.flip();
            lock.write(header);
            entries.forEach((key,value)->{
                ByteBuffer bf = ByteBuffer.wrap(value.fields.toByte());

                lock.write(bf);
            });
            lock.commit();
        }
    }
    public void load_update(){
        if(lock.aquire_lock()){
            load();
        }
    }
    private void load(){
        try {
            byte[] fileData = Files.readAllBytes(path);
            int entry_count = read_header(fileData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private int read_header(byte[] data){
        return ByteBuffer.wrap(data, 4, 4).getInt();
    }
    private int read_fileName(byte[] data){
        return ByteBuffer.wrap(data, 4, 4).getInt();
    }

    public static void main(String[] args){

    }
}
