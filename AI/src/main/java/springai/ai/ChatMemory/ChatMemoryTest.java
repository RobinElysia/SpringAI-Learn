package springai.ai.ChatMemory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import springai.ai.config.AiLogConfig;

import java.util.HashMap;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings("all")
public class ChatMemoryTest {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatOptions chatOptions;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private AiLogConfig aiLogConfig;
//    @Autowired
//    private JdbcChatMemoryRepository chatMemoryRepository;
    @Autowired
    private InMemoryChatMemoryRepository inMemoryChatMemoryRepository;

    public String ChatMemoryTest(HashMap map) {
        JdbcChatMemoryRepository chatMemoryRepository = JdbcChatMemoryRepository.builder()
                .jdbcTemplate(yourJdbcTemplate) // 提供已有的 JdbcTemplate
                .dataSource(yourDataSource)       // 或者直接提供数据源
                .dialect(Dialect.MYSQL)           // 显式指定数据库类型
                .transactionManager(yourTxManager)// 事务管理器（可选）
                .build();                         // 构建最终的 Repository
        MessageWindowChatMemory builder = MessageWindowChatMemory.builder()
                .maxMessages(20) // 最大记忆条数，正式RC版本为默认20
                .chatMemoryRepository(chatMemoryRepository) // 传入聊天记忆库
                .build();


        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        ChatClient.CallResponseSpec call = chatClient
                .prompt()
                .user(u -> u.text("Explain what do you see on this picture?")
                        .media(MimeTypeUtils.IMAGE_PNG, new ClassPathResource("/multimodal.test.png")))
                .system(sp -> sp.param("messages", map.get("message").toString())) // 动态添加系统提示
                .call();
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
