package utils.Database;

public abstract class MitObjects {
    public String data;
    private String Oid;

    public String getOid(){
        return this.Oid;
    }
    public void setOid(String oid){
        this.Oid = oid;
    }
    public String type(){
        return "";
    }
    public String toString(){
        return "";
    }
}
