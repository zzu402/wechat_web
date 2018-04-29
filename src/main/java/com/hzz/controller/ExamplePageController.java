package com.hzz.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Controller
public class ExamplePageController {
    @RequestMapping(value ="/ui-elements", method = RequestMethod.GET)
    public String uiElementsPage(){
        return "example/ui-elements";
    }

    @RequestMapping(value ="/index", method = RequestMethod.GET)
    public String indexPage(){
        return "example/index";
    }

    @RequestMapping(value ="/chart", method = RequestMethod.GET)
    public String chartPage(){
        return "example/chart";
    }
    @RequestMapping(value ="/tab-panel", method = RequestMethod.GET)
    public String tabPanelPage(){
        return "example/tab-panel";
    }

    @RequestMapping(value ="/table", method = RequestMethod.GET)
    public String tablePage(){
        return "example/table";
    }

    @RequestMapping(value ="/form", method = RequestMethod.GET)
    public String formPage(){
        return "example/form";
    }

    @RequestMapping(value ="/empty", method = RequestMethod.GET)
    public String emptyPage(){
        return "example/empty";
    }
}
