package com.mecoo.web.controller.operation;

import com.mecoo.common.core.controller.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: lin
 * @date: 2025-06-26 11:36
 */

@Controller
@RequestMapping("/op/sm")
public class KolSocialMediaController extends BaseController {

    private String prefix = "operation/sm";

    @RequiresPermissions("operation:sm:video:view")
    @GetMapping("/video")
    public String video() {
        return prefix + "/video";
    }

}
