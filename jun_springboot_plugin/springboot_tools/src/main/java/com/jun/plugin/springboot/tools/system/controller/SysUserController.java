package com.jun.plugin.springboot.tools.system.controller;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jun.plugin.springboot.tools.baseutil.PasswordUtil;
import com.jun.plugin.springboot.tools.baseutil.RedisUtil;
import com.jun.plugin.springboot.tools.common.mdoel.Page;
import com.jun.plugin.springboot.tools.common.mdoel.Result;
import com.jun.plugin.springboot.tools.system.model.SysUser;
import com.jun.plugin.springboot.tools.system.service.SysUserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
	@Autowired
    private SysUserService sysUserService;
	@Autowired
	private RedisUtil redisUtil;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<Page<SysUser>> queryPageList(SysUser user, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
		Result<Page<SysUser>> result = new Result<Page<SysUser>>();
		Page<SysUser> pageList = sysUserService.getPage(user,pageNo,pageSize);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
    @RequiresPermissions("user:add")
	public Result<SysUser> add(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		String selectedRoles = jsonObject.getString("selectedroles");
		String selectedDeparts = jsonObject.getString("selecteddeparts");
		try {
			SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
			user.setCreateTime(new Date());//??????????????????
			String salt = RandomUtil.randomNumbers(8);
			user.setSalt(salt);
			String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
			user.setPassword(passwordEncode);
			user.setStatus(true);
			user.setDelFlag(true);
			sysUserService.addUserWithRole(user, selectedRoles);
            sysUserService.addUserWithDepart(user, selectedDeparts);
			result.success("???????????????");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("????????????");
		}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@RequiresPermissions("user:edit")
	public Result<SysUser> edit(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		try {
			SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
			if(sysUser==null) {
				result.error500("?????????????????????");
			}else {
				SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
				user.setUpdateTime(new Date());
				//String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
				user.setPassword(sysUser.getPassword());
				String roles = jsonObject.getString("selectedroles");
                String departs = jsonObject.getString("selecteddeparts");
				sysUserService.editUserWithRole(user, roles);
                sysUserService.editUserWithDepart(user, departs);
				result.success("????????????!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("????????????");
		}
		return result;
	}

	/**
	 * ????????????
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		this.sysUserService.deleteUser(id);
		return Result.ok("??????????????????");
	}





    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysUser> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.error500("?????????????????????");
        } else {
            result.setResult(sysUser);
            result.setSuccess(true);
        }
        return result;
    }







    /**
     * ???????????????????????????????????????????????????,???????????????,?????????id??????????????????
     *
     * @return
     */
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public Result<String> generateUserId() {
        Result<String> result = new Result<>();
        System.out.println("????????????,????????????ID==============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setResult(userId);
        return result;
    }






	
}
