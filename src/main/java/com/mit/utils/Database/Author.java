package utils.Database;

public class Author {
    public String name;
    public String email;
    public Author(String name,String email){
        this.email = email;
        this.name = name;
    }
    @Override
    public String toString(){
        return String.format("%s <%s>",name,email);
    }
}
