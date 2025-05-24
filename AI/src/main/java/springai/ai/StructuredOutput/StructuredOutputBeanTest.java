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
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import springai.ai.config.AiLogConfig;

import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class StructuredOutputBeanTest {
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

    public String StructuredOutputTest(HashMap Map) throws MalformedURLException {

        // 创建一个 StructuredOutputConverter 对象
        // 传入实体类，简单实用
        StructuredOutputConverter structuredOutputConverter = new BeanOutputConverter(User.class);
//        // 传入实体类，定制 JSON 处理：需调整日期格式、字段命名策略（如驼峰转下划线）、忽略未知字段等
//        StructuredOutputConverter structuredOutputConverter2 = new BeanOutputConverter(User.class, new ObjectMapper());
//        // 处理泛型集合或嵌套结构：当目标类型包含泛型参数时（如 List<Actor>、Map<String, Actor>），必须通过此构造器保留泛型信息。
//        StructuredOutputConverter structuredOutputConverter3 = new BeanOutputConverter(
//                new ParameterizedTypeReference<List<User>>(){}
//        );
//        // 泛型 + 定制 JSON 处理：需要同时处理泛型类型和自定义 JSON 规则时使用。
//        StructuredOutputConverter structuredOutputConverter4 = new BeanOutputConverter(
//                new ParameterizedTypeReference<HashMap<String, User>>(){}
//        );

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
