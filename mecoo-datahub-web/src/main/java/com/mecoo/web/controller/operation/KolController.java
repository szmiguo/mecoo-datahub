package com.mecoo.web.controller.operation;

import com.mecoo.common.core.controller.BaseController;
import com.mecoo.common.core.domain.AjaxResult;
import com.mecoo.operation.service.IKolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: lin
 * @date: 2025-06-26 11:36
 */

@Controller
@RequestMapping("/op")
public class KolController extends BaseController {

    @Autowired
    private IKolService kolService ;

    //@RequiresPermissions("operation:list")
    @GetMapping("/list")
    @ResponseBody
    public AjaxResult ok() {

        return AjaxResult.success(kolService.hi());
    }



}
