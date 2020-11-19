package x.samantha.commandsjda.handlers;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x.samantha.commandsjda.abs.CommandContext;
import x.samantha.commandsjda.abs.ICommand;
import x.samantha.commandsjda.abs.Module;
import x.samantha.commandsjda.managers.CommandManager;

import java.text.MessageFormat;
import java.util.List;

public class CommandHandler extends ListenerAdapter {

    private final Logger log = LoggerFactory.getLogger(CommandHandler.class);

    Boolean canBeSelf;
    Boolean canBeBot;
    String prefix;
    Boolean enableUnknownCommandMessage;
    List<ICommand> commands;
    List<Module> modules;

    CommandHandler(Boolean canBeSelf, Boolean canBeBot, String prefix, Boolean enableUnknownCommandMessage) {
        this.canBeSelf = canBeSelf;
        this.canBeBot = canBeBot;
        this.prefix = prefix;
        this.enableUnknownCommandMessage = enableUnknownCommandMessage;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        log.info(MessageFormat.format("[{0}] {1}:\t\"{2}\"", event.getGuild().getName(), event.getAuthor().getName(), event.getMessage().getContentRaw()));

        if (!canBeSelf && event.getAuthor() == event.getJDA().getSelfUser()) return;

        if (!canBeBot && event.getAuthor().isBot()) return;

        CommandContext ctx = CommandContext.parse(event, prefix, enableUnknownCommandMessage);

        if (ctx == null) return;

        CommandManager.onCall(ctx);
    }

    public CommandHandler canBeSelfUser(Boolean value) {
        this.canBeSelf = value;
        return this;
    }

    public CommandHandler canBeBot(Boolean value) {
        this.canBeBot = value;
        return this;
    }

    public CommandHandler enableUnknownCommandMessage(Boolean value) {
        this.enableUnknownCommandMessage = value;
        return this;
    }

    public CommandHandler prefix(String value) {
        this.prefix = value;
        return this;
    }

    public CommandHandler commands(List<ICommand> value) {
        this.commands = value;
        return this;
    }

    public CommandHandler modules(List<Module> value) {
        this.modules = value;
        return this;
    }

    public CommandHandler registerModules() {
        this.modules.forEach(CommandManager::registerModule);
        return this;
    }

    public CommandHandler registerCommands() {
        this.commands.forEach(CommandManager::registerCommand);
        return this;
    }
}
