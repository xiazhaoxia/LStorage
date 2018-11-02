package com.example.suxia.lstorageplugin;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import  android.widget.Button;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private  String localStorageFileName;
    private  String LOG_TAG="localstorage";

    public static void printAllFiles(String baseDirName) {

        File baseDir = new File(baseDirName); // create a  file object
        if (!baseDir.exists() || !baseDir.isDirectory()) { // check if the dir existed
            System.out.println("find file failed?" + baseDirName + "not a directory?");
        }
        String tempName = null;
        File tempFile;
        File[] files = baseDir.listFiles();
        try{
            if(files!=null){
                for (int i = 0; i < files.length; i++) {
                    tempFile = files[i];
                    tempName = tempFile.getName();
                    if (tempFile.isDirectory()  && !tempName.equalsIgnoreCase("Snapshots")) {
                        System.out.println("findFiles->tempDir:"+tempFile.toString());
                        printAllFiles(tempFile.getAbsolutePath());
                    } else if (tempFile.isFile()) {
                        System.out.println("findFiles->tempFile:"+tempFile.toString());
                    }
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            System.out.println("findFiles->tempFile4:"+ex.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClear= (Button)findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dbFilePath="/data/data/"+ getApplicationContext().getPackageName() +"/app_webview/Local Storage/";
                printAllFiles(dbFilePath);
                File dbfile=new File(dbFilePath);
                if(dbfile.exists()) {
                    Log.i(LOG_TAG, "Open Sqlite :" + dbfile.getAbsolutePath());
                }else{
                    Log.i(LOG_TAG, "Sqlite not exist :" + dbfile.getAbsolutePath());
                }
            }
        });


        //String url="https://slc14umv.us.oracle.com:16690/siebel/app/epharmam/enu";
        String url="http://www.baidu.com";
        WebView wbBrowser=(WebView)findViewById(R.id.wbBrowser);

        WebSettings wSet = wbBrowser.getSettings();
        wSet.setJavaScriptEnabled(true);
        wSet.setDatabaseEnabled(true);
        wSet.setAllowFileAccess(true);
        wSet.setAllowContentAccess(true);
        wbBrowser.setFocusable(true);
        wbBrowser.setFocusableInTouchMode(true);
        wSet.setJavaScriptEnabled(true);
        //mWebview.getSettings().setPluginsEnabled(true);
        //mWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wSet.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wSet.setDomStorageEnabled(true);
        wSet.setDatabaseEnabled(true);
        wSet.setAppCacheEnabled(true);

        //如果不设置个属性，webview不支持localstorage
        wSet.setDomStorageEnabled(true);

        wbBrowser.loadUrl(url);
        wbBrowser.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("URL", "shouldoverrideurlloading:----"+url.toString());
                view.loadUrl(url);
                return true;
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                Log.i("URL", "wk,zoumeiyzou a -----:" + request.toString());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String currentUrl = request.getUrl().toString();
                    try {
                        URL url = new URL(currentUrl);
                        String protocal = url.getProtocol();
                        int port = url.getPort();
                        String host = url.getHost();
                        if (port < 0) {
                            port = 0;
                        }
                        localStorageFileName = protocal + "_" + host + "_" + port + ".localstorage";
                        Log.i("URL", "currentUrl -----:" + currentUrl);
                        Log.i("URL", "localStorageFileName -----:" + localStorageFileName);
                        view.loadUrl(request.getUrl().toString());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else {
                    view.loadUrl(request.toString());
                }
                return true;
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.i("URL", "有错误啊 -----:"+error.toString());
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
    }
}
