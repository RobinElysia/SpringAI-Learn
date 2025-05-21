package springai.ai.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
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
public class MyHTMLReader {
    @Autowired
    private VectorStore vectorStore;
    public List<Document> readHTML(Resource resource) {
        // 将文本分割为多个片段
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

        // HTML
        // 配置读取
        JsoupDocumentReaderConfig jsoupDocumentReaderConfig = JsoupDocumentReaderConfig.builder()
                .selector("article p") // 提取 <article> 标签内的段落
                .charset("ISO-8859-1")  // 使用 ISO-8859-1 编码
                .includeLinkUrls(true) // 在元数据中包含链接 URL
                .metadataTags(List.of("author", "date")) // 提取作者和日期元标记
                .additionalMetadata("source", "my-page.html") // 添加自定义元数据
                .build();
        // 创建读取
        JsoupDocumentReader jsoupDocumentReader = new JsoupDocumentReader(resource,  jsoupDocumentReaderConfig);
        // 读取文档
        List<Document> HTMLdocuments = jsoupDocumentReader.get();
        // 分割写入
        vectorStore.write(tokenTextSplitter.split(HTMLdocuments));
        return HTMLdocuments;
    }
}
