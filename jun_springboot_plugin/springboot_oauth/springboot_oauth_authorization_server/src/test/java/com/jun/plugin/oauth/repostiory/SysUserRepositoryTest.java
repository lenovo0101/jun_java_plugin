package com.jun.plugin.oauth.repostiory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jun.plugin.oauth.entity.SysUser;
import com.jun.plugin.oauth.repostiory.SysUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


/**
 * .
 *
 * @author Wujun
 * @date 2020/1/6 下午1:25
 */
@DataJpaTest
public class SysUserRepositoryTest {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Test
    public void autowiredSuccessWhenPassed() {
        assertNotNull(sysUserRepository);
    }

    @Test
    @DisplayName("测试关联查询")
    public void queryUserAndRoleWhenPassed() {
        Optional<SysUser> admin = sysUserRepository.findFirstByUsername("admin");
        assertTrue(admin.isPresent());
        SysUser sysUser = admin.orElseGet(SysUser::new);
        assertNotNull(sysUser.getRoles());
        assertEquals(1, sysUser.getRoles().size());
    }
}
