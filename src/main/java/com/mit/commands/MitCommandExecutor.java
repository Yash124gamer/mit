package commands;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MitCommandExecutor {
    private final Map<String, Command> commands = new HashMap<>();

    public MitCommandExecutor(Path currentPath) {
        commands.put("init", new Init(currentPath));
        commands.put("add", new Add(currentPath));
        commands.put("commit", new Commit(currentPath));
        commands.put("revert", new Revert(currentPath));
        commands.put("status", new Status(currentPath));
        commands.put("restore", new Restore(currentPath));
        commands.put("logs", new Logs(currentPath));
        commands.put("diff", new Diff(currentPath));
    }

    public void execute(String command, String[] args) {
        Command cmd = commands.get(command);
        if (cmd != null) {
            cmd.run(args);
        } else {
            System.out.println("mit " + command + " is not a mit command");
        }
    }
}

