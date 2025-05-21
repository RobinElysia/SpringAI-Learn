package springai.ai.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
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
public class MyPDFReader {
    @Autowired
    private VectorStore vectorStore;
    public List<Document> readPDF(Resource resource) {
        // 将文本分割为多个片段
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

        // PDF页面
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, // 本地路径
                PdfDocumentReaderConfig.builder() // 创建PDF文档读取器配置
                        .withPageTopMargin(0) // 设置页边距
                        // 设置页面提取文本格式化器
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder() // 设置提取文本格式化器
                                .withNumberOfTopTextLinesToDelete(0) // 删除顶部文本行数
                                .build())
                        .withPagesPerDocument(1) // 设置每页文档数
                        .build());
        // 文档读取
        List<Document> pdfdocuments = pdfReader.get();
        // 将PDF文档添加到向量存储中
        vectorStore.write(tokenTextSplitter.split(pdfdocuments));
        return pdfdocuments;
    }
}
