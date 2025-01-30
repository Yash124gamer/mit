package utils.Database;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Entry extends MitObjects{
    final String REGULAR_MODE = "100644";
    final String EXECUTABLE_MODE = "100755";
    private Path name;
    public String stat;

    public Entry(Path name,String Oid){
        setOid(Oid);
        this.name = name;
        this.stat = mode();
    }
    public Entry(Path name,String Oid,boolean isDirectory){
        setOid(Oid);
        this.name = name;
        this.stat = "40000";
    }
    public String getName(){
        return this.name.toString();
    }
    //check if the filePath specified in name is a regular file or executable and update the stat of the file accordingly
    public String mode(){
        if(Files.isExecutable(this.name))
            return EXECUTABLE_MODE;
        else {
            return REGULAR_MODE;
        }
    }
    public List<Path> parent_directory(){ 
        List<Path> intermediatePaths = new ArrayList<>();

        // If the path has more than one element, process it
        int nameCount = name.getNameCount();
        if (nameCount > 1) {
            for (int i = 1; i < nameCount; i++) {
                // Add subpath progressively up to the second-to-last component
                intermediatePaths.add(name.subpath(0, i));
            }
        }

        return intermediatePaths;    
    }
    // Retrieves the last element of the path
    public Path basename(){ 
        return name.getFileName();
    }
    public byte[] toBytes(){
        String header = stat+" "+name.getFileName()+"\0";
        ByteBuffer buffer = ByteBuffer.allocate(header.length()+21);
        buffer.put(header.getBytes());
        buffer.put(hex_to_byte(getOid()));
        return buffer.array();
    }
}
