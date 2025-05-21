package springai.ai.VectorSQL;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.*;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.*;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import springai.ai.config.AiLogConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings("all")
public class MongoDBTest {
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatModel chatModel;
    @Autowired
    private ChatOptions chatOptions;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private Prompt prompt;
    @Autowired
    private AiLogConfig aiLogConfig;

    public String test(@RequestBody List<String> ListText) {
        // 先实现二层接口 EmbeddingModel 和 模型选项
        EmbeddingOptions embeddingOptions = new EmbeddingOptions() {
            @Override
            /// 模型名称
            public String getModel() {
                return "模型名称";
            }

            @Override
            /// 嵌入维度
            public Integer getDimensions() {
                return 1536; // 嵌入维度，越大越nb，但是性能也会...需要平衡精度与计算资源
            }
        };
        EmbeddingModel embeddingModel = new EmbeddingModel() {
            @Override
            /// 重写调用模型
            public EmbeddingResponse call(EmbeddingRequest request) {
                final List<String> instructions = request.getInstructions();
                return embedForResponse(instructions);
            }

            @Override
            /// 重写嵌入方法
            public float[] embed(Document document) {
                ArrayList<String> objects = new ArrayList<>();
                objects.add(document.getText());
                EmbeddingRequest embeddingRequest = new EmbeddingRequest(objects, embeddingOptions);
                EmbeddingResponse call = call(embeddingRequest);
                float[] output = call.getResult().getOutput();
                return output;
            }
        };

        List<Document> documents = new ArrayList();
        ListText.forEach(text -> {
            documents.add(new Document(text));
        });
        vectorStore.add(documents);
        // 查询文档
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        List<Document> SearchDocuments = vectorStore.similaritySearch(new SearchRequest()
                .builder()
                .query("查询")
                .topK(10)
                .similarityThreshold(0.75)
                .filterExpression(b.and(
                        b.eq("name","qwp"),
                        b.eq("age",18)
                ).build())
                .build());

        // 数据装载
        ArrayList<String> strings = new ArrayList<>();
        SearchDocuments.stream().forEach(document -> {
            strings.add(document.getText());
        });

        // 方法一（常用）：RAG走聊天模型
        // 记忆拦截器
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory);

        // 复杂RAG 拦截器
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(ChatClient.builder(chatModel))
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .filterExpression(b.eq("name","qwp").build())
                        .vectorStore(vectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();

        // 拿到文档向量走聊天模型
        String content = chatClient
                .prompt()
                .user(strings.toString())
                .system(sp -> sp.param("messages", strings.toString())) // 动态添加系统提示
                .advisors(
                        a -> a.param("name","qwp")
                ).call().content();

        // 方法二：走嵌入模型
        EmbeddingRequest embeddingRequest = new EmbeddingRequest(strings,embeddingOptions);
        EmbeddingResponse call = embeddingModel.call(embeddingRequest);
        Embedding result = call.getResult();
        EmbeddingResponseMetadata metadata = call.getMetadata();
        List<Embedding> results = call.getResults();

        // 方法三：你可以直接将搜索出来的文档转化为向量
        int batchSize = 10;     // 文档分批处理大小
        List<float[]> embed = embeddingModel.embed(SearchDocuments, embeddingOptions, new BatchingStrategy() {
            @Override
            public List<List<Document>> batch(List<Document> documents) {
                List<List<Document>> batches = new ArrayList<>();
                if (documents.size() > batchSize) {
                    for (int i = 0; i < documents.size(); i += batchSize) {
                        int end = Math.min(i + batchSize, documents.size());
                        batches.add(documents.subList(i, end));
                    }
                    return batches;
                } else if (documents.size() > 0 && documents.size() <= batchSize) {
                    batches.add(documents);
                    return batches;
                }
                return null;
            }
        });
        return content;
    }
}
