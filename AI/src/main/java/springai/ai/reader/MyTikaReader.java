package springai.ai.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author RobinElysia
 * @version 1.0
 */
@Component
@SuppressWarnings({"all"})
public class MyTikaReader {
    @Autowired
    private VectorStore vectorStore;
    public List<Document> readTaki(Resource resource) {
        // 将文本分割为多个片段
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        // Tika
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        // 获取文档
        List<Document> tikaDocuments = tikaDocumentReader.get();
        // 将文档添加到向量存储中
        vectorStore.write(tokenTextSplitter.split(tikaDocuments));
        return tikaDocuments;
    }
}
