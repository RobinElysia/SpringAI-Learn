package springai.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("http://localhost:5173") // 允许哪些源的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许哪些方法
                .allowedHeaders("*") // 允许哪些请求头
                .allowCredentials(true) // 是否允许携带认证信息（cookies, HTTP认证及客户端SSL证明等）
                .maxAge(3600); // 缓存CORS信息的时间（秒），默认是1800秒
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader("Authorization", "Bearer ${spring.ai.deepseek.api-key}")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
