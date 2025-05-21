package springai.ai.model;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.image.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springai.ai.config.AiLogConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author RobinElysia
 * @version 1.0
 */
@RestController
@RequestMapping("api")
@SuppressWarnings({"all"})
public class ImageTest {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private AiLogConfig aiLogConfig;
    @Autowired
    private ImageModel imageModel;
    @Autowired
    private ImageResponse imageResponse;

    @PostMapping(value = "image", produces = "text/event-stream;charset=UTF-8")
    public String sendMessage(@RequestBody Map Map) {
        final ImageOptions imageOptions = new ImageOptions() {
            @Override
            public Integer getN() {
                return 0;
            }

            @Override
            public String getModel() {
                return "";
            }

            @Override
            public Integer getWidth() {
                return 0;
            }

            @Override
            public Integer getHeight() {
                return 0;
            }

            @Override
            public String getResponseFormat() {
                return "";
            }

            @Override
            public String getStyle() {
                return "";
            }
        }; // 选项重写（这里我不清楚有没有默认选项）

        final ImageMessage imageMessage = new ImageMessage("提示词",1F); // 图片消息定义
        final ImageMessage imageMessage1 = new ImageMessage("提示词1",1F); // 图片消息定义

        List<ImageMessage> prompt = new ArrayList(); // 批量
        prompt.add(imageMessage);
        prompt.add(imageMessage1);

        final ImagePrompt imagePrompt = new ImagePrompt(prompt,ImageOptionsBuilder.builder().build()); // 提示词整合

        final List<ImageMessage> instructions = imagePrompt.getInstructions();
        final ImageOptions options = imagePrompt.getOptions();

        final ImageResponse call = imageModel.call(imagePrompt); // 放入提示词

        // 接收响应
        final List<ImageGeneration> results = imageResponse.getResults();
        for (ImageGeneration imageGeneration :results) {
            System.out.println(imageGeneration.getMetadata());
            System.out.println(imageGeneration.getOutput());
        }

        final ImageGeneration result = imageResponse.getResult();
        final ImageResponseMetadata metadata = imageResponse.getMetadata();

        return call.toString();
    }
}
