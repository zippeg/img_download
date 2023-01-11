package core.Image;
import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Data
public class HttpCore {
    /**
     *
     */
  public static short GET =0;
  public static short POST =1;
  private CloseableHttpClient httpClient;
  public  String path;
  private ExecutorService threadPool;
    public HttpCore() {
        httpClient=HttpClients.createDefault();
        threadPool=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        File file = new File("C:\\下载图片");
        if (!file.exists()) {
            file.mkdir();
        }
        path=file.getAbsolutePath()+"\\";
    }

    public    void downloadImage(String url, short type){
        switch (type){
            case 0:
                httpGetDownload(url);
                break;
            case 1:
                httpPostDownload(url);
                break;
        }
        }

    /**
     * 下载某个具体页面的套图
     * https://meirentu.cc/pic/311753603060.html
      * @param url
     */
    private  void httpGetDownload(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        httpGet.setHeader("Referer","http://meirentu.cc/model/%E7%8E%8B%E9%A6%A8%E7%91%B6.html");
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode()==200) {//请求成功
                String html= EntityUtils.toString(httpResponse.getEntity());
                Document document = Jsoup.parse(html);
                Elements content_left = document.getElementsByClass("content_left");
                Elements img_tags = content_left.first().getElementsByTag("img");
                Element title = document.getElementsByClass("item_title").first();
                String dir = title.getElementsByTag("h1").first().text();
                mkdir(dir);
                for (Element img_tag : img_tags) {
                    String img_src = img_tag.attr("src");
                    imgDownload(img_src,dir);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void mkdir(String dir) {
        File file = new File(path + dir);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     * 下载网络图片
     * @param img_src
     */
    private void imgDownload(String img_src,String dir) {

        URLConnection urlConnection = null;
        try {
//            https://cdn3.mmdb.cc
            String a=img_src.replaceAll("cdn3","cdn2");
            String b=a.replaceAll("cdn1","cdn2");
            String url=b.replaceAll("cdn4","cdn2");
            urlConnection = new URL(url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("连接异常");
        }
        try {
            InputStream inputStream = urlConnection.getInputStream();
            String fileName=path+"\\" +dir+"\\"+UUID.randomUUID().toString().substring(0,8)+".png";
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            byte[] bytes = new byte[1024];
            int len=0;
            while ((len = inputStream.read(bytes, 0,bytes.length)) != -1) {
                fileOutputStream.write(bytes,0,len);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void downloadAllImgForModel(String url){
        List<String> urls= checkUrl(url);
        if (urls!=null) {
            //创建一个线程池
            CountDownLatch countDownLatch = new CountDownLatch(urls.size());
            Vector<String> vector = new Vector<>(urls);
            for (String img_url : vector) {
                threadPool.execute(()->{
                   downloadImage(img_url,(short) 0);
                   countDownLatch.countDown();
                });
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> checkUrl(String url) {
        //https://meirentu.cc/pic/311753603060-x.html
        List<String> urls=null;
        String baseUrl="https://meirentu.cc/pic/";
        String tailUrl=".html";
        int maxPage=0;
        url=url.replaceAll("http:","https:");
        String id = url.replaceAll(baseUrl, "").replaceAll(tailUrl, "");
        //获取最大页数
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(new HttpGet(url));
            if (httpResponse.getStatusLine().getStatusCode()==200) {
                String html = EntityUtils.toString(httpResponse.getEntity());
                Document document = Jsoup.parse(html);
                Element div_page_tag = document.getElementsByClass("page").first();
                Elements a_tags = div_page_tag.getElementsByTag("a");
                if (a_tags.size()>1){
                    urls=new ArrayList<String>();
                    Element element = a_tags.get(a_tags.size() - 2);
                    maxPage=Integer.parseInt(element.text());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 1; i < maxPage+1; i++) {
            String target=baseUrl+id+"-"+i+tailUrl;
            urls.add(target);
        }

        return urls;
    }

    public void  downloadModelAllImage(String model_url){
            List<String> models_url=parseModelHtml(model_url);
//          models_url = models_url.subList(0, 5);
        for (String model : models_url) {
            downloadAllImgForModel(model);
        }
    }

    private List<String> parseModelHtml(String model_url) {
        List<String> models_url=null;
        HttpGet httpGet = new HttpGet(model_url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String html = EntityUtils.toString(response.getEntity());
            Document document = Jsoup.parse(html);
            Element ul_tag = document.getElementsByClass("update_area_lists cl").first();
            Elements lis_tag = ul_tag.getElementsByTag("li");
            String path="https://meirentu.cc";
            models_url=new ArrayList<>();
            for (Element li : lis_tag) {
                String tail = li.getElementsByTag("a").first().attr("href");
                String url=path+tail;
                models_url.add(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return models_url;
    }

    /**
     * todo
     * @param url
     */
    private  void httpPostDownload(String url) {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void stop(){
        threadPool.shutdownNow();
    }
}
