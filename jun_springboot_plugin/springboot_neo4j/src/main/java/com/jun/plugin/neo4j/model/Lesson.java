package com.jun.plugin.neo4j.model;

import lombok.*;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.jun.plugin.neo4j.config.CustomIdStrategy;
import com.jun.plugin.neo4j.constants.NeoConsts;

/**
 * <p>
 * 课程节点
 * </p>
 *
 * @package: com.xkcoding.neo4j.model
 * @description: 课程节点
 * @author: yangkai.shen
 * @date: Created in 2018-12-24 14:55
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Builder
@NodeEntity
public class Lesson {
    /**
     * 主键，自定义主键策略，使用UUID生成
     */
    @Id
    @GeneratedValue(strategy = CustomIdStrategy.class)
    private String id;

    /**
     * 课程名称
     */
    @NonNull
    private String name;

    /**
     * 任教老师
     */
    @Relationship(NeoConsts.R_TEACHER_OF_LESSON)
    @NonNull
    private Teacher teacher;
}
