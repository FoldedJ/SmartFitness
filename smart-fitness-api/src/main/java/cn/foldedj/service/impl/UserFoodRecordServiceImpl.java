package cn.foldedj.service.impl;

import cn.foldedj.mapper.FoodMapper;
import cn.foldedj.mapper.UserFoodRecordMapper;
import cn.foldedj.pojo.api.ApiResult;
import cn.foldedj.pojo.api.Result;
import cn.foldedj.pojo.dto.DietSaveDTO;
import cn.foldedj.pojo.dto.query.extend.UserFoodRecordQueryDto;
import cn.foldedj.pojo.entity.Food;
import cn.foldedj.pojo.entity.UserFoodRecord;
import cn.foldedj.pojo.vo.UserFoodRecordVO;
import cn.foldedj.service.UserFoodRecordService;
import cn.foldedj.context.LocalThreadHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户饮食记录服务实现
 */
@Service
public class UserFoodRecordServiceImpl implements UserFoodRecordService {

    @Resource
    private UserFoodRecordMapper userFoodRecordMapper;

    @Resource
    private FoodMapper foodMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchSave(DietSaveDTO dietSaveDTO) {
        try {
            // 获取当前用户ID
            Long userId = LocalThreadHolder.getUserId().longValue();
            
            List<Long> foodIds = dietSaveDTO.getFoodIds();
            List<Integer> foodNums = dietSaveDTO.getFoodNums();
            
            if (foodIds == null || foodNums == null || foodIds.size() != foodNums.size()) {
                return ApiResult.error("食物ID和数量不匹配");
            }
            
            List<UserFoodRecord> records = new ArrayList<>();
            
            for (int i = 0; i < foodIds.size(); i++) {
                Long foodId = foodIds.get(i);
                Integer quantity = foodNums.get(i);
                
                // 获取食物信息
                Food food = foodMapper.getById(foodId.intValue());
                if (food == null) {
                    return ApiResult.error("食物ID " + foodId + " 不存在");
                }
                
                // 计算实际营养成分
                BigDecimal consumeAmount = new BigDecimal(quantity);
                BigDecimal servingSize = food.getServingSize();
                BigDecimal ratio = consumeAmount.divide(servingSize, 4, RoundingMode.HALF_UP);
                
                UserFoodRecord record = UserFoodRecord.builder()
                        .userId(userId)
                        .foodId(foodId.intValue())
                        .recordDate(dietSaveDTO.getRecordDate() != null ? dietSaveDTO.getRecordDate() : LocalDate.now())
                        .mealType(dietSaveDTO.getMealType() != null ? dietSaveDTO.getMealType() : 2) // 默认午餐
                        .consumeAmount(consumeAmount)
                        .actualCalories(food.getCalories() != null ? 
                                ratio.multiply(food.getCalories()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                        .actualProtein(food.getProtein() != null ? 
                                ratio.multiply(food.getProtein()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                        .actualCarbohydrate(food.getCarbohydrate() != null ? 
                                ratio.multiply(food.getCarbohydrate()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                        .actualFat(food.getFat() != null ? 
                                ratio.multiply(food.getFat()).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                        .createTime(LocalDateTime.now())
                        .build();
                
                records.add(record);
            }
            
            // 批量保存
            userFoodRecordMapper.batchSave(records);
            
            return ApiResult.success("保存成功");
        } catch (Exception e) {
            return ApiResult.error("保存失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> delete(Integer id) {
        try {
            userFoodRecordMapper.delete(id);
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            return ApiResult.error("删除失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserFoodRecordVO>> query(UserFoodRecordQueryDto queryDto) {
        try {
            List<UserFoodRecordVO> records = userFoodRecordMapper.query(queryDto);
            Integer totalCount = userFoodRecordMapper.queryCount(queryDto);
            return ApiResult.success(records, totalCount);
        } catch (Exception e) {
            return ApiResult.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserFoodRecordVO>> getRecordsByUserIdAndDate(Long userId, String recordDate) {
        try {
            UserFoodRecordQueryDto queryDto = UserFoodRecordQueryDto.builder()
                    .userId(userId)
                    .recordDate(recordDate)
                    .build();
            
            List<UserFoodRecordVO> records = userFoodRecordMapper.query(queryDto);
            return ApiResult.success(records);
        } catch (Exception e) {
            return ApiResult.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    public UserFoodRecord getById(Integer id) {
        return userFoodRecordMapper.getById(id);
    }
}