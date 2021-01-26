package cn.itrip.controller;

import cn.itrip.common.Dto;
import cn.itrip.common.DtoUtil;
import cn.itrip.dao.itripAreaDic.ItripAreaDicMapper;
import cn.itrip.dao.itripLabelDic.ItripLabelDicMapper;
import cn.itrip.pojo.ItripAreaDic;
import cn.itrip.pojo.ItripLabelDic;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/api")
public class BizController {

    @Resource
    private ItripAreaDicMapper dao;

    @Resource
    private ItripLabelDicMapper dao2;
    @RequestMapping("/hotel/queryhotcity/{id}")
    @ResponseBody
    public Dto getHotList(@PathVariable("id")String type) throws Exception {
        List<ItripAreaDic> list = dao.getItripAreaDicByType(type);
        return DtoUtil.returnDataSuccess(list);
    }
    @RequestMapping("/hotel/queryhotelfeature")
    @ResponseBody
    public Dto getLabelList()
    {
        List<ItripLabelDic> list = dao2.getItripLabelDic();
        return DtoUtil.returnDataSuccess(list);
    }
}
