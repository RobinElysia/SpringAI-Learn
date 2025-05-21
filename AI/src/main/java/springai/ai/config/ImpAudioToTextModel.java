package springai.ai.config;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.model.Model;
import org.springframework.stereotype.Component;

/**
 * @author RobinElysia
 * @version 1.0
 */
@Component
@FunctionalInterface
@SuppressWarnings({"all"})
public interface ImpAudioToTextModel extends Model<AudioTranscriptionPrompt, AudioTranscriptionResponse>{

    public AudioTranscriptionResponse call(AudioTranscriptionPrompt request);

}
