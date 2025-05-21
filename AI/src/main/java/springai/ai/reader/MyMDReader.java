package springai.ai.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
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
public class MyMDReader {
    @Autowired
    private VectorStore vectorStore;
    public List<Document> readMD(Resource resource) {
        // 将文本分割为多个片段
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

        // Markdown
        // 创建配置
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true) // 使用水平规则创建文档
                .withIncludeCodeBlock(false) // 使用包含代码块
                .withIncludeBlockquote(false) // 使用包含块引用
                .withAdditionalMetadata("filename", "code.md") // 使用附加元数据
                .build();
        // 创建读取器
        MarkdownDocumentReader MDdocuments = new MarkdownDocumentReader(resource, config);
        // 读取
        List<Document> markdownDocuments = MDdocuments.get();
        vectorStore.write(tokenTextSplitter.split(markdownDocuments));
        return markdownDocuments;
    }
}
