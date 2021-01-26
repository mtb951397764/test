package com.bdqn.biz;

import cn.itrip.common.ItripTokenVO;
import cn.itrip.common.JredisApi;
import cn.itrip.common.MD5;
import cn.itrip.common.UserAgentUtil;
import cn.itrip.pojo.ItripUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bdqn.exception.TokenTimeProtectedException;
import cz.mallat.uasparser.UserAgentInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class TokenServiceImpl implements TokenService {

    @Resource
    private JredisApi redisApi;
    @Override
    public boolean validateToken(String token, String agent) {
        if(redisApi.getRedis(token) == null)
        {
            return false;
        }
        String code = token.split("-")[4];
        return code.equals(MD5.getMd5(agent,6));
    }

    @Override
    public void deleteToken(String token) throws Exception {
        redisApi.deleteRedis(token);
    }

    @Override
    public ItripTokenVO refreshToken(String token, String agent) throws TokenTimeProtectedException, ParseException {
        if(redisApi.getRedis(token) == null)
        {
            throw new TokenTimeProtectedException("无效的token");
        }
        Date genDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(token.split("-")[3]);
        if((Calendar.getInstance().getTimeInMillis()-genDate.getTime()) <
         TokenService.TOKEN_REPLACEMENT_PROTECTED_TIME)
        {
            throw new TokenTimeProtectedException("仍在保护期，不可置换");
        }
        String oldTokenValue = redisApi.getRedis(token);
        ItripUser user = JSON.parseObject(oldTokenValue, ItripUser.class);
        String newToken = this.generateToken(agent, user);
        redisApi.SetRedis(newToken, JSONArray.toJSONString(user),7200);
        redisApi.SetRedis(token,oldTokenValue,TokenService.TOKEN_DELAY_TIME);
        return new ItripTokenVO(newToken,TokenService.TOKEN_REPLACEMENT_PROTECTED_TIME,genDate.getTime());
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
