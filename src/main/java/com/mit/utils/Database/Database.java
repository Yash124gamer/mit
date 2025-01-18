package utils.Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import utils.FileHandler.File;

public class Database {
    Path path;
    private static final char[] TEMP_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final Random RANDOM = new Random();

    public Database(Path path){
        this.path = path;
    }

    public void store(MitObjects obj){
        String s = obj.toString();
        byte[] byteArray = s.getBytes(StandardCharsets.UTF_8);
        String content = obj.type()+" "+byteArray.length+"\0"+s;
        System.out.println(content);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(content.getBytes());
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b)); // Format each byte as a two-character hex string
            }
            obj.setOid(hashString.toString());
            write_object(obj.getOid(), content);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    private void write_object(String Oid,String content){
        File fu = new File(path);
        String dir = Oid.substring(0, 2);
        String file = Oid.substring(2);
        String tempname = tempFileName();
        fu.createDirectories(new String[]{dir});
        fu.createFile(tempname,path.resolve(dir));
        byte[] compressedContent = compress(content);
        fu.writeData(compressedContent, path.resolve(dir).resolve(tempname));
        fu.renameFile(path.resolve(dir).resolve(tempname),path.resolve(dir).resolve(file));
    }
    private String tempFileName() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            id.append(TEMP_CHARS[RANDOM.nextInt(TEMP_CHARS.length)]);
        }
        return id.toString();
    }
    private byte[] compress(String content) {
        try {
            // Convert the content string to bytes
            byte[] input = content.getBytes("UTF-8");

            // Create a Deflater with the BEST_SPEED level
            Deflater deflater = new Deflater(Deflater.BEST_SPEED);
            deflater.setInput(input);
            deflater.finish();

            // Create a buffer for the compressed data
            byte[] buffer = new byte[1024];
            int compressedDataLength;

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while (!deflater.finished()) {
                    compressedDataLength = deflater.deflate(buffer);
                    outputStream.write(buffer, 0, compressedDataLength);
                }
                deflater.end();
                // Return the compressed byte array
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while compressing data", e);
        }
    }
    public static String decompress(byte[] compressedData){
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(compressedData), inflater)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inflaterInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toString("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error while decompressing data", e);
        } finally {
            inflater.end();
        }
    }

}
