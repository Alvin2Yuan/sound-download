package com.slong.tools;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String url = "https://www.aigei.com/sound/cc";
       ChromeOptions options=new ChromeOptions();

        options.addArguments("download.default_directory="+outDir);
        options.addArguments("download.prompt_for_download=false");
        options.addArguments("profile.default_content_settings.popups=0");
       options.setExperimentalOption("debuggerAddress","127.0.0.1:9222");
       ChromeDriver driver=new ChromeDriver(options);
       List<WebElement> leftList= driver.findElementsByCssSelector(".container-left-item");
       int size=0;
        int size2=0;
        flag:
        for(WebElement left:leftList){
           left.click();
           if(size>0){
               continue;
           }

           size++;


           Thread.sleep(2000);
         List<WebElement> linkItems= driver.findElementsByCssSelector("div.groups-recommend-list.active  div.group .link-item");
         for(WebElement linkItem:linkItems){
             size2++;
             if(size2>10){
                 break;
             }
             System.out.println(linkItem.getText());
             linkItem.click();
             Thread.sleep(2000);
             driver.findElement(By.xpath("//*[@id=\"dim_ul_scope_of_license_4\"]/li[1]/span[1]")).click();
             Thread.sleep(2000);
             driver.findElement(By.xpath("//*[@id=\"tab-mount-nav\"]/div/div/span[1]/a[2]")).click();
             Thread.sleep(2000);
            List<WebElement> downloadList= driver.findElementsByCssSelector("#searchContainer .audio-download-box");
            //取前10条
            for(int i=0;i<10;i++){
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollTo(0, 200)");
                WebElement down=downloadList.get(i);
                System.out.println(down.toString());
                down.findElement(By.cssSelector("span:first-child")).click();
                Thread.sleep(1000);
                downloadList= driver.findElementsByCssSelector("#searchContainer .audio-download-box");
                down.findElement(By.cssSelector("ul.dropdown-menu>li.table-row")).click();
               String currentWind= driver.getWindowHandle();

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
                    element.findElement(By.cssSelector("div>button:nth-child(2)")).click();

                }catch (Exception e){}
                driver.switchTo().window(currentWind);
            }
         }
       }
        System.out.println(driver.getTitle());
    }
}
