package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.vo.ChartVO;

import java.util.List;

public interface ViewsService {

    Result<List<ChartVO>> staticControls();

}
