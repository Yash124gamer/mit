package utils.Workspace;

import java.nio.ByteBuffer;

public class entry_fields {
    public long MTIME;   // Last Modified Time            8 bytes
    public long CTIME;   // Creation Time                 8 bytes
    public int MODE;     // type of file                  4 bytes
    public int SIZE ;   // size of the file               4 bytes       
    public String OID;   // hash of the object file      20 bytes
    public short FLAG ;  // length of the file path       2 bytes
    public String path;  // name of the file             variable size

    public entry_fields(){

    }
    public String toString(){
        return "Modify time : "+MTIME+"\n"+"Creation time : "+CTIME+"\n"+"Mode : "+MODE+"\n"+"Flag : "+FLAG+"\n"+"Size : "+SIZE+"\n";
    }
    public byte[] toByte(){
        ByteBuffer buffer = ByteBuffer.allocate(46+path.length());
        try {
            buffer.putLong(MTIME);
            buffer.putLong(CTIME);
            buffer.putInt(MODE);
            buffer.put(hex_to_byte(OID));
            buffer.putInt(SIZE);
            buffer.putShort(FLAG);
            buffer.put(path.getBytes());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.array();
    }
    private byte[] hex_to_byte(String hash){
        byte[] byteArray = new byte[20]; // Array to store 20 bytes
        for (int i = 0; i < hash.length(); i += 2) {
            String hexPair = hash.substring(i, i + 2);
            byteArray[i / 2] = (byte) Integer.parseInt(hexPair, 16); // Parse and store byte
        }
        return byteArray;
    }
}

