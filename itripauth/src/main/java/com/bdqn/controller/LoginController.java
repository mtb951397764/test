package com.bdqn.controller;

import cn.itrip.common.*;
import cn.itrip.dao.itripUser.ItripUserMapper;
import cn.itrip.pojo.ItripUser;
import cn.itrip.pojo.ItripUserVO;
import com.alibaba.fastjson.JSONArray;
import com.bdqn.biz.TokenServiceImpl;
import com.bdqn.exception.LoginException;
import cz.mallat.uasparser.UserAgentInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/api")
public class LoginController {

    @Resource
    private ItripUserMapper dao;

    @Resource
    private JredisApi jredisApi;

    @Resource
    private TokenServiceImpl tokenService;

    @Resource
    private MessageSent sent;

    @RequestMapping(value = "/dologin",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
    @ResponseBody
    public Dto doLogin(String name, String password, HttpServletRequest request)
    {
        ItripUser user = dao.doLogin(name, MD5.getMd5(password,32));
        if(user != null)
        {
            String agent = request.getHeader("User-Agent");
            System.out.println(agent);
            String token = generateToken(agent,user);
            jredisApi.SetRedis(token, JSONArray.toJSONString(user),7200);
            System.out.println(token);
            ItripTokenVO tokenVO = new ItripTokenVO(token, Calendar.getInstance().getTimeInMillis()+7200,Calendar.getInstance().getTimeInMillis());
            return DtoUtil.returnDataSuccess(tokenVO);
        }
        else
        {
            return DtoUtil.returnFail("登录失败！",ErrorCode.AUTH_AUTHENTICATION_FAILED);
        }
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public Dto logout(HttpServletRequest request) throws Exception {
        String token = request.getHeader("token");
        String agent = request.getHeader("User-Agent");
        if(tokenService.validateToken(token,agent))
        {
            tokenService.deleteToken(token);
            return DtoUtil.returnSuccess("您已注销~");
        }
        else
        {
            return DtoUtil.returnFail("Token验证无效",ErrorCode.AUTH_TOKEN_INVALID);
        }
    }
    @RequestMapping(value = "/registerbyphone",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public Dto registerByPhone(@RequestBody ItripUserVO vo, HttpServletRequest request) throws Exception {
        Integer testNum = dao.exists(vo.getUserCode());
        if(testNum != null)
        {
            return DtoUtil.returnFail("注册失败，用户已存在！",ErrorCode.AUTH_USER_ALREADY_EXISTS);
        }
        ItripUser itripUser = new ItripUser();
        itripUser.setUserCode(vo.getUserCode());
        itripUser.setUserPassword(MD5.getMd5(vo.getUserPassword(),32));
        itripUser.setUserName(vo.getUserName());
        itripUser.setActivated(0);

        int count = dao.insertItripUser(itripUser);
        if(count > 0)
        {
            int random = MD5.getRandomCode();
            jredisApi.SetRedis(vo.getUserCode(),""+random,3600);
            sent.sentMassage(vo.getUserCode(),""+random,"5");
            return DtoUtil.returnSuccess("注册成功！");
        }
        return DtoUtil.returnFail("注册失败",ErrorCode.AUTH_UNKNOWN);
    }

    @RequestMapping(value = "/doregister",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public Dto registerByEmail(@RequestBody ItripUserVO vo, HttpServletRequest request) throws Exception {
        Integer testNum = dao.exists(vo.getUserCode());
        if(testNum != null)
        {
            return DtoUtil.returnFail("注册失败，用户已存在！",ErrorCode.AUTH_USER_ALREADY_EXISTS);
        }
        ItripUser itripUser = new ItripUser();
        itripUser.setUserCode(vo.getUserCode());
        itripUser.setUserPassword(MD5.getMd5(vo.getUserPassword(),32));
        itripUser.setUserName(vo.getUserName());
        itripUser.setActivated(0);

        int count = dao.insertItripUser(itripUser);
        if(count > 0)
        {
            int random = MD5.getRandomCode();
            jredisApi.SetRedis(vo.getUserCode(),""+random,3600);
            EmailTestUtil.sentMessage(vo.getUserCode(),""+random,"5");
            return DtoUtil.returnSuccess("注册成功！");
        }
        return DtoUtil.returnFail("注册失败",ErrorCode.AUTH_UNKNOWN);
    }

    @RequestMapping(value = "/activate",method = RequestMethod.PUT,produces = "application/json;charset=utf-8")
    @ResponseBody
    public Dto emailActive(String user,String code)
    {
        String value = jredisApi.getRedis(user);
        if(value != null && value.equals(code))
        {
            dao.activeItripUser(user,1);
            return DtoUtil.returnSuccess("激活成功！");
        }
        return DtoUtil.returnFail("验证码有误，请重新输入",ErrorCode.AUTH_ACTIVATE_FAILED);
    }
    @RequestMapping(value = "/validatephone",method = RequestMethod.PUT,produces = "application/json;charset=utf-8")
    @ResponseBody
    public Dto validate(String user,String code,HttpServletRequest request)
    {
        String value = jredisApi.getRedis(user);
        if(value != null && value.equals(code))
        {
            dao.activeItripUser(user,1);
            return DtoUtil.returnSuccess("激活成功！");
        }
        return DtoUtil.returnFail("验证码有误，请重新输入",ErrorCode.AUTH_ACTIVATE_FAILED);
    }
    /**
     * 判断用户名密码是否正确
     * @param userCode   用户名
     * @param password   密码
     * @return           用户对象
     * @throws Exception
     */
    public ItripUser checkLogin(String userCode,String password) throws Exception
    {
        Map params = new HashMap();
        params.put("userCode",userCode);
        params.put("userPassword",params);
        List<ItripUser> result = dao.getItripUserListByMap(params);
        if(result == null)
        {
            return null;
        }
        else
        {
            ItripUser user = result.get(0);
            if(user.getActivated() == 0)
            {
                throw new LoginException("账户未激活");
            }
            return user;
        }
    }



    private String generateToken(String agent, ItripUser user) {
        // TODO Auto-generated method stub
        try {
            UserAgentInfo userAgentInfo = UserAgentUtil.getUasParser().parse(
                    agent);
            StringBuilder sb = new StringBuilder();
            sb.append("token:");//统一前缀
            if (userAgentInfo.getDeviceType().equals(UserAgentInfo.UNKNOWN)) {
                if (UserAgentUtil.CheckAgent(agent)) {
                    sb.append("MOBILE-");
                } else {
                    sb.append("PC-");
                }
            } else if (userAgentInfo.getDeviceType()
                    .equals("Personal computer")) {
                sb.append("PC-");
            } else
                sb.append("MOBILE-");
//			sb.append(user.getUserCode() + "-");
            sb.append(MD5.getMd5(user.getUserCode(),32) + "-");//加密用户名称
            sb.append(user.getId() + "-");
            sb.append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    + "-");
            sb.append(MD5.getMd5(agent, 6));// 识别客户端的简化实现——6位MD5码

            return sb.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


}
