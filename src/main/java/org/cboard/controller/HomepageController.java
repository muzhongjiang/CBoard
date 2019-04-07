package org.cboard.controller;

import java.util.HashMap;
import java.util.Map;

import org.cboard.services.BoardService;
import org.cboard.services.HomepageService;
import org.cboard.services.ServiceStatus;
import org.cboard.services.job.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * "主页"相关该功能Controller
 */
@RestController
@RequestMapping("/homepage")
public class HomepageController extends BaseController {

    @Autowired
    private HomepageService homepageService;
    
    @Autowired
    private BoardService boardService;
    
    @Value("${admin_user_id}")
    private String adminUserId;

    @RequestMapping(value = "/saveHomepage")
    public ServiceStatus saveHomepage(@RequestParam(name = "boardId", required = true) Long boardId) {
        String userId = tlUser.get().getUserId();
        return homepageService.saveHomepage(boardId, userId);
    }

    /**
     * 重置主页
     */
    @RequestMapping(value = "/resetHomepage")
    public ServiceStatus resetHomepage() {
        String userId = tlUser.get().getUserId();
        return homepageService.resetHomepage(userId);
    }
    
    @RequestMapping(value = "/selectHomepage")
    public Long selectHomepage() {
        String userId = tlUser.get().getUserId();
        return homepageService.selectHomepage(userId);
    }


    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    public Map<String, Object> loginPage() {
        String userId = tlUser.get().getUserId();
        // 优先查找当前用户设置的首页:
        Long boardId = homepageService.selectHomepage(userId);
        if(boardId == null) {
            // 如当前用户未设置首页，查找由管理员设置的系统首页：
            Long adminBoardId = homepageService.selectHomepage(adminUserId);
            if(true){//FIXME 是否有 "admin设置的系统首页"的权限(Dashboard权限、Chart权限)：
                boardId=adminBoardId;//使用 "管理员设置的系统首页"
            }else{
                LOG.error("没有'admin设置的系统首页'的权限");
            }
        }

        //====
        Map<String, Object> result = new HashMap<>();
        if(boardId == null) {// 如当前用户和管理员均未设置首页，将使用默认首页
            result.put("url", "");
            result.put("templateUrl", "org/cboard/view/cboard/homepage.html");
            result.put("controller", "homepageCtrl");
        } else {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", boardId);
            result.put("url", "");
            result.put("params", params);
            result.put("templateUrl", "org/cboard/view/dashboard/view.html");
            result.put("controller", "dashboardViewCtrl");
        }
        return result;
    }
}
