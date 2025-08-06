package com.mecoo.operation.scrapers;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;

import java.util.Arrays;
import java.util.List;

/**
 * BrightData 是爬虫服务商，该类用于对接BrightData的抓取相关接口
 *
 * @author: lin
 * @date: 2025-06-27 11:18
 */
public class BrightDataInstagramScraper {

    public static final String API_AUTH_KEY = "Bearer 04b173ca19fc57f8fa0613c306179f6638d016fa4ba5b9a8c17b37cd4ba0a326";

    public static final String WEBHOOK_API_AUTH_KEY = "Basic am9obkBleGFtcGxlLmNvbTphYmMxMjM";

    //todo 要把回调地址可配置化
    public static final String POST_DETAIL_API_URL = "https://api.brightdata.com/datasets/v3/trigger?dataset_id=gd_lk5ns7kz21pck8jpis&endpoint=https%3A%2F%2Fadmin.szmiguo.com%2Fop%2Fkol%2Fpost%2Fhook%2Fbright-data-ig-collect&auth_header=Basic%20am9obkBleGFtcGxlLmNvbTphYmMxMjM&format=json&uncompressed_webhook=true";

    /**
     * 批量抓取instagram post信息，接口是异步的，调用成功指标是创建任务成功，会返回一个数据快照ID（snapshot_id），如： {"snapshot_id":"s_mce8ljo5bxlig5fb6"}
     * 事后需要通过这个ID去找BrightData获取具体结果
     *
     * @param postLinks Instagram上帖子链接对应的URLs（帖子类型可以是image、reel、carousel）
     */
    public static String createIgPostSracpeTask(List<String> postLinks) {
        List<BrightDataTargetLink> brightDataTargetLinks = BrightDataTargetLink.buildTargetlinks(postLinks);
        System.out.println("brightDataTargetLinks.size() = " + brightDataTargetLinks.size());
        return HttpUtil.createPost(POST_DETAIL_API_URL).auth(API_AUTH_KEY)
                .contentType("application/json").body(JSON.toJSONString(brightDataTargetLinks)).execute().body();
    }


    public static void main(String[] args) {

        List<String> links = Arrays.asList(
            "https://www.instagram.com/reel/DFJw8SGRl0T/",
            "https://www.instagram.com/reel/DGW35jxRO1q/",
            "https://www.instagram.com/reel/DHTLXPWxlHm/",
            "https://www.instagram.com/reel/DKdhzcURpXa/",
            "https://www.instagram.com/reel/DFgyUlTzqcw/",
            "https://www.instagram.com/reel/DGuAriqzj8c/",
            "https://www.instagram.com/reel/DGXONNmPWj5/",
            "https://www.instagram.com/reel/DG-o933PEqF/",
            "https://www.instagram.com/reel/DGmafO0T0PW/",
            "https://www.instagram.com/reel/DFOzx9IzPLU/",
            "https://www.instagram.com/reel/DGLJ8BFzNgE/",
            "https://www.instagram.com/reel/DG6-5okzyPc/",
            "https://www.instagram.com/reel/DGfDgF0Jy-9/",
            "https://www.instagram.com/reel/DG19wPCJg_Q/",
            "https://www.instagram.com/reel/DHDULwJJ6lN/",
            "https://www.instagram.com/reel/DGc36QYvNPC/",
            "https://www.instagram.com/reel/DGnpi-gTV1U/",
            "https://www.instagram.com/reel/DG4nv-5TCPy/",
            "https://www.instagram.com/reel/DHLGyjUht0m/",
            "https://www.instagram.com/reel/DHclIyNhSuU/",
            "https://www.instagram.com/reel/DE4jiFGhXDX/",
            "https://www.instagram.com/reel/DFFOMiphzKP/",
            "https://www.instagram.com/reel/DFce5DeB10i/",
            "https://www.instagram.com/reel/DFuXR6YBUUq/",
            "https://www.instagram.com/reel/DF_rpUzh3eF/",
            "https://www.instagram.com/reel/DGSUVmshAua/",
            "https://www.instagram.com/reel/DGdCf3IKDv2/",
            "https://www.instagram.com/reel/DGu0b0WhoP3/",
            "https://www.instagram.com/reel/DG2mca4hRMs/",
            "https://www.instagram.com/reel/DHLOjP4hynu/",
            "https://www.instagram.com/reel/DHdmzljBpcX/",
            "https://www.instagram.com/reel/DHpyRWFB_Pv/"
        );

        List<String> links2 = Arrays.asList(
            "https://www.instagram.com/reel/DK34aBJBa-2/",
                "https://www.instagram.com/reel/DK34aBJBa-2/",
                "https://www.instagram.com/reel/DK34aBJBa-2/",
                "https://www.instagram.com/reel/DK34aBJBa-2/"

        );


        String body = createIgPostSracpeTask(links2);

        System.out.println("body = " + body);

    }


}
