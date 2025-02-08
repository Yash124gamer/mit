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
    public void parse(byte[] data){
        this.data = new String(data,UTF_8);
    }
    public String get_parsedData(byte[] data){
        return new String(data,UTF_8);
    }

}
