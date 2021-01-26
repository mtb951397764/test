package com.bdqn.biz;

import cn.itrip.common.ItripTokenVO;
import com.bdqn.exception.TokenTimeProtectedException;

import java.text.ParseException;

public interface TokenService {
    public final static int TOKEN_REPLACEMENT_PROTECTED_TIME = 3600;
    public final static int TOKEN_DELAY_TIME = 120;
    public boolean validateToken(String token, String agent);
    public void deleteToken(String token)throws Exception;
    public ItripTokenVO refreshToken(String token,String agent) throws TokenTimeProtectedException, ParseException;
}
