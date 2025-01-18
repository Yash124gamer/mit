package utils.Database;

public class Commit extends MitObjects{
    private String message,parent;
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
        return "Commit";
    }
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("tree "+tree.getOid()+"\n");
        if(parent != null){
            str.append("parent "+parent+"\n");
        }
        str.append("author "+author.toString()+"\n");
        str.append(message+"\n");
        return str.toString();
    }
}
