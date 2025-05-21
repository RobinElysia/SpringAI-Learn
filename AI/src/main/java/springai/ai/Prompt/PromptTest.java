package springai.ai.Prompt;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.NoOpTemplateRenderer;
import org.springframework.ai.template.ValidationMode;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.RequestBody;
import springai.ai.config.AiLogConfig;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class PromptTest {
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
    @Autowired
    private ChatModel chatModel;

    public String promptTest(@RequestBody Map Map) throws MalformedURLException {

        // 创建 TemplateRenderer 实现类
        StTemplateRenderer templateRenderer = StTemplateRenderer.builder()
                .startDelimiterToken('{')
                .endDelimiterToken('}')
                .supportStFunctions() // 该方法用于启用对 StringTemplate 内置函数的支持。
                // 开启后，在验证模板时，遇到如 "first"、"rest"、"length" 等内置函数名时
                // 不会将其视为必须提供的变量
                // 默认是关闭状态，此时这些名称会被当作普通变量处理，并在启用验证时要求提供。
                .validationMode(ValidationMode.NONE) // 动态变量不匹配时的处理方式，可以是抛出异常，警告和不处理，默认抛出异常
                .build();
        // 或者创建 NoOpTemplateRenderer
        NoOpTemplateRenderer noOpTemplateRenderer = new NoOpTemplateRenderer();

        PromptTemplate promptTemplate = PromptTemplate.builder() // M8以后的版本，这里需要删除 M6版本的core依赖
                .variables((Map<String, Object>) new HashMap<String, Object>().put("占位符", "动态变量")) // 动态替换
//                .template("Tell me a {adjective} joke about {topic}") // 模板字符串
                .renderer(noOpTemplateRenderer) // 使用模板渲染器
//                .renderer(templateRenderer) // 或者使用另一个实现类
                .resource(new UrlResource("classpath:/static/Resource.txt")) // 跟template二选一
                .build();

        // 创建 Prompt 对象
        Prompt prompt = promptTemplate.create();

        // 对话聊天
        // 创建 Memory Advisor
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(chatMemory);
        // 创建 ChatClient
        ChatClient.CallResponseSpec call = chatClient
                .prompt(prompt) // 添加 Prompt
                .user(Map.get("message").toString())
                .system(sp -> sp.param("messages", Map.get("message").toString())) // 动态添加系统提示
                .advisors(
                        messageChatMemoryAdvisor,    // 添加记忆 Memory Advisor
                        aiLogConfig.simpleLoggerAdvisor(
                                Map.get("message").toString(), messageChatMemoryAdvisor.toString())
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
