package springai.ai.RAG;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.DefaultChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import springai.ai.config.AiLogConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings("all")
public class RAGTest {
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


    public String sendMessage(@RequestBody Map Map){
        DefaultChatOptions defaultChatOptions = new DefaultChatOptions();
        defaultChatOptions.setTemperature(0.7);

        class compressionQueryTransformer {
            // 查询转化的子类，压缩转换
            // 创建 Query 对象，用于模拟用户查询和历史对话
            Query query1 = Query.builder()
                    .text("And what is its second largest city?")
                    .history(new UserMessage("What is the capital of Denmark?"),
                            new AssistantMessage("Copenhagen is the capital of Denmark."))
                    .build();

            QueryTransformer compressionQueryTransformer = CompressionQueryTransformer.builder()
                    .chatClientBuilder(ChatClient.builder(chatModel))
                    .build();
        }

        class rewriteQueryTransformer{
            // 模拟重写查询的请求
            Query query2 = new Query("I'm studying machine learning. What is an LLM?");

            // 创建 QueryTransformer 对象，用于模拟重写查询
            QueryTransformer rewriteQueryTransformer = RewriteQueryTransformer.builder()
                    .chatClientBuilder(ChatClient.builder(chatModel))
                    .build();
        }

        class queryTransformer {
            // 模拟翻译查询的请求
            Query query = new Query("Hvad er Danmarks hovedstad?");

            // 创建 QueryTransformer 对象，用于模拟翻译查询
            QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
                    .chatClientBuilder(ChatClient.builder(chatModel))
                    .promptTemplate() // 提示词模板
                    .targetLanguage("english") // 目标语言
                    .build();
        }

        class queryExpander {
            MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                    .chatClientBuilder(ChatClient.builder(chatModel))
                    .promptTemplate() // 提示词模板
                    .numberOfQueries(3)
                    .build();
            List<Query> queries = queryExpander.expand(new Query("How to run a Spring Boot app?"));
        }

        class contextualQueryAugmenter{
            ContextualQueryAugmenter build = ContextualQueryAugmenter
                    .builder()
                    .allowEmptyContext(true) // 可以为空
                    .emptyContextPromptTemplate(new PromptTemplate()) // 传入提示词模板，用来上下文空的时候使用
                    .build();
            Query augment = build.augment(new Query(""), new ArrayList<Document>());
        }

        class documentRetriever{
            // 创建 DocumentRetriever 对象，用于检索相关文档
            DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                    .vectorStore(vectorStore) // 相关的矢量数据库
                    .similarityThreshold(0.73) // 相似度阈值
                    .topK(5) // 相似数量大小
                    .filterExpression(new FilterExpressionBuilder() // 过滤器（过滤器相关和之前见过的一样）
                            .eq("genre", "fairytale")
                            .build())
                    .build();
            // 创建 DocumentJoiner 对象，用于将检索到的文档组合成一个完整的文档
            DocumentJoiner documentJoiner = new ConcatenationDocumentJoiner();
        }

        // 复杂 RAG 拦截器
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(
                        new compressionQueryTransformer().compressionQueryTransformer,
                        new rewriteQueryTransformer().rewriteQueryTransformer,
                        new queryTransformer().queryTransformer
                )
                .documentRetriever(new documentRetriever().retriever)
                .documentJoiner(new documentRetriever().documentJoiner)
                .queryAugmenter(new contextualQueryAugmenter().build)
                .queryExpander(new queryExpander().queryExpander)
                .build();

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory);
        ChatClient.CallResponseSpec call = chatClient
                .prompt()
                .options(defaultChatOptions)
                .user(Map.get("message").toString())
                .system(sp -> sp.param("messages", Map.get("message").toString())) // 动态添加系统提示
                .advisors(
                        messageChatMemoryAdvisor,    // 添加记忆 Memory Advisor
                        aiLogConfig.simpleLoggerAdvisor(
                                Map.get("message").toString(), messageChatMemoryAdvisor.toString()),
                        // 这里接收的是上一个拦截器返回的数据
                        retrievalAugmentationAdvisor
                ).call();
        ChatResponse chatResponse = call.chatResponse();
        Generation result = chatResponse.getResult();
        // 获取 AssistantMessage 对象
        AssistantMessage assistantMessage = result.getOutput();

        // 获取生成的文本
        String generatedText = assistantMessage.getText();
        System.out.println("生成的文本: " + generatedText);

        // 获取 ChatGenerationMetadata 对象
        ChatGenerationMetadata metadata = result.getMetadata();
        System.out.println("元数据: " + metadata);

        return call.content();
    }
}
