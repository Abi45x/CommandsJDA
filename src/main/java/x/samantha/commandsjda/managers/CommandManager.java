package x.samantha.commandsjda.managers;

import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x.samantha.commandsjda.abs.CommandContext;
import x.samantha.commandsjda.abs.ICommand;
import x.samantha.commandsjda.abs.Module;
import x.samantha.commandsjda.annotations.Permissions;

import java.util.*;

public class CommandManager {

    private final Logger log = LoggerFactory.getLogger(CommandManager.class);

    static HashMap<String, ICommand> commandRegistry = new HashMap<>();
    static HashMap<String, Module> moduleRegistry = new HashMap<>();

    public static void onCall(CommandContext ctx) {
        ICommand cmd = ctx.command;

        if (!cmd.getModule().isEnabled()) {
            ctx.channel.sendMessage("The module \"" + cmd.getModule().getName() + "\" is disabled!").queue();
        } else if (cmd.getClass().isAnnotationPresent(Permissions.class)) {
            if (ctx.guild.getSelfMember().hasPermission(Objects.requireNonNull(ctx.guild.getGuildChannelById(ctx.channel.getId())), cmd.getPermissions()) && ctx.member.hasPermission(Objects.requireNonNull(ctx.guild.getGuildChannelById(ctx.channel.getId())), cmd.getPermissions())) {
                cmd.onCall(ctx);
            } else {
                StringBuilder sb = new StringBuilder();
                cmd.getPermissions().forEach(perm -> sb.append("- ").append(perm.getName()).append("\n"));
                ctx.channel.sendMessage("This bot and the message author both need the following permissions to run this command: \n```\n" + sb + "\n```").queue();
            }
        } else {
            cmd.onCall(ctx);
        }
    }

    public static void registerCommand(ICommand command) {
        commandRegistry.put(command.getName().toLowerCase(), command);
        command.getAliases().forEach(alias -> commandRegistry.put(alias.toLowerCase(), command));
    }

    public static void registerModule(Module module) {
        moduleRegistry.put(module.getName().toLowerCase(), module);
        module.getCommands().forEach(command -> {
            commandRegistry.put(command.getName().toLowerCase(), command);
            command.getAliases().forEach(alias -> commandRegistry.put(alias, command));
        });
    }

    public static ICommand getCommand(String name) {
        return commandRegistry.get(name);
    }

    public static HashMap<String, ICommand> getCommandRegistry() {
        return commandRegistry;
    }

    public static HashMap<String, Module> getModuleRegistry() {
        return moduleRegistry;
    }
}
