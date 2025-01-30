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
    // Build the tree from the list of entries
    public static Tree build(List<Entry> entries) {
        Tree root = new Tree(null);
        for (Entry entry : entries) {
            root.add_entry(entry.parent_directory(), entry,root);
        }
        return root;
    }
    @Override
    public String type(){
        return "tree";
    }
    private String mode(){
        return "40000";
    }
    // Add the entry to the appropriate place in the tree structure
    public void add_entry(List<Path> parents, Entry entry,Tree currentTree) {
        if(parents.isEmpty()){
            currentTree.entries.put(entry.basename(), entry);
        }
        else{
            MitObjects obj = currentTree.entries.get(parents.get(0).getFileName());
            if (obj instanceof Entry){
                Tree subTree = new Tree();
                add_entry(parents.subList(1,parents.size()), entry, subTree);
                currentTree.entries.put(parents.get(0).getFileName(),subTree);
            }else if(obj instanceof Tree){
                Tree subtree = (Tree)obj;
                add_entry(parents.subList(1,parents.size()), entry, subtree);
            }
        }
    }
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
        ByteBuffer buffer = ByteBuffer.allocate(header.length()+20);
        buffer.put(header.getBytes());
        buffer.put(hex_to_byte(value.getOid()));
        // buffer.put("\0".getBytes());
        return buffer.array();
    }
    public byte[] toBytes(){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for(Map.Entry<Path, MitObjects> entry : entries.entrySet()){
            MitObjects value = entry.getValue();
            try {
                if (value instanceof Tree){
                    buffer.write(tree_bytes(entry.getKey().getFileName(),(Tree)value));
                }else{
                    byte[] temp = ((Entry)value).toBytes();
                    // buffer.write(((Entry)value).toBytes());
                    buffer.write(temp, 0, temp.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buffer.toByteArray();
    }
}