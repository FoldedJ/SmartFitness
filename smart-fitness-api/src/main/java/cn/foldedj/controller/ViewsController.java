package cn.foldedj.controller;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.vo.ChartVO;
import cn.foldedj.service.ViewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 可视化接口
 */
@RestController
@RequestMapping(value = "/views")
public class ViewsController {

    @Resource
    private ViewsService viewsService;

    /**
     * 统计一些系统的基础数据
     *
     * @return Result<List < ChartVO>>
     */
    @GetMapping("/staticControls")
    public Result<List<ChartVO>> staticControls() {
        return viewsService.staticControls();
    }

}
