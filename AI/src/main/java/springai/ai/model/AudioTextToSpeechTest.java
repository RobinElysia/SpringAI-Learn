package springai.ai.model;

import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.audio.speech.*;
import org.springframework.ai.openai.metadata.audio.OpenAiAudioSpeechMetadata;
import org.springframework.ai.openai.metadata.audio.OpenAiAudioSpeechResponseMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author RobinElysia
 * @version 1.0
 * 需要自行导入依赖，这里因为支支持openai的语音模型
 */
@RestController
@RequestMapping("api")
@SuppressWarnings({"all"})
public class AudioTextToSpeechTest {
    // Text-to-Speech
    @Autowired
    private StreamingSpeechModel streamingSpeechModel;
    @Autowired
    private SpeechPrompt speechPrompt;
    @Autowired
    private SpeechMessage speechMessage;
    @Autowired
    private SpeechResponse speechResponse;
    @PostMapping(value = "Audio", produces = "text/event-stream;charset=UTF-8")
    public void AudioContext(@RequestBody Map Map) {

        SpeechPrompt speechPrompt = new SpeechPrompt("提示词",
                new OpenAiAudioSpeechOptions.Builder().build());

        SpeechModel speechModel = new SpeechModel() {
            @Override
            public SpeechResponse call(SpeechPrompt request) {
                return this.call(speechPrompt);// 实现自定义call方法
            }
        };
        // 获取响应数据
        SpeechResponse call = speechModel.call(speechPrompt); // 传入prompt，获取响应
        OpenAiAudioSpeechResponseMetadata metadata = call.getMetadata();
        Speech result = call.getResult();

        List<Speech> results = call.getResults();
        for (Speech speech : results) {
            byte[] output = speech.getOutput();
            System.out.println(output);
            OpenAiAudioSpeechMetadata metaData = speech.getMetadata();
            System.out.println(metaData);
        }
    }
}
