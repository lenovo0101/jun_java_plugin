/**
 * @Auther: yuanyuan
 * @Date: 2018/8/29 16:01
 * @Description:
 */
package com.jun.plugin.springbootjunit.test1;


import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jun.plugin.springbootjunit.SpringbootJunitApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringbootJunitApplication.class)
@WebAppConfiguration
public class BaseJunitTest {

}
