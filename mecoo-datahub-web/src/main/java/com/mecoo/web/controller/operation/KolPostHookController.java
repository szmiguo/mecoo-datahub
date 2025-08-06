package com.mecoo.web.controller.operation;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.mecoo.common.core.controller.BaseController;
import com.mecoo.common.core.domain.AjaxResult;
import com.mecoo.operation.domain.KolPost;
import com.mecoo.operation.enums.SocialMediaPlatform;
import com.mecoo.operation.enums.SocialMediaType;
import com.mecoo.operation.service.IKolService;
import com.mecoo.operation.service.IKolPostService;
import com.mecoo.web.controller.operation.dto.KolPostDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: lin
 * @date: 2025-06-26 11:36
 */

@Slf4j
@Controller
@RequestMapping("/op/kol/post/hook")
public class KolPostHookController extends BaseController {

    @Autowired
    private IKolService kolService;

    @Autowired
    private IKolPostService kolSmService;

    //@RequiresPermissions("operation:list")
    @GetMapping("/list")
    @ResponseBody
    public AjaxResult ok() {

        return AjaxResult.success(kolService.hi());
    }

    /**
     * BrightData 抓取Instagram数据的回调接口
     * @param data
     * @return
     */
    @PostMapping("/bright-data-ig-collect")
    @ResponseBody
    public AjaxResult brightDdataHook(@RequestBody List<KolPostDTO> data) {
        // 业务处理逻辑
        //todo 需要处理接收到的异常数据
        log.info("接收到BrightData WebHook 通知，总共收到数据：{} 条", data.size());
        log.info("接收到BrightData WebHook 通知，数据详情为：{} ", JSONObject.toJSONString(data));

        List<KolPost> kolPosts = BeanUtil.copyToList(data, KolPost.class);
        kolPosts.forEach(kolPost -> {
            kolPost.setPostSm(SocialMediaPlatform.INSTAGRAM);
        });
        kolSmService.saveKolSmVideos(kolPosts);

        return AjaxResult.success();
    }


}
