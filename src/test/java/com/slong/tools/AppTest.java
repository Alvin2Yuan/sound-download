package com.slong.tools;

import cn.hutool.core.util.URLUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testUrl(){
        String url="https://home.aigei.com:8443/0-r3/GeiFileLocalStore/b61/src/aud/wav/47/47f8db67d7a042a280d4c0a41e5a2f1c.wav?download/%E4%BA%BA%E7%BE%A4%E9%AA%9A%E5%8A%A8%E5%98%B2%E7%AC%91%E5%A3%B0_%E7%88%B1%E7%BB%99%E7%BD%91_aigei_com.wav&e=1682089200&token=P7S2Xpzfz11vAkASLTkfHN7Fw-oOZBecqeJaxypL:sTcKoZAM5ITVaXbp33tmrpNDHJY=";
        url= URLUtil.decode(url);
        String fileName=url.substring(url.indexOf("download/")+9,url.indexOf("&e"));
        System.out.println(fileName);
        System.out.println(url);
        String[] a=new String[]{"1","2","3","4"};
       String b= a[3];
        System.out.println(a.length);
    }
}
