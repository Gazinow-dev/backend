package com.gazi.gazi_renew.service;


import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class JsonSimple {

    public void writeJson(int number) throws IOException{
        String filePath = "./src/main/resources/latestNumber.json";

        // ObjectMapper를 사용하여 JSON 파일 읽기
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(filePath));

        // "number" 필드의 값을 수정
        ((ObjectNode) rootNode).put("number", number);

        // 수정된 JSON 내용을 파일에 쓰기
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new FileWriter(filePath), rootNode);
    }

    public Integer getLastestNumber() throws IOException {
        String filePath = "./src/main/resources/latestNumber.json";

        // ObjectMapper를 사용하여 JSON 파일 읽기
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(filePath));

        // "number" 필드의 값을 반환
        return rootNode.get("number").asInt();

    }

}
