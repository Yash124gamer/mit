package utils.Workspace;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class entry {
    final int REGULAR_MODE = 0100644;       // Octal Value
    final int EXECUTABLE_MODE = 0100755;    // Octal Value
    final int MAX_FILE_SIZE = 0xfff;        
    public entry_fields fields = new entry_fields();

    public entry(){

    }
    public entry(long Mtime ,long Ctime,int mode,int size,String Oid,short flag,String path){
        fields.CTIME = Ctime;
        fields.MTIME = Mtime;
        fields.MODE = mode;
        fields.SIZE = size;
        fields.OID = Oid;
        fields.FLAG = flag;
        fields.path = path;
    }
    public entry create(Path name,String Oid,BasicFileAttributes stat){
        fields.path = name.toString();
        fields.FLAG = (short)Math.min(fields.path.getBytes().length, MAX_FILE_SIZE);
        fields.CTIME = stat.creationTime().toMillis();
        fields.MTIME = stat.lastModifiedTime().toMillis();
        if(stat.isRegularFile()){
            fields.MODE = REGULAR_MODE;
        }else{
            fields.MODE = EXECUTABLE_MODE;
        }
        fields.SIZE = (int)stat.size();
        fields.OID = Oid;
        return this;
    }

}
