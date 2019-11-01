package com.baidu.aip.run.util;

import lombok.extern.slf4j.Slf4j;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version 1.0
 * @ClassName WordUtil
 * @Author Administrator
 * @Date 2019/10/30 16:09
 */
@Component
@Slf4j
public class WordUtil {

    //项目启动扫描bean时,初始化word分词器词库
    {
        log.info("----------初始化word分词器---------");
        WordSegmenter.seg("test");
    }

    public static List<Word> splitWords(String word) {
        return WordSegmenter.seg(word);
    }
}
