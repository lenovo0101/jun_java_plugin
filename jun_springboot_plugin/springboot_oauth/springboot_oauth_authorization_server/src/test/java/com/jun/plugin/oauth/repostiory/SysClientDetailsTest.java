package com.jun.plugin.oauth.repostiory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.jun.plugin.oauth.repostiory.SysClientDetailsRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * .
 *
 * @author Wujun
 * @date 2020/1/6 下午1:10
 */
@DataJpaTest
public class SysClientDetailsTest {
    @Autowired
    private SysClientDetailsRepository sysClientDetailsRepository;

    @Test
    public void autowiredSuccessWhenPassed() {
        assertNotNull(sysClientDetailsRepository);
    }

}
