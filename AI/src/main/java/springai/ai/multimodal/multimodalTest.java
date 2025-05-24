package springai.ai.multimodal;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import springai.ai.config.AiLogConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings("all")
public class multimodalTest {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatOptions chatOptions;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private AiLogConfig aiLogConfig;

    public String multimodalTest(HashMap map) {
        var imageResource = new ClassPathResource("/multimodal.test.png");

        // 创建资源
        List<Media> objects = new ArrayList<>();
        objects.add(new Media(MimeTypeUtils.IMAGE_PNG, imageResource));

        var userMessage = new UserMessage(
                "Explain what do you see in this picture?", // content
                new Media(MimeTypeUtils.IMAGE_PNG, imageResource)); // media

        Prompt prompt = new Prompt(userMessage);

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory);
        ChatClient.CallResponseSpec call = chatClient
                .prompt(prompt) // 添加用户消息
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
