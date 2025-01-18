package utils.Database;

public class Blob extends MitObjects{

    public Blob(String data){
        this.data = data;
    }
    @Override
    public String type(){
        return "blob";
    }
    public String toString(){
        return this.data.toString();
    }

}
