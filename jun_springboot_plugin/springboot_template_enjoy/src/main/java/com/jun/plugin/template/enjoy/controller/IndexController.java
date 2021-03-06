package com.jun.plugin.template.enjoy.controller;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jun.plugin.template.enjoy.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 主页
 * </p>
 *
 * @package: com.xkcoding.template.enjoy.controller
 * @description: 主页
 * @author: yangkai.shen
 * @date: Created in 2018/10/11 2:22 PM
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Controller
@Slf4j
public class IndexController {

	@GetMapping(value = {"", "/"})
	public ModelAndView index(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();

		User user = (User) request.getSession().getAttribute("user");
		if (ObjectUtil.isNull(user)) {
			mv.setViewName("redirect:/user/login");
		} else {
			mv.setViewName("page/index");
			mv.addObject(user);
		}

		return mv;
	}
}
