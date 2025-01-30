package utils.Database;

public abstract class MitObjects {
    public String data;
    private String Oid;

    public MitObjects(){
        
    }

    public String getOid(){
        return this.Oid;
    }
    public void setOid(String oid){
        this.Oid = oid;
    }
    public String type(){
        return "";
    }
    public byte[] toBytes(){
        return new byte[]{};
    }
    public byte[] hex_to_byte(String hash){
        byte[] byteArray = new byte[21]; // Array to store 20 bytes
        for (int i = 0; i < hash.length(); i += 2) {
            String hexPair = hash.substring(i, i + 2);
            byteArray[i / 2] = (byte) Integer.parseInt(hexPair, 16); // Parse and store byte
        }
        byteArray[20] = 0;
        return byteArray;
    }
}
