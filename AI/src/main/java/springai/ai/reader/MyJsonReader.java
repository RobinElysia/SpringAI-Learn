package springai.ai.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author RobinElysia
 * @version 1.0
 */
@Component
@SuppressWarnings({"all"})
public class MyJsonReader {
    @Autowired
    private VectorStore vectorStore;

    public List<Document> readJson(Resource resource) {
        // 将文本分割为多个片段
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

        // json读取JSON文件
        JsonReader jsonReader = new JsonReader(resource,"name");
        // JSON指针
        List<Document> documents = jsonReader.get("/data/0/id");
        // 添加到向量存储中
        vectorStore.write(tokenTextSplitter.split(jsonReader.get()));
        return documents;
    }
}
