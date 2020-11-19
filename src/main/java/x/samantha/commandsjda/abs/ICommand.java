package x.samantha.commandsjda.abs;

import net.dv8tion.jda.api.Permission;
import x.samantha.commandsjda.annotations.Command;
import x.samantha.commandsjda.annotations.Description;
import x.samantha.commandsjda.annotations.Permissions;
import x.samantha.commandsjda.managers.CommandManager;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public interface ICommand {

    /**
     * The method called when someone tries to execute a command
     * @param ctx
     * The context in which the command is executed
     */
    void onCall(CommandContext ctx);

    /**
     * The name of this command
     * @return String
     */
    default String getName() {
        return this.getClass().getAnnotation(Command.class).name();
    }

    /**
     * A list of aliases that can activate this command
     * @return List<String>
     */
    default List<String> getAliases() {
        return Arrays.asList(this.getClass().getAnnotation(Command.class).aliases());
    }

    /**
     * The description of this command
     * @return String
     */
    default String getDescription() {
        return this.getClass().getAnnotation(Description.class).description();
    }

    /**
     * The permissions the user and bot need to execute this command
     * @return List<Permission>
     */
    default List<Permission> getPermissions() {
        return Arrays.asList(this.getClass().getAnnotation(Permissions.class).permissions());
    }

    /**
     * The module this command belongs to
     * @return Module
     */
    default Module getModule() {
        AtomicReference<Module> m = new AtomicReference<>(new Module("NO MODULE"));
        CommandManager.getModuleRegistry().forEach((name, module) -> {
            if (module.commands.contains(this)) m.set(module);
        });
        return m.get();
    }
}
