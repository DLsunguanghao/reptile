package com.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * info:
 * Created by shang on 16/5/13.
 */
public class Test {


    /**
     * 打开连接,获取页面
     * @param urlStr
     * @return
     */
    private String getHtml(String urlStr) {
        URL url;
        try {
            url=new URL(urlStr);
            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                return StreamTool(inputStream);
            } else {
                System.out.println("获取页面失败....");
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 流中读取html
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String StreamTool(InputStream inputStream) throws IOException {
        StringBuffer stringBuffer=new StringBuffer();
        int len=0;
        byte[] buf=new byte[1024];
        while ((len = inputStream.read(buf)) != -1) {
            stringBuffer.append(new String(buf,0,len,"UTF-8"));
        }
        inputStream.close();
        return stringBuffer.toString();
    }

    private List<String> getContent(String html,String url) {
        List<String> list =new ArrayList<String>();
        Document document=Jsoup.parse(html);
        Element element=document.getElementsByClass("center_right2").get(0);
        Elements elements=element.getElementsByClass("right1");
        for (Element e : elements) {
            Node childNode=e.childNode(1);
            String href=url+childNode.attr("href");//下个网页路径
            String ht=getHtml(href);
            Document doc=Jsoup.parse(ht);
            Element el=doc.getElementsByClass("center_right").get(0);

            Node n1=el.childNode(1);

            Node n2=n1.childNode(7);
            Node n3=n2.childNode(1);

            String imgUrl=n3.attr("src");
            list.add(imgUrl);
        }

        return list;
    }

    private void download(List<String> list) {
        for (String s : list) {
            GetImage.getImage(s);//下载图片
        }

    }


    public static void main(String[] args) {
        String url="http://www.meitubar.com";
        Test test=new Test();
        for (int i = 1; i <30 ; i++) {
            String ur=url+"/photos/?page="+i;
            System.out.println("页面路径:"+ur);
            String html=test.getHtml(ur);
            if (html != null) {
                List<String> list = test.getContent(html,url);
                test.download(list);
            }
        }

    }
}
