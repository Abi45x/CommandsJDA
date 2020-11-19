package x.samantha.commandsjda.abs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module {

    String name;
    Boolean isEnabled;
    List<ICommand> commands = new ArrayList<>();

    public Module(String name) {
        this.name = name;
    }

    Module addCommand(ICommand command) {
        this.commands.add(command);
        return this;
    }

    Module addCommands(ICommand... commands) {
        this.commands.addAll(Arrays.asList(commands));
        return this;
    }

    Module setEnabled(Boolean enabled) {
        this.isEnabled = enabled;
        return this;
    }

    public String getName() {
        return name;
    }

    public Boolean isEnabled() {
        return isEnabled;
    }

    public List<ICommand> getCommands() {
        return commands;
    }
}
