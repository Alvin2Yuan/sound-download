package com.slong.tools;

import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        String url="https://www.aigei.com/sound/cc";
        Document doc = Jsoup.connect(url).get();

        // 获取页面标题
        String title = doc.title();
        System.out.println("Title: " + title);
        //获取一级分类
        Elements groupsList = doc.select(".container-right>.groups-recommend-list");
        for (Element groups : groupsList) {
            //一级分类名称
            String groupName = groups.attr("cnt_stat_target_pkey");
            if("[热门推荐]".equals(groupName)){
                continue;
            }
            Elements groupElements= groups.select(".group .link-item>span");
            //String linkText = left.text();
             for(Element groupElement:groupElements){
                String targetUrl= groupElement.attr("data-short-path-ordered");
                 targetUrl=url+"-"+targetUrl+"?tab=all&orderType=download";
                 Document targetDoc= Jsoup.connect(targetUrl).get();
                 Elements downloads= targetDoc.select("#searchContainer .audio-item-row2 .audio-download-box");
                 for(int i=0;i<10;i++){
                     Element downElement=downloads.get(i);
                     Elements downRows= downElement.select("li.table-row");
                     for(Element down:downRows){
                        String format= down.select(".td-format").get(0).text();
                        if(format.equals("wav")){

                        }
                     }
                 }
                System.out.println(targetUrl);
             }
        }
    }
}
