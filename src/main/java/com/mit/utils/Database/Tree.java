package utils.Database;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
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
     // Print the tree (for testing)
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