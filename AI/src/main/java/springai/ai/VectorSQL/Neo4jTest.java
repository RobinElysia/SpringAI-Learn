package springai.ai.VectorSQL;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.image.*;
import org.springframework.ai.model.Media;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeTypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author RobinElysia
 * @version 1.0
 */
@SuppressWarnings({"all"})
public class Neo4jTest {
    @Autowired
    private VectorStore vectorStore;

    public String test(@RequestBody Image ImageFile) {
        List<Document> documents = new ArrayList<>();

        // 先实现二层接口 EmbeddingModel 和 模型选项
        EmbeddingOptions embeddingOptions = new EmbeddingOptions() {
            @Override
            /// 模型名称
            public String getModel() {
                return "模型名称";
            }

            @Override
            /// 嵌入维度
            public Integer getDimensions() {
                return 1536; // 嵌入维度，越大越nb，但是性能也会...需要平衡精度与计算资源
            }
        };
        EmbeddingModel embeddingModel = new EmbeddingModel() {
            @Override
            /// 重写调用模型
            public EmbeddingResponse call(EmbeddingRequest request) {
                final List<String> instructions = request.getInstructions();
                return embedForResponse(instructions);
            }

            @Override
            /// 重写嵌入方法
            public float[] embed(Document document) {
                ArrayList<String> objects = new ArrayList<>();
                objects.add(document.getText());
                EmbeddingRequest embeddingRequest = new EmbeddingRequest(objects, embeddingOptions);
                EmbeddingResponse call = call(embeddingRequest);
                float[] output = call.getResult().getOutput();
                return output;
            }
        };

        // 图像封装 -> 文档
        Document document = new Document(
                Media.builder()
                        .mimeType(MimeTypeUtils.IMAGE_JPEG)
                        .data(ImageFile)
                        .build(),
                Map.of("描述","内容"));

        // 嵌入操作（如果需要）
        float[] embed = embeddingModel.embed(document);
        return "ok";
    }
}
