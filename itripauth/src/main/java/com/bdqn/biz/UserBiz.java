package com.bdqn.biz;

import cn.itrip.pojo.ItripUser;

public interface UserBiz {
    public boolean checkUserByUserCode(String userCode) throws Exception;

    public void itripxAddUserByPhone(ItripUser itripUser) throws Exception;

    public boolean validateUserByUserCode(String phone,String code) throws Exception;


}
