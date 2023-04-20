package com.slong.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Hello world!
 *
 */
public class SeleniumApp
{
    static {
        WebDriverManager.chromedriver().setup();
    }

    public static void main( String[] args ) throws IOException, InterruptedException {
        String outDir="D:\\sound";
        String defaultDownloadPath = System.getProperty("user.home") + "/Downloads";
        String resultXlsx="d:\\sound\\下载记录.xlsx";
        if(args.length>1){
            outDir=args[0];
            resultXlsx=args[1];
        }
        System.out.println("当前默认下载目录："+defaultDownloadPath);
        System.out.println("需要输出目录："+outDir);
       //如果输出目录不存在则自动创建文件夹
        File outFile = new File(outDir);
        if(!outFile.exists()){
            outFile.mkdirs();
        }
        List<Map<String,Object>> resultData=new ArrayList<Map<String, Object>>();
        File resultFile=new File(resultXlsx);
        if(resultFile.exists()){
            ExcelReader reader= ExcelUtil.getReader(resultXlsx);
            resultData=reader.readAll();
        }

        String url = "https://www.aigei.com/sound/cc";
       ChromeOptions options=new ChromeOptions();
//        options.addArguments("--start-maximized", "--disable-extensions");
        options.addArguments("--start-maximized", "--safebrowsing-disable-download-protection","--disable-features=RendererCodeIntegrity","--disable-extensions","--download.default_directory="+outDir);
//        options.addArguments("download.default_directory="+outDir);
//        options.addArguments("download.prompt_for_download=false");
//        options.addArguments("profile.default_content_settings.popups=0");
       options.setExperimentalOption("debuggerAddress","127.0.0.1:9222");
       ChromeDriver driver=new ChromeDriver(options);
       Thread.sleep(2000);

       List<WebElement> leftList= driver.findElementsByCssSelector(".container-left-item");
       int size=0;
        int size2=0;
        //点击左侧一级分类
        flag:
        for(WebElement left:leftList){
           left.click();
           //资源名称
           String resourceName=left.getText();
           if("热门推荐".equals(resourceName)){
               continue ;
           }
            size++;
            if(size>1){
                continue;
            }
           Thread.sleep(2000);
            System.out.println("资源名称:"+resourceName);
           //点击右侧小分类
         List<WebElement> groups= driver.findElementsByCssSelector("div.groups-recommend-list.active  div.group");
         for(WebElement group:groups){
             size2++;
             if(size2>1){
                 break;
             }
             //二级分类
             String groupName= group.getAttribute("cnt_stat_target_pkey").replaceAll("\\[","").replaceAll("]","");
             System.out.println("二级分类:"+groupName);
             //三级分类集合
             List<WebElement> linkItems= group.findElements(By.className("link-item"));
             int max3=0;
             for(WebElement linkItem:linkItems){
                 max3++;
                 if(max3>1){
                     break ;
                 }
                 //三级分类名称
                 String itemName=linkItem.getText();
                 System.out.println("三级分类:"+itemName);
                 linkItem.click();
                 Thread.sleep(2000);
                 driver.findElement(By.xpath("//*[@id=\"dim_ul_scope_of_license_4\"]/li[1]/span[1]")).click();
                 Thread.sleep(2000);
                 driver.findElement(By.xpath("//*[@id=\"tab-mount-nav\"]/div/div/span[1]/a[2]")).click();
                 Thread.sleep(2000);
                 List<WebElement> searchItemList= driver.findElementsByCssSelector("#searchContainer .audio-item-box");
                 //取前10条
                 for(int i=0;i<2;i++){
                     JavascriptExecutor js = (JavascriptExecutor) driver;
                     js.executeScript("window.scrollTo(0, 500)");
                     WebElement searchItem=searchItemList.get(i);
                     //音效资源名称
                     String soundName= searchItem.findElement(By.className("title-name")).getText();
                     System.out.println("音效资源名称:"+soundName);
                     //作者
                     String author="";
                    List<WebElement> authorEle= searchItem.findElements(By.className("author"));
                    if(authorEle.size()!=0){
                        author= authorEle.get(0).getAttribute("title");
                    }

                     //license
                     String license="";
                     List<WebElement> licenseEle=  searchItem.findElements(By.className("license"));
                     if(licenseEle.size()!=0){
                         license=licenseEle.get(0).getAttribute("title");
                     }
                    //组装记录对象
                     JSONObject sound=new JSONObject();
                     sound.set("资源分类",resourceName);
                     sound.set("二级分类",groupName);
                     sound.set("三级分类",itemName);
                     sound.set("音效资源名称",soundName);
                     sound.set("作者",author);
                     sound.set("协议类型",license);
                     //检查音乐是否被下载过 如果存在不继续下载
                     if(exist(resultData,sound)){
                         continue ;
                     }
                     searchItem.findElement(By.cssSelector(".audio-download-box span:first-child")).click();
                     Thread.sleep(1000);
                     //点击下载
                     searchItem.findElement(By.cssSelector("ul.dropdown-menu>li.table-row")).click();

                     //点击弹窗
                     try {
                         WebDriverWait wait = new WebDriverWait(driver, 2);
                         WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(" div.modal-footer")));
                         WebElement msg = driver.findElement(By.cssSelector(".bootstrap-dialog > div > div > div.modal-body"));
                         String text=msg.findElement(By.className("coin-tip-my")).getText();
                         text=text.substring(text.indexOf("余额")+2);
                         System.out.println(text);
                         if("0枚".equals(text)){
                             break flag;
                         }
                         element.findElement(By.cssSelector("div>button:nth-child(1)")).click();

                     }catch (Exception e){}
                     //判断是否下载完成，如果下载完成 就从默认目录移动到指定目录
                     File downLoadFile= getFile(defaultDownloadPath,soundName);
                     if(null!=downLoadFile){
                         // downLoadFile=FileUtil.rename(downLoadFile,newName,true);
                         File targetFile=new File(outDir+"/"+downLoadFile.getName());
                         //文件可能在合并直接移动会提示被占用
                         long maxLength=1024*1024*10; //如果文件大于10M就多等两秒
                         if(downLoadFile.length()>maxLength){
                             Thread.sleep(2000);
                         }
                         Thread.sleep(2000);
                         FileUtil.move(downLoadFile,targetFile,true);
                         ExcelWriter writer=ExcelUtil.getWriter(resultFile);
                         writer.autoSizeColumnAll();
                         resultData.add(sound);
                         writer.write(resultData,true);
                         writer.close();
                     }
             }

            }
         }
       }
        System.out.println(driver.getTitle());
    }

    public static boolean exist(List<Map<String,Object>> dataList, JSONObject item){
        if(dataList.isEmpty()){
            return false;
        }
        for(Map<String,Object> data:dataList){
            String key=data.get("资源分类").toString()+data.get("二级分类")+data.get("三级分类")
                    +data.get("音效资源名称")+data.get("作者")+data.get("协议类型");
            String key2=item.get("资源分类").toString()+item.get("二级分类")+item.get("三级分类")
                    +item.get("音效资源名称")+item.get("作者")+item.get("协议类型");
            if(key2.equals(key)){
                return true;
            }

        }
        return false;
    }

    public static File getFile(String folder,String fileName) throws InterruptedException {
        int maxWait=60*10;  //每次等1秒，60次等1分钟 最多等10分钟
        int count=0;
        while (count<maxWait){
            count++;
            File dir=new File(folder);
            if(!dir.exists()){
                return null;
            }
            for(File file :dir.listFiles()){
                String name=file.getName();
                if(name.endsWith(".crdownload")){
                    continue;
                }
                if(name.startsWith(fileName)){
                    return file;
                }
            }
            Thread.sleep(1000);
        }


        return null;
    }
}
