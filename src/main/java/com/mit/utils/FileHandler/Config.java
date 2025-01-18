package utils.FileHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public Path config_path;
    private String name = "";
    private String email = "";

    public Config(Path rootPath){
        makeFile(rootPath);
        config_path = rootPath.resolve(".mit/config");
    }
    public void makeFile(Path parent_path){
        if(!Files.exists(parent_path.resolve(".mit/config"))){
            File fu = new File(parent_path);
            fu.createFile("config", parent_path.resolve(".mit"));
        }
    }
    public String getName(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }
    public void createEntries(String name,String email){
        File fu = new File(config_path);
        if(exist()){
            List<String> data = new ArrayList<>();
            data.add(name);
            data.add(email);
            this.name = name;
            this.email = email; 
            fu.writeData(data);
        }
    }
    public void getData(){
        File fu = new File(config_path);
        List<String> config_data = fu.readData();
        if (exist() && !config_data.isEmpty()){
            this.name = config_data.get(0);
            this.email = config_data.get(1);
        }
    }
    public boolean exist(){
        return Files.exists(config_path);
    }
}
