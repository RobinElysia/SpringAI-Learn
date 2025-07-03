package springai.ai.toolCall;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import springai.ai.config.AiLogConfig;
import springai.ai.toolCall.tools.DateTimeTools;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class toolCallTest {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private AiLogConfig aiLogConfig;

    public String StructuredOutputListTest(HashMap map) throws MalformedURLException {

        // 创建一个 DateTimeTools 对象
//        ToolCallback[] dateTimeTools = ToolCallbacks.from(new DateTimeTools());
//        ToolCallback[] dateTimeTools1 = ToolCallbacks.from(new ArrayList<>().add(new DateTimeTools()));

        // 以下方式创建一个 DateTimeTool 对象

        // 方法的方式创建一个 ToolCallback 对象

        Method method = ReflectionUtils.findMethod(DateTimeTools.class, "getCurrentDateTime");
        ToolCallback toolCallback = MethodToolCallback.builder()
                .toolDefinition(ToolDefinition.builder()
                        .description("Get the current date and time in the user's timezone")
                        .name("name")
                        .inputSchema("inputSchema")
                        .build())
                .toolMethod(method)
                .toolMetadata(ToolMetadata.builder().build()) // 传递默认的工具元数据
                .toolObject(new DateTimeTools())
                .build();

//        // 函数的方式创建一个 ToolCallback 对象
//        ToolCallback toolCallback = FunctionToolCallback
//                .builder("currentWeather", new WeatherService()) // 需要传入 函数name 和 函数对象
//                .description("Get the weather in location") // 描述
//                .inputType(WeatherRequest.class) // 输入参数
//                .toolMetadata(ToolMetadata.builder().build()) // 和方法创建的工具元数据操作一样
//                .toolCallResultConverter(new DefaultToolCallResultConverter()) // 这个需要讲
//                .inputSchema("工具输入格式") // 输入格式
//                .build();

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();
        ChatClient.CallResponseSpec call = chatClient
                .prompt()
                .user(map.get("message").toString())
                .system(sp ->
                        sp.param("messages", map.get("message").toString())) // 动态添加系统提示
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
