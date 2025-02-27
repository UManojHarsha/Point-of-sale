package com.pos.increff.util;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class GenerateTemplateTsvUtil {
    public static Resource generateTsv(List<String> headers) {
        StringBuilder content = new StringBuilder();
        content.append(String.join("\t", headers)).append("\n");
        return new ByteArrayResource(content.toString().getBytes(StandardCharsets.UTF_8));
    }
}