package com.cirt.ctf.index;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping(value = {"/","/index"})
    String getIndex(Model model){
        return "index";
    }
}
