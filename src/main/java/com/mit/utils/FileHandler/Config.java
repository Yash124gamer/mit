package utils.FileHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    public void createEntries(){
        File fu = new File(config_path);
        Scanner scan = new Scanner(System.in);
        if(exist()){
            List<String> data = new ArrayList<>();
            System.out.println("Please Enter your Name");
            String name = scan.nextLine();
            System.out.println("Please Enter your Email");
            String email = scan.nextLine();
            data.add(name);
            data.add(email);
            this.name = name;
            this.email = email; 
            fu.writeData(data);
        }
        scan.close();
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
