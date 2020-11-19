package x.samantha.commandsjda.util

import x.samantha.commandsjda.managers.CommandManager
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.RAMDirectory

class CommandSearchUtil {

    companion object {
        lateinit var analyzer: StandardAnalyzer
        lateinit var index: RAMDirectory
    }

    init {
        analyzer = StandardAnalyzer()
        index = RAMDirectory()
        val config = IndexWriterConfig(analyzer)
        val writer = IndexWriter(index, config)

        CommandManager.commandRegistry.keys.forEach {
            val doc = Document()
            doc.add(StringField("command", it, Field.Store.YES))
            writer.addDocument(doc)
        }

        writer.close()
    }
}