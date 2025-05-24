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
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import springai.ai.config.AiLogConfig;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class StructuredOutputListTest {
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

    public String StructuredOutputListTest(HashMap map) throws MalformedURLException {

        // 创建一个 StructuredOutputConverter 对象
        // 传入实体类，简单实用
        ListOutputConverter structuredOutputConverter = new ListOutputConverter(new DefaultConversionService());

        // 获取格式化字符串
        String format = structuredOutputConverter.getFormat();

        // 模板
        PromptTemplate promptTemplate = new PromptTemplate("Tell me a {format} joke about topic",
                (java.util.Map<String, Object>) new HashMap<>().put("format", format));
        // 创建一个 Prompt 对象
        prompt = promptTemplate.create();

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory);
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
        List<String> convert = structuredOutputConverter.convert(generatedText);

        // 获取 ChatGenerationMetadata 对象
        ChatGenerationMetadata metadata = result.getMetadata();
        System.out.println("元数据: " + metadata);

        return call.content();
    }
}
