package springai.ai.StructuredOutput;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import springai.ai.config.AiLogConfig;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class StructuredOutputMapTest {
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

    public String StructuredOutputTest(HashMap map) throws MalformedURLException {

        // 创建一个 StructuredOutputConverter 对象
        // 传入实体类，简单实用
        MapOutputConverter structuredOutputConverter = new MapOutputConverter();

        // 获取格式化字符串
        String format = structuredOutputConverter.getFormat();

        // 模板
        PromptTemplate promptTemplate = PromptTemplate
                .builder()
                .template("Tell me a {format} joke about topic")
                .variables((Map<String, Object>) new HashMap<>().put("format", format))
                .build();
        // 创建一个 Prompt 对象
        prompt = promptTemplate.create();

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();
        ChatClient.CallResponseSpec call = chatClient
                .prompt(prompt)
                .user(map.get("message").toString())
                .system(sp -> sp.param("messages", map.get("message").toString())) // 动态添加系统提示
                .call();
        ChatResponse chatResponse = call.chatResponse();
        Generation result = chatResponse.getResult();

        // 获取 AssistantMessage 对象
        AssistantMessage assistantMessage = result.getOutput();

        // 获取生成的文本
        String generatedText = assistantMessage.getText();
        System.out.println("生成的文本: " + generatedText);

        // 调用 StructuredOutputConverter
        Map<String,  Object> convert = structuredOutputConverter.convert(generatedText);

        // 获取 ChatGenerationMetadata 对象
        ChatGenerationMetadata metadata = result.getMetadata();
        System.out.println("元数据: " + metadata);

        return call.content();
    }
}
