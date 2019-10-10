package com.baidu.aip.util;

/**
 * 用于字符串相似度计算
 */
public class Levenshtein
{
    public static int compare(String str, String target)
    {
        int d[][];              // 矩阵
        int n = str.length();
        int m = target.length();
        int i;                  // 遍历str的
        int j;                  // 遍历target的
        char ch1;               // str的
        char ch2;               // target的
        int temp;               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) { return m; }
        if (m == 0) { return n; }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++)
        {                       // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++)
        {                       // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++)
        {                       // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++)
            {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2+32 || ch1+32 == ch2)
                {
                    temp = 0;
                } else
                {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static  int min(int one, int two, int three)
    {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     * 获取两字符串的相似度
     */

    public static float getSimilarityRatio(String str, String target)
    {
        return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
    }

    public static void main(String[] args)
    {
        Levenshtein lt = new Levenshtein();
        String str = "2,长沙 雨花 专区 艺名 晓 条件 净身高 c kg d 特色 深喉 波推 漫游 吹箫 随波逐流 妃吟露珠 丝袜诱惑 情趣内衣 纯天然 波波 超高颜值 极高性价比 给你不一样的感觉 不事 态度 ok 服务 ok 不偷钟 服务 可调教 上粉下嫩 包君满意 ps 分钟 两次 火力 全开";
        String sta = "2,长沙 雨花 专区 艺名 芷若 条件 净身高 d kg d 特色 深喉 波推 漫游 口爆 随波逐流 妃吟露珠 丝袜诱惑 毒龙 纯天然 波波 超高颜值 极高性价比 给你不一样的感觉 不事 态度 ok 服务 ok 不偷钟 服务 可调教 上粉下嫩 包君满意 ps 分钟 两次 火力 全开\n";
        System.out.println("similarityRatio=" + lt.getSimilarityRatio(str, sta));
    }
}