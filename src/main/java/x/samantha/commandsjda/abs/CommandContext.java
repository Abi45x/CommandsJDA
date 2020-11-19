package x.samantha.commandsjda.abs;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x.samantha.commandsjda.managers.CommandManager;
import x.samantha.commandsjda.util.CommandSearchUtil;

import java.io.IOException;
import java.util.Arrays;

public class CommandContext {

    private static final Logger log = LoggerFactory.getLogger(CommandContext.class);

    public Guild guild;
    public TextChannel channel;
    public Member member;
    public User author;
    public Message message;
    public GuildMessageReceivedEvent rawEvent;

    public String trigger;
    public String[] args;
    public String rawArgs;
    public ICommand command;

    private CommandContext(Guild guild, TextChannel channel, Member member, User author, Message message, GuildMessageReceivedEvent rawEvent) {
        this.guild = guild;
        this.channel = channel;
        this.member = member;
        this.author = author;
        this.message = message;
        this.rawEvent = rawEvent;
    }

    /**
     * Create a CommandContext object
     * @return CommandContext
     */
    public static CommandContext parse(GuildMessageReceivedEvent e, String prefix, Boolean enableUnknownCommandMessage) {
        String input;
        String[] tempArgs;
        String msg = e.getMessage().getContentRaw();

        // Check if the message starts with the set prefix, return null if it doesn't
        if (msg.startsWith(prefix)) {
            input = msg.substring(prefix.length()).trim();
        } else {
            return null;
        }

        // If there is nothing after the prefix, return null, else continue
        if (input.isEmpty()) return null;
        tempArgs = input.split("\\s+");
        if (tempArgs.length == 0) return null;

        // Try to get the specified command from the command registry
        ICommand cmd = CommandManager.getCommand(tempArgs[0].toLowerCase());

        // If the command exists create a new CommandContext Object and return it
        if (cmd != null) {
            CommandContext ctx = new CommandContext(
                    e.getGuild(),
                    e.getChannel(),
                    e.getMember(),
                    e.getAuthor(),
                    e.getMessage(),
                    e
            );
            ctx.trigger = tempArgs[0];
            ctx.command = cmd;
            ctx.args = Arrays.copyOfRange(tempArgs, 1, tempArgs.length);
            ctx.rawArgs = input.replaceFirst(tempArgs[0], "");
            return ctx;
        } else {
            // If the command doesn't exist and enableUnknownCmdMsg is true then suggest similar commands
            if (enableUnknownCommandMessage) {
                try {
                    Query q = new QueryParser("command", CommandSearchUtil.analyzer).parse(tempArgs[0].toLowerCase());
                    int hitsPerPage = 5;
                    DirectoryReader reader = DirectoryReader.open(CommandSearchUtil.index);
                    IndexSearcher searcher = new IndexSearcher(reader);
                    TopDocs docs = searcher.search(q, hitsPerPage);
                    ScoreDoc[] hits = docs.scoreDocs;
                    MessageBuilder msgBuilder = new MessageBuilder("The Command `" + tempArgs[0].toLowerCase() + "` was not found, did you mean any of the following?\n```");
                    Arrays.stream(hits).forEach(hit -> {
                        try {
                            msgBuilder.append(searcher.doc(hit.doc).get("command").toLowerCase());
                        } catch (IOException ex2) {
                            log.warn("Unable to add command to suggestion list: " + ex2.getMessage());
                        }
                    });
                    reader.close();
                    msgBuilder.append("\n```");
                    e.getChannel().sendMessage(msgBuilder.build()).queue();
                } catch (ParseException | IOException ex) {
                    e.getChannel().sendMessage("Unknown Command").queue();
                    log.warn("Unknown Command '" + tempArgs[0].toLowerCase() + "' and unable to find similar commands: " + ex.getMessage());
                }
            }
            // Return null for commands that don't exist
            return null;
        }
    }

    Boolean hasArguments() {
        return args.length != 0 && !rawArgs.isEmpty();
    }
}
