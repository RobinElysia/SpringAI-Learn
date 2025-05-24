package springai.ai.StructuredOutput;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * @author RobinElysia
 * @version 1.0
 */
@Data
@SuppressWarnings({"all"})
@JsonPropertyOrder(alphabetic = true,value = {"name", "age", "sex"}) // 指定属性的顺序,生成的 JSON Schema 将按 id → status → data 顺序描述属性。
// 通过 alphabetic = true 参数，让未在 value 中列出的字段按字母顺序排序：
public class User {
    private String name;
    private String age;
    private String sex;
}
