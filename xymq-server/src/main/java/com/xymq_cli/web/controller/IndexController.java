package com.xymq_cli.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 可视化界面主页
 * @author 黎勇炫
 * @date 2022年07月25日 21:46
 */
@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index(){
        return "index";
    }
}
