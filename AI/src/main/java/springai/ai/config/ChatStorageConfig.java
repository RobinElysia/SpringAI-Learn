package springai.ai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RobinElysia
 * @version 1.0
 */
@Configuration
public class ChatStorageConfig {

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory(); // 默认实现
    }

    // 自定义内存缓存配置
//    @Bean
//    public ChatMemory chatMemory() {
//        return new InMemoryChatMemory(
//                20, // 最大对话轮次
//                new TokenCountEstimator() // 可选的Token计数器
//        );
//    }

    // 添加 持久化配置
//    @Bean
//    public ChatMemory chatMemory(RedisTemplate<String, Object> redisTemplate) {
//        return new RedisChatMemory(
//                redisTemplate,
//                new RedisChatMemoryProperties() {{
//                    setKeyPrefix("ai:chat:");  // Redis key 前缀
//                    setTtl(Duration.ofDays(30));  // 自动过期时间
//                }}
//        );
//    }
}
