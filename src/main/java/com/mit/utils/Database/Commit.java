package utils.Database;

import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.Arrays;

public class Commit extends MitObjects{
    public String message,parent;
    private Tree tree;
    private Author author;

    public Commit(String message ,String parent , Tree tree ,Author author){
        this.message = message;
        this.parent = parent;
        this.tree = tree;
        this.author = author;
    }
    @Override
    public String type(){
        return "commit";
    }
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("tree "+tree.getOid()+"\n");
        if(parent.equals("")){
            str.append("parent "+parent+"\n");
        }
        str.append("author "+author.toString()+"\n");
        str.append(message+"\n");
        return str.toString();
    }
    public byte[] toBytes(){
        byte[] treeBytes = ("tree "+(tree.getOid()+"\0")).getBytes(UTF_8);
        int size = treeBytes.length;
        byte[] AuthorBytes = ("author "+author.toString()+"\0").getBytes(UTF_8);
        size+=AuthorBytes.length;
        byte[] messageBytes = (message+"\0").getBytes(UTF_8);
        size+=messageBytes.length;
        if (!parent.equals("")){
            size+=48;
        }
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(treeBytes);
        if (!parent.equals("")){
            buffer.put(("parent "+parent+"\0").getBytes(UTF_8));
        }
        buffer.put(AuthorBytes);
        buffer.put(messageBytes);
       return buffer.array();
    }
    public void parse(byte[] data,String oid){
        // Database db = new Database(Paths.get(System.getProperty("user.dir")+"/.mit/objects"));
        Database db = new Database(Paths.get("D:/workspace/first-repo/.mit/objects"));
        byte[] treeBytes = new byte[40];
        int pointer=0;
        while(data[pointer] != (byte)' '){
            pointer++;
        }
        System.arraycopy(data, pointer+1, treeBytes, 0, 40);
        String treeID = new String(treeBytes,UTF_8);
        this.tree.setOid(treeID);
        this.tree.buildTree(this.tree.parse(db.readObject(treeID),""));
        this.message = readMessage(data);
        this.parent = read_prev_commit(data,pointer+42);
        this.setOid(oid);
    }
    private String readMessage(byte[] data){
        int pointer = data.length-2;
        while (data[pointer] != (byte)'\0') {
            pointer--;
        }
        return new String((Arrays.copyOfRange(data, pointer, data.length)),UTF_8);
    }
    private String read_prev_commit(byte[] data,int pointer){
        if (data[pointer] != (byte)'p'){
            return "";
        }
        while(data[pointer] != (byte)' '){
            pointer++;
        }
        byte[] prev_commit = new byte[40];
        System.arraycopy(data, pointer+1, prev_commit, 0, 40);
        return new String(prev_commit,UTF_8);
    }
}
