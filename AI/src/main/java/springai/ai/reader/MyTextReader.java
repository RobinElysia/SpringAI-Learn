package springai.ai.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author RobinElysia
 * @version 1.0
 */
@Component
@SuppressWarnings({"all"})
public class MyTextReader {
    @Autowired
    private VectorStore vectorStore;

    public List<Document> readText(Resource resource) {
        // 将文本分割为多个片段
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

        // text读取文本文件
        TextReader textReader = new TextReader(resource);
        textReader.setCharset(StandardCharsets.UTF_16);
        List<Document> documents = textReader.get();
        // 添加到向量存储中
        vectorStore.accept(tokenTextSplitter.apply(documents));
        return documents;
    }
}
