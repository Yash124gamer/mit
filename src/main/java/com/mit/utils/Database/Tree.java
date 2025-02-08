package utils.Database;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class Tree extends MitObjects{
    public Map<Path, MitObjects> entries = new HashMap<>();

    public Tree(String oid){
        setOid(oid);
    }
    public Tree(){

    }
    @Override
    public String type(){
        return "tree";
    }
    private String mode(){
        return "40000";
    }
    // returns A new map with entries sorted by the name of their paths
    public Map<Path, MitObjects> sortEntriesByPathName(Map<Path, MitObjects> entries) {
        return entries.entrySet()
                .stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().getFileName().toString()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // Merge function (not needed here but required for toMap)
                        LinkedHashMap::new // Use LinkedHashMap to maintain sorted order
                ));
    }
    // Function that will recursively traverse the tree and execute a given command on it.
    public void Traverse(Consumer<Tree> block){
        for(Map.Entry<Path, MitObjects> entry : entries.entrySet()){
            MitObjects value = entry.getValue();
            if (value instanceof Tree){
                ((Tree) value).Traverse(block);
            }
        }
        block.accept(this);
    }
    private byte[] tree_bytes(Path file_name,Tree value){
        String header = mode()+" "+file_name+"\0";
        ByteBuffer buffer = ByteBuffer.allocate(header.length()+21);
        buffer.put(header.getBytes());
        buffer.put(hex_to_byte(value.getOid()));
        return buffer.array();
    }
    // return The byte array representation of the entries of the tree
    public byte[] toBytes(){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for(Map.Entry<Path, MitObjects> entry : entries.entrySet()){
            MitObjects value = entry.getValue();
            try {
                if (value instanceof Tree){
                    buffer.write(tree_bytes(entry.getKey().getFileName(),(Tree)value));
                }else{
                    byte[] temp = ((Entry)value).toBytes();
                    buffer.write(temp, 0, temp.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buffer.toByteArray();
    }
    // Builds a tree structure from a list of entries
    public void buildTree(List<Entry> entryList) {
        for (Entry entry : entryList) {
            Path filePath = entry.getPath();
            Path parentPath = filePath.getParent(); // Extract folder name if exists

            if (parentPath == null) {
                // No parent -> Directly add the entry to ROOT TREE
                entries.put(filePath, entry);
            } else {
                // Folder exists -> Add it to the correct Tree
                Tree folderTree = (Tree) entries.computeIfAbsent(parentPath, k -> new Tree());
                if (folderTree instanceof Tree) {
                    folderTree.entries.put(filePath.getFileName(), entry);
                }
            }
        }
    }
    public List<Entry> parse(byte[] data,String parent){
        List<Entry> entry_list = new ArrayList<>();
        int pointer = 0;
        while (pointer < data.length){
            boolean isDirectory = false;
            String mode;
            // Reading File mode
            if (data[pointer] == (byte)'4'){
                isDirectory = true;
                mode = read_string(data, pointer, 5);
                pointer+=6;
            }else{
                mode = read_string(data, pointer, 6);
                pointer+=7;
            }
            int end = pointer;
            // Reading File path
            while (end < data.length && data[end] != 0) {
                end++;
            }
            String file_name = new String(data, pointer, end - pointer, UTF_8);
            pointer+=(end-pointer)+1;
            // Reading File object Id
            String oid = toHex(Arrays.copyOfRange(data, pointer, pointer + 20));
            pointer+=21;
            if(isDirectory){
                // Database db = new Database(Paths.get(System.getProperty("user.dir")+"/.mit/objects"));
                Database db = new Database(Paths.get("D:/workspace/first-repo/.mit/objects"));
                entry_list.addAll(parse(db.readObject(oid),file_name+"/"));
            }else{
                entry_list.add(new Entry(Paths.get(parent+file_name), oid , mode));
            }
        }
        return entry_list;
    }
    private String toHex(byte[] data) {
        StringBuilder hexString = new StringBuilder(40);
        for (byte b : data) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }    
    private String read_string(byte[] data , int offset , int size){
        byte[] temp = new byte[size];
        System.arraycopy(data, offset, temp, 0, size);
        return new String(temp , UTF_8);
    }
    public void printTree(String treeName) {
        System.out.println(treeName + " TREE:");
        for (Map.Entry<Path, MitObjects> entry : entries.entrySet()) {
            if (entry.getValue() instanceof Entry) {
                System.out.println(entry.getKey() + " -> Entry(" + ((Entry) entry.getValue()).getOid() + ")");
            } else {
                ((Tree)entry.getValue()).printTree(entry.getKey().toString());
            }
        }
        System.out.println();
    }
}