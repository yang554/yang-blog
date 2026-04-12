package com.yang.blog.mapper;

import com.yang.blog.entity.TFamilyTreeEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TFamilyTreeMapper {
    //查询所有族谱的统计信息
    List<TFamilyTreeEntity> getFamilyTree();
    List<TFamilyTreeEntity> getAll();
    int addTree(TFamilyTreeEntity entity);
    int saveTree(TFamilyTreeEntity entity);
    int delTree(Long id);
}
