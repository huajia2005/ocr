package com.baidu.aip.run.mapper;


import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 */
@Mapper
public interface OcrMapper {
    /**
     * 插入图片违规记录
     * @Author xuhongchun
     * @Description
     * @Date 13:28 2019/10/11
     * @Param [imageName, sort]
     * @return int
     */
    int insertViolationRecord(String imageName, String sort);
}
