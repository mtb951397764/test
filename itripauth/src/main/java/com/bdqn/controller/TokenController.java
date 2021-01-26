package com.bdqn.controller;

import cn.itrip.common.Dto;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ItripTokenVO;
import com.bdqn.biz.TokenServiceImpl;
import com.bdqn.exception.TokenTimeProtectedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@Controller
@RequestMapping("/api")
public class TokenController {
    @Resource
    private TokenServiceImpl tokenService;
    @RequestMapping(value = "/retoken",method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public Dto refreshToken(HttpServletRequest request,String token)
    {
        //String token = request.getHeader("token");
        String agent = request.getHeader("agent");
        ItripTokenVO tokenVO = null;
        try {
            tokenVO = tokenService.refreshToken(token,agent);
        } catch (TokenTimeProtectedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DtoUtil.returnDataSuccess(tokenVO);
    }



}
