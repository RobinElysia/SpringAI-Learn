package springai.ai.model;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springai.ai.config.AiLogConfig;

import java.util.Map;

/**
 * @author RobinElysia
 * @version 1.0
 * @描述 聊天AI
 * @date 2023/7/26  15:04
 */
@RestController
@RequestMapping("api")
@SuppressWarnings({"all"})
public class ChatTest{
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

    @PostMapping(value = "chat", produces = "text/event-stream;charset=UTF-8")
    public String sendMessage(@RequestBody Map Map) {

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory);
        ChatClient.CallResponseSpec call = chatClient
                .prompt()
                .user(Map.get("message").toString())
                .system(sp -> sp.param("messages", Map.get("message").toString())) // 动态添加系统提示
                .advisors(
                        messageChatMemoryAdvisor,    // 添加记忆 Memory Advisor
                        aiLogConfig.simpleLoggerAdvisor(Map.get("message").toString(), messageChatMemoryAdvisor.toString())
                        // 这里接收的是上一个拦截器返回的数据
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
