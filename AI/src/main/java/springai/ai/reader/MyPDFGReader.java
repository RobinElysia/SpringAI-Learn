package springai.ai.reader;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
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
public class MyPDFGReader {
    @Autowired
    private VectorStore vectorStore;
    public List<Document> readPDFG(Resource resource) {
        // 将文本分割为多个片段
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        // PDF段落
        ParagraphPdfDocumentReader pdfGraphReader = new ParagraphPdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());
        // 文档读取
        List<Document> pdfGraphdocuments = pdfGraphReader.get();
        // 将PDF文档添加到向量存储中
        vectorStore.write(tokenTextSplitter.split(pdfGraphdocuments));
        return pdfGraphdocuments;
    }
}
