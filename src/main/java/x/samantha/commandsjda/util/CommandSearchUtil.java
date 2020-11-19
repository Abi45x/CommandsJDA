package x.samantha.commandsjda.util;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import x.samantha.commandsjda.managers.CommandManager;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class CommandSearchUtil {
    public static StandardAnalyzer analyzer;
    public static RAMDirectory index;

    CommandSearchUtil() {
        analyzer = new StandardAnalyzer();
        index = new RAMDirectory();
        IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
        IndexWriter writer;
        try {
            writer = new IndexWriter(index, cfg);
            CommandManager.getCommandRegistry().keySet().forEach(key -> {
                Document doc = new Document();
                doc.add(new StringField("command", key, Field.Store.YES));
                try {
                    writer.addDocument(doc);
                } catch (IOException ignored) {}
            });
            writer.close();
        } catch (IOException ignored) {}
    }
}
