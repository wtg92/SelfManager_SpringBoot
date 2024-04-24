package work;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;

public class DEBUG_Decode {
    public static void main(String[] args) throws Exception {
        String utf8JsonString = "{\"name\":\"東京都大田区東糀谷１－４６－３\",\"age\":30}"; // UTF-8编码的JSON字符串

        // 将UTF-8编码的JSON字符串转换为EUC-JP编码的JSON字符串
        String eucJPJsonString = convertToJson(utf8JsonString);

        // 输出结果
        System.out.println("UTF-8编码的JSON字符串: " + utf8JsonString);
        System.out.println("对应的EUC-JP编码的JSON字符串: " + eucJPJsonString);
    }

    public static String convertToJson(String utf8JsonString) throws Exception {
        // 解析JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(utf8JsonString);

        // 遍历JSON对象，将其中的文本内容逐个转换为EUC-JP编码
        convertJsonNode(jsonNode);

        // 将JSON对象转换为字符串
        return objectMapper.writeValueAsString(jsonNode);
    }

    public static void convertJsonNode(JsonNode jsonNode) {
        if (jsonNode.isObject()) {
            jsonNode.fields().forEachRemaining(entry -> {
                JsonNode value = entry.getValue();
                if (value.isTextual()) {
                    String utf8Text = value.asText();
                    String eucJPText = new String(utf8Text.getBytes(Charset.forName("UTF-8")), Charset.forName("EUC-JP"));
                    ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).put(entry.getKey(), eucJPText);
                } else {
                    convertJsonNode(value);
                }
            });
        } else if (jsonNode.isArray()) {
            jsonNode.elements().forEachRemaining((e)->convertJsonNode(e));
        }
    }
}
