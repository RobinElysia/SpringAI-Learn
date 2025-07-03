package springai.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.*;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.DefaultChatOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RobinElysia
 * @version 1.0
 * @描述 AI聊天的系统配置
 * @date 2023/10/15 16:02
 */
@Configuration
@SuppressWarnings({"all"})
public class SpringAIConfig {
    @Autowired
    private AiLogConfig aiLogConfig;
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private ChatMemory chatMemory;

    DefaultChatOptions defaultChatOptions = new DefaultChatOptions();   // 默认的ChatOptions
    // 这里默认设置默认参数，企业级开发根据需求自行调节默认参数
    ChatOptions chatOptions = new ChatOptions(){    // 自定义实现ChatOptions，企业级开发根据需求自行调节默认参数
        @Override
        public String getModel() {
            return defaultChatOptions.getModel();
        }

        @Override
        public Double getFrequencyPenalty() {
            return defaultChatOptions.getFrequencyPenalty();
        }

        @Override
        public Integer getMaxTokens() {
            return defaultChatOptions.getMaxTokens();
        }

        @Override
        public Double getPresencePenalty() {
            return defaultChatOptions.getPresencePenalty();
        }

        @Override
        public List<String> getStopSequences() {
            return defaultChatOptions.getStopSequences();
        }

        @Override
        public Double getTemperature() {
            return defaultChatOptions.getTemperature();
        }

        @Override
        public Integer getTopK() {
            return 0;
        }

        @Override
        public Double getTopP() {
            return defaultChatOptions.getTopP();
        }

        @Override
        public <T extends ChatOptions> T copy() {
            return defaultChatOptions.copy();
        }
    };

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        final ArrayList<String> strings = new ArrayList<>();
        strings.add("**龙门粗口**");

        return builder
                .defaultSystem("你的名字是S-AI-H(SpringAIHelp)，精通Java的同时对Spring框架和SpringAI有深厚的了解" +
                "，可以帮助开发作者开发Spring生态应用，同时你还精通人工智能、大数据、爬虫、后端开发！,你需要正在 {messages} 帮助我")
                .defaultOptions(chatOptions)    // 加入自定义参数，或者中直接使用默认参数defaultChatOptions
                .defaultUser("用中文回复我！") // 默认输入模板
                .defaultTools()
                .defaultAdvisors(
                        new SafeGuardAdvisor(strings,"",1),
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                )  // 默认的Advice
                .build();
    }
}
