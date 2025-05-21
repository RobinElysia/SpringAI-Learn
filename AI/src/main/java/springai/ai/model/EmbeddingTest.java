package springai.ai.model;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.*;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author RobinElysia
 * @version 1.0
 */
@RestController
@RequestMapping("api")
@SuppressWarnings({"all"})
public class EmbeddingTest{
    @Autowired
    private EmbeddingModel embeddingModel;

    @PostMapping(value = "EmbeddingTest", produces = "text/event-stream;charset=UTF-8")
    public String sendMessage(@RequestBody Map Map) {
        /// 直接嵌入，没有返回响应：
        // 单个字符串嵌入
        float[] embedding0 = embeddingModel.embed("I like Spring Boot");

        // 文档嵌入
        Document document = new Document("I like Spring Boot");
        float[] embedding1 = embeddingModel.embed(document);

        // 批量文本嵌入
        List<String> texts0 = List.of("I like Spring Boot", "I love Java programming language");
        List<float[]> embeddings0 = embeddingModel.embed(texts0);

        // 批量文档嵌入
        List<Document> documents = List.of(new Document("I like Spring Boot"), new Document("I love Java programming language"));
        EmbeddingOptions options = EmbeddingOptionsBuilder.builder().build();
        BatchingStrategy batchingStrategy = new BatchingStrategy() {
            @Override
            public List<List<Document>> batch(List<Document> documents) {
                int batchSize = 10; // 每个批次的大小
                List<List<Document>> batches = new ArrayList<>();
                for (int i = 0; i < documents.size(); i += batchSize) {
                    int end = Math.min(i + batchSize, documents.size());
                    batches.add(documents.subList(i, end));
                }
                return batches;
            }
        };
        List<float[]> embeddings1 = embeddingModel.embed(documents, options, batchingStrategy);

        /// 获取响应嵌入
        // 获取嵌入响应
        List<String> texts1 = List.of("I like Spring Boot", "I love Java programming language");
        EmbeddingResponse response = embeddingModel.embedForResponse(texts1);
        List<float[]> embeddings = response.getResults().stream().map(Embedding::getOutput).toList();

        // 获取嵌入维度
        int dimensions = embeddingModel.dimensions();

        // 使用嵌入选项
        EmbeddingRequest request = new EmbeddingRequest(List.of("I like Spring Boot"),
                EmbeddingOptionsBuilder.builder().withModel("text-davinci-003").build());
        EmbeddingResponse response1 = embeddingModel.call(request);
        List<float[]> embeddings2 = response.getResults().stream().map(Embedding::getOutput).toList();

        // 嵌入数据库操作
        VectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        List<Document> documents0 = List.of(new Document("I like Spring Boot"), new Document("I love Java programming language"));
        vectorStore.add(documents0);

        // 检索嵌入
        SearchRequest query = SearchRequest.builder().query("Spring Boot").topK(2).build();
        List<Document> similarDocuments = vectorStore.similaritySearch(query);

        return "";
    }
}
