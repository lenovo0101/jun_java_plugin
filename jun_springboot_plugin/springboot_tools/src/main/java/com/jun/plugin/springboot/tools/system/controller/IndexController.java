package com.jun.plugin.springboot.tools.system.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.jun.plugin.springboot.tools.baseutil.JwtUtil;
import com.jun.plugin.springboot.tools.baseutil.MD5Util;
import com.jun.plugin.springboot.tools.baseutil.PasswordUtil;
import com.jun.plugin.springboot.tools.baseutil.RedisUtil;
import com.jun.plugin.springboot.tools.common.config.CacheConstant;
import com.jun.plugin.springboot.tools.common.config.CommonConstant;
import com.jun.plugin.springboot.tools.common.config.DefContants;
import com.jun.plugin.springboot.tools.common.controller.BaseController;
import com.jun.plugin.springboot.tools.common.mdoel.Result;
import com.jun.plugin.springboot.tools.system.aspect.LoginUser;
import com.jun.plugin.springboot.tools.system.mapper.SysDepartMapper;
import com.jun.plugin.springboot.tools.system.model.SysDepart;
import com.jun.plugin.springboot.tools.system.model.SysLoginModel;
import com.jun.plugin.springboot.tools.system.model.SysUser;
import com.jun.plugin.springboot.tools.system.service.SysDepartService;
import com.jun.plugin.springboot.tools.system.service.SysUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wujun
 */
@Slf4j
@Api(tags="system")
@RestController
@RequestMapping("/sys")
public class IndexController extends BaseController {

	private static final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

@Autowired
private SysUserService sysUserService;
@Autowired
private SysDepartService sysDepartService;
@Autowired
private RedisUtil redisUtil;
	/**
	 * ??????????????????
	 *
	 * @return ????????????
	 */
	@GetMapping("/testlogin")
	@ApiOperation(value = "????????????", notes = "????????????")
	public String login() {
		return "login";
	}

	/**
	 * ????????????
	 *
	 * @return ??????
	 */
	@GetMapping("/doLogin/{username}")
	@ResponseBody
	@ApiOperation(value = "??????", notes = "??????")
	public Result<?> doLogin(@ApiParam(name = "username", value = "username", required = true)@PathVariable(name = "username")String username,
						  @ApiParam(name = "user01", value = "user01", required = true) @RequestParam(name = "user01")String user01) {

		SysUser user = new SysUser();
		user.setId(username);
		user.setUsername(username);
		user.setRealname(user01);
		sysUserService.add(user);

//		UsernamePasswordToken token = new UsernamePasswordToken("admin", "123456");
//		Subject subject = SecurityUtils.getSubject();
//		subject.login(token);
		return  Result.ok("ok");
	}

	/**
	 * ???????????????
	 */
	@ApiOperation("???????????????")
	@GetMapping(value = "/getCheckCode")
	public Result<Map<String,String>> getCheckCode(){
		Result<Map<String,String>> result = new Result<Map<String,String>>();
		Map<String,String> map = new HashMap<String,String>();
		try {
			String code = RandomUtil.randomString(BASE_CHECK_CODES,4);
			String key = MD5Util.MD5Encode(code+System.currentTimeMillis(), "utf-8");
			redisUtil.set(key, code, 60);
			map.put("key", key);
			map.put("code",code);
			result.setResult(map);
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
		}
		return result;
	}
	/**
	 * ????????????
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public Result<Object> logout(HttpServletRequest request, HttpServletResponse response) {
		//??????????????????
		String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
		if(StringUtils.isEmpty(token)) {
			return Result.error("?????????????????????");
		}
		String username = JwtUtil.getUsername(token);
		SysUser sysUser = sysUserService.getUserByName(username);
		if(sysUser!=null) {
			log.info(" ?????????:  "+sysUser.getRealname()+",??????????????? ");
			//??????????????????Token??????
			redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
			//??????????????????Shiro????????????
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			//????????????????????????????????????????????????????????????sys:cache:user::<username>
			redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
			//??????shiro???logout
			SecurityUtils.getSubject().logout();
			return Result.ok("?????????????????????");
		}else {
			return Result.error("Token??????!");
		}
	}
	@ApiOperation("????????????")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel){
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		Object checkCode = redisUtil.get(sysLoginModel.getCheckKey());
		if(checkCode==null) {
			result.error500("???????????????");
			return result;
		}
		if(!checkCode.equals(sysLoginModel.getCaptcha())) {
			result.error500("???????????????");
			return result;
		}

		//1. ????????????????????????
		SysUser sysUser = sysUserService.getUserByName(username);
		result = (Result<JSONObject>) sysUserService.checkUserIsEffective(sysUser);
		if(!result.isSuccess()) {
			return result;
		}

		//2. ????????????????????????????????????
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("????????????????????????");
			return result;
		}

		//??????????????????
		userInfo(sysUser, result);

		return result;
	}

	/**
	 * ????????????
	 *
	 * @param sysUser
	 * @param result
	 * @return
	 */
	private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
		String syspassword = sysUser.getPassword();
		String username = sysUser.getUsername();
		// ??????token
		String token = JwtUtil.sign(username, syspassword);
		// ??????token??????????????????
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME*2 / 1000);

		// ????????????????????????
		JSONObject obj = new JSONObject();
		List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
		obj.put("departs", departs);
		if (departs == null || departs.size() == 0) {
			obj.put("multi_depart", 0);
		} else if (departs.size() == 1) {
			sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
			obj.put("multi_depart", 1);
		} else {
			obj.put("multi_depart", 2);
		}
		obj.put("token", token);
		obj.put("userInfo", sysUser);
		result.setResult(obj);
		result.success("????????????");
		return result;
	}

}
