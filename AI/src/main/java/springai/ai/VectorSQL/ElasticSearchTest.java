package springai.ai.VectorSQL;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.*;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import springai.ai.config.AiLogConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings("all")
public class ElasticSearchTest {
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatOptions chatOptions;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private Prompt prompt;
    @Autowired
    private AiLogConfig aiLogConfig;

    public String test(@RequestBody List<Document> instructions) {// 接收文档样例
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

        // 添加到向量库
        vectorStore.add(instructions);
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        // 检索与查询类似的文档
        List<Document> Result = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("查询内容")
                        .topK(10) // 设置返回的相似度最高的文档数量
                        .similarityThreshold(0.75) // 设置相似度阈值，范围在 0 到 1 之间，值越接近 1 表示要求的相似度越高。例如，设置为 0.75 表示只返回相似度高于 0.75 的文档。
                        .filterExpression(
                                b.and(
                                        b.in("city","shenyang"), b.eq("shenyang","shenbei")
                                ).build()) // 设置查询条件，例如，只返回文档中包含 "Java" 的文档
                        .build());
        List<String> testList = new ArrayList<>(); // 获取相似的文档列表

        Result.forEach(R->{
            String text = R.getText(); // 获取相似的文档内容
            // 拿到文档内容后走聊天AI模型
            testList.add(text);
        });

        // 用法一：你也可以直接 拿到数据库中的文档，转化为向量数据，返回文档的向量，但是一般不这么写
        int batchSize = 10;// 文档分批处理大小
        List<float[]> embed = embeddingModel.embed(Result, embeddingOptions, new BatchingStrategy() {
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

        // 用法二：用拿到的testList拿到响应的元数据数据，也是向量数据
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(testList);
        EmbeddingResponseMetadata metadata = embeddingResponse.getMetadata();
        List<Embedding> results = embeddingResponse.getResults();
        Embedding result = embeddingResponse.getResult();

        // 正儿八经用法 RAG+聊天模型，通过拿到的文档列表，去与聊天模型交互
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory);
        ChatClient.CallResponseSpec call = chatClient
                .prompt()
                .user(testList.toString())
                .advisors(messageChatMemoryAdvisor) // 你甚至可以把检索向量数据库写成拦截器，直接写在这边！
                .call();
        ChatResponse chatResponse = call.chatResponse();
        Generation r = chatResponse.getResult();
        // 获取 AssistantMessage 对象
        AssistantMessage assistantMessage = r.getOutput();
        // 获取生成的文本
        String generatedText = assistantMessage.getText();
        System.out.println("生成的文本: " + generatedText);
        // 获取 ChatGenerationMetadata 对象
        ChatGenerationMetadata md = r.getMetadata();
        System.out.println("元数据: " + md);
        // 返回增强结果
        return call.content();
    }
}
