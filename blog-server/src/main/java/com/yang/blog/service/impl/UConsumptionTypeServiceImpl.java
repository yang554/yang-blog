package com.yang.blog.service.impl;

import com.yang.blog.entity.UConsumptionTypeEntity;
import com.yang.blog.mapper.UConsumptionTypeMapper;
import com.yang.blog.service.UConsumptionTypeService;
import com.yang.blog.utils.CommonDate;
import com.yang.blog.utils.RespBean;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UConsumptionTypeServiceImpl implements UConsumptionTypeService {
    @Autowired
    private UConsumptionTypeMapper consumptionTypeMapper;
    @Override
    public RespBean getConsumptionTypeAll() {
        List<UConsumptionTypeEntity> entities = consumptionTypeMapper.getConsumptionTypeAll();
        return RespBean.ok("ok",entities);
    }

    @Override
    public RespBean getConsumptionTypeByName(String name) {
        List<UConsumptionTypeEntity> entities = consumptionTypeMapper.getConsumptionTypeByName(name);
        return RespBean.ok("ok",entities);
    }

    @Override
    public RespBean addConsumptionType(HashMap<String,String> hashMap) {
        String tagName = hashMap.get("tName");
        String tagColor = hashMap.get("tColor");
        String keywords = hashMap.get("tKey");
        UConsumptionTypeEntity entity = new UConsumptionTypeEntity();
        entity.setId(CommonDate.getStamp());
        entity.setName(tagName);
        entity.setColor(tagColor);
        entity.setKeywords(keywords);
        int status = consumptionTypeMapper.addConsumptionType(entity);
        if(status > 0){
            return RespBean.ok("添加标签成功");
        }else {
            return RespBean.error("添加标签失败");
        }
    }

    @Override
    public RespBean updConsumptionTypeById(UConsumptionTypeEntity entity) {
        int status = consumptionTypeMapper.updConsumptionTypeById(entity);
        if(status > 0){
            return RespBean.ok("修改标签成功");
        }else {
            return RespBean.error("修改标签失败");
        }
    }

    @Override
    public List<String> matchCategory(String purpose) {
        List<UConsumptionTypeEntity> sources = consumptionTypeMapper.getConsumptionTypeAll();
        Map<String, Double> categoryScores = new HashMap<>();

        for (UConsumptionTypeEntity source : sources) {
            if(source.getKeywords()  == null ||   "".equals(source.getKeywords()))
                continue;
            String[] strArray = source.getKeywords().split("、");
            List<String> keywords = Arrays.asList(strArray); // 从数据库获取关键词列表
            double score = calculateSimilarity(purpose, keywords);
            if (score > 0) {
                categoryScores.put(source.getName(), score);
            }
        }

        // 按匹配分数排序（分数越高优先级越高）
        return categoryScores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 计算文本与关键词列表的相似度（简化版）
    private double calculateSimilarity(String text, List<String> keywords) {
        return keywords.stream()
                .mapToDouble(keyword -> {
                    // 1. 完全匹配
                    if (text.equals(keyword)) return 1.0;

                    // 2. 双向包含关系
                    boolean textContainsKeyword = text.contains(keyword);
                    boolean keywordContainsText = keyword.contains(text);

                    if (textContainsKeyword || keywordContainsText) {
                        // 计算重叠比例
                        int minLen = Math.min(text.length(), keyword.length());
                        int overlap = textContainsKeyword ? keyword.length() : text.length();
                        return (double) overlap / minLen;
                    }

                    // 3. 相似度算法（余弦相似度）
                    CosineSimilarity cosine = new CosineSimilarity();
                    Map<CharSequence, Integer> vectorA = Arrays.stream(text.split(""))
                            .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
                    Map<CharSequence, Integer> vectorB = Arrays.stream(keyword.split(""))
                            .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));
                    return cosine.cosineSimilarity(vectorA, vectorB);
                })
                .max().orElse(0.0);
    }

    @Override
    public RespBean delConsumptionType(String id) {
        int status = consumptionTypeMapper.delConsumptionType(id);
        if(status > 0){
            return RespBean.ok("删除标签成功");
        }else {
            return RespBean.error("删除标签失败");
        }
    }
}
