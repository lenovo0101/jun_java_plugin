package com.jun.plugin.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.jun.plugin.neo4j.model.Lesson;

/**
 * <p>
 * 课程节点Repository
 * </p>
 *
 * @package: com.xkcoding.neo4j.repository
 * @description: 课程节点Repository
 * @author: yangkai.shen
 * @date: Created in 2018-12-24 15:05
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
public interface LessonRepository extends Neo4jRepository<Lesson, String> {
}
