package springai.ai.config;

import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpRequest;

/**
 * @author RobinElysia
 * @version 1.0
 */
@Configuration
public class AiLogConfig {
    public SimpleLoggerAdvisor simpleLoggerAdvisor(String userText, String Result) {
        SimpleLoggerAdvisor advisor = new SimpleLoggerAdvisor(
                request -> "Custom userInput: " + userText,
                response -> "Custom advisorLogs: " + Result,
                0
        );
        return advisor;
    }
}
