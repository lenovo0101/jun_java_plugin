package com.jun.plugin.oauth.server;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.jun.plugin.oauth.server.web.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * 2017-12-05
 *
 * @author Wujun
 */
public class SpringOauthServerServletInitializer extends SpringBootServletInitializer {


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        //主版本号
        servletContext.setAttribute("mainVersion", WebUtils.VERSION);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringOauthServerApplication.class);
    }

}
