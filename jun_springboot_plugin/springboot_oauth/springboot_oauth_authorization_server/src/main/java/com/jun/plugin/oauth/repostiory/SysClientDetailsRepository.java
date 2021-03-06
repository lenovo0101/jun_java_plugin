package com.jun.plugin.oauth.repostiory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.jun.plugin.oauth.entity.SysClientDetails;

import java.util.Optional;

/**
 * 客户端信息.
 *
 * @author Wujun
 * @date 2020/1/6 下午1:09
 */
public interface SysClientDetailsRepository extends JpaRepository<SysClientDetails, Long> {

    /**
     * 通过 clientId 查找客户端信息.
     *
     * @param clientId clientId
     * @return 结果
     */
    Optional<SysClientDetails> findFirstByClientId(String clientId);

    /**
     * 根据客户端 id 删除客户端
     *
     * @param clientId 客户端id
     */
    @Modifying
    void deleteByClientId(String clientId);

}
