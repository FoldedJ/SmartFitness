package cn.foldedj.service;

import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.query.extend.NewsSaveQueryDto;
import cn.foldedj.pojo.entity.NewsSave;
import cn.foldedj.pojo.vo.NewsSaveVO;

import java.util.List;

public interface NewsSaveService {

    Result<Void> save(NewsSave newsSave);

    Result<Void> batchDelete(List<Long> ids);

    Result<List<NewsSaveVO>> query(NewsSaveQueryDto newsSaveQueryDto);

    Result<Void> operation(NewsSave newsSave);


}
