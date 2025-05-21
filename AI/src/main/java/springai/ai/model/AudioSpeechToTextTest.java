package springai.ai.model;

import org.springframework.ai.audio.transcription.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springai.ai.config.ImpAudioToTextModel;

import java.util.List;

/**
 * @author RobinElysia
 * @version 1.0
 */
@RestController
@RequestMapping("api")
@SuppressWarnings({"all"})
public class AudioSpeechToTextTest {
    // Speech-to-Text
    @Autowired
    private AudioTranscriptionResponseMetadata audioTranscriptionResponseMetadata;

    @PostMapping(value = "Audio", produces = "text/event-stream;charset=UTF-8")
    public String AudioContext(@RequestBody Resource audioResource){

        // 提示词+模型选项
        AudioTranscriptionPrompt audioTranscriptionPrompt =
                new AudioTranscriptionPrompt(audioResource, new AudioTranscriptionOptions() {
            @Override
            public String getModel() {
                return "模型名称";
            }
        });
        AudioTranscriptionOptions options = audioTranscriptionPrompt.getOptions(); // 输入的选项参数
        Resource InputResource = audioTranscriptionPrompt.getInstructions(); // 输入的数据

        // 自定义模型
        ImpAudioToTextModel impAudioToTextModel = new ImpAudioToTextModel() {
            @Override
            public AudioTranscriptionResponse call(AudioTranscriptionPrompt request) {
                AudioTranscriptionResponse call = this.call(request);
                return call;
            }
        };

        // 拿到相应的结果
        AudioTranscriptionResponse audioTranscriptionResponse = impAudioToTextModel.call(audioTranscriptionPrompt);
        // 获取响应
        AudioTranscriptionResponseMetadata metadata = audioTranscriptionResponse.getMetadata();
        AudioTranscription result = audioTranscriptionResponse.getResult();

        List<AudioTranscription> results = audioTranscriptionResponse.getResults();
        for (AudioTranscription transcription :results) {
            AudioTranscriptionMetadata metaData = transcription.getMetadata();
            System.out.println(metaData);
        }

        return result.getOutput();
    }
}
