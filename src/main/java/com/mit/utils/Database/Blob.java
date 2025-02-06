package utils.Database;

public class Blob extends MitObjects{

    public Blob(String data){
        this.data = data;
    }
    @Override
    public String type(){
        return "blob";
    }
    public byte[] toBytes(){
        return this.data.getBytes(UTF_8);
    }

}
