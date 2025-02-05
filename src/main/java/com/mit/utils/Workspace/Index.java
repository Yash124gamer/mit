package utils.Workspace;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import utils.Database.Entry;   
import utils.FileHandler.File;
import core.Lockfile;

public class Index {
    private Path path;
    // private Map<String,entry> entries = new HashMap<>();
    public Map<String, entry> entries = new TreeMap<>();
    private Lockfile lock;

    public Index(Path path){
        checkFile(path);
        this.path = path.resolve("index");
        this.lock = new Lockfile(path,"index");
    }
    // To check if Index file exists or not and create if does not  exist
    private void checkFile(Path path){
        if(!Files.exists(path.resolve("index"))){
            File fu = new File(path);   
            fu.createFile("index", path);
        }
    }
    // Function to add entries to the List
    public void add(Path path,String Oid,BasicFileAttributes stat){
        entry entry = new entry();
        entry = entry.create(path, Oid,stat);
        this.entries.put(path.toString(), entry);
    }
    // Funtion to write all the entries to the Index file
    public void write_updates(){
        try {
            if(lock.aquire_lock()){
                ByteBuffer header = ByteBuffer.allocate(8);
                header.put("DIRC".getBytes());      // DIRC(DirectorrCahche) ,  4 bytes
                header.putInt(this.entries.size()); // number of entries ,      4 bytes
                header.flip();
                lock.write(header);
                entries.forEach((key,value)->{
                    ByteBuffer bf = ByteBuffer.wrap(value.fields.toByte());
                    lock.write(bf);
                });
                lock.commit();
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
    // Function that will read entries from Index file and return them as list with their respective Path
    public TreeMap<String,entry> load_update(){
        TreeMap<String,entry> list = new TreeMap<>();
        try {
            byte[] fileData = Files.readAllBytes(path);
            if (fileData.length == 0){
                return list;
            }
            int entry_count = read_entryCount(fileData);
            int pointer = 8;
            for (int i=0;i<entry_count;i++){
                long Mtime = read_Mtime(fileData,pointer);
                pointer+=8;
                long Ctime = read_Mtime(fileData,pointer);
                pointer+=8;
                int mode = read_mode(fileData,pointer);
                pointer+=4;
                String id = read_oid(fileData, pointer);
                pointer+=20;
                int size = read_mode(fileData,pointer);
                pointer+=4;
                short flag = (short)read_fileLength(fileData, pointer-44);
                pointer+=2;
                String fileName = read_fileName(fileData, pointer, flag);
                pointer+=flag;
                list.put(fileName, new entry(Mtime, Ctime, mode, size, id, flag, fileName));
            }                            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // Funtion that will read FilePath and their Object Id from the Index File
    public List<Entry> load(){
        List<Entry> entries = new ArrayList<>();
        try {
            byte[] fileData = Files.readAllBytes(path);
            if (fileData.length == 0){    
                return entries;              // Return empty list if index file is empty
            }
            int entry_count = read_entryCount(fileData);
            int pointer = 8;
            for (int i=0;i<entry_count;i++){
                int fileLength = read_fileLength(fileData, pointer);
                entries.add(new Entry(Paths.get(read_fileName(fileData, pointer+46, fileLength)),read_oid(fileData, pointer+20)));
                pointer+=fileLength+46;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }
    // Function that will return 40 digit hexadecimal value Object Id of an entry from Index file
    private String read_oid(byte[] data,int pointer){
        byte[] idBytes = new byte[20];
        System.arraycopy(data, pointer, idBytes, 0, 20);       // Retrieving Byte value of Object Id
        
        return byte_to_hex(idBytes);
    }
    // Function to convert (20)bytes into their respective (40)Hexadecimal digits 
    private String byte_to_hex(byte[] id) {
        StringBuilder hexString = new StringBuilder(id.length * 2);
        for (byte b : id) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
    // Function that will read name of the file of an entry from the Index File
    private String read_fileName(byte[] data,int pointer , int length){
        char[] filenameChars = new char[length];
        for (int i = 0; i < length; i++) {
            filenameChars[i] = (char) data[pointer + i];
        }
        return new String(filenameChars);
    }
    // Function that returns number of Entries in an Index File
    private int read_entryCount(byte[] data){
        return ByteBuffer.wrap(data, 4, 4).getInt();
    }
    // Funtion that returns length of the File name of an entry 
    private int read_fileLength(byte[] data,int cuurentPointer){
        return ByteBuffer.wrap(data, cuurentPointer+44, 2).getShort();
    }
    // Funtion that returns Last Modified time of a File from Index
    private long read_Mtime(byte[] data,int currentPointer){
        return ByteBuffer.wrap(data,currentPointer,8).getLong();
    }
    private int read_mode(byte[] data,int currentPointer){
        return ByteBuffer.wrap(data,currentPointer,4).getInt();
    }
}
