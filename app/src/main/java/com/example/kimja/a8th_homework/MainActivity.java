package com.example.kimja.a8th_homework;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    EditText et;
    Animation anim_top, anim;
    LinearLayout linearLayout;
    ListView listView;
    GridLayout grid;

    ArrayList<String> urls = new ArrayList<String>();
    ArrayList<url> url2 = new ArrayList<url>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Myweb");

        init();
    }
    void init(){
        webView = (WebView)findViewById(R.id.webview);

        et = (EditText)findViewById(R.id.editText);
        linearLayout = (LinearLayout)findViewById(R.id.linear);
        listView = (ListView)findViewById(R.id.listview);
        grid = (GridLayout)findViewById(R.id.grid);
        webView.addJavascriptInterface(new JavaScriptMethods(), "Myapp");
        //
        WebSettings webSettings = webView.getSettings();
        // 자바스크립트 사용하기
        webSettings.setJavaScriptEnabled(true);
        // WebView 내장 Zoom 사용
        webSettings.setBuiltInZoomControls(true);
        // 확대,축소 기능을 사용할 수 있도록 설정
        webSettings.setSupportZoom(true);
        // 캐쉬 사용 방법을 정의(default : LOAD_DEFAULT)
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        webView.setWebViewClient(new WebViewClient(){
            @Override //웹 로딩중 띄우기
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loading...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                et.setText(url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress >= 100) dialog.dismiss();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });


        webView.loadUrl("http://www.hanyang.ac.kr");

        anim_top = AnimationUtils.loadAnimation(this,R.anim.traslate_top);
        anim_top.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.setVisibility(View.GONE); //리니어레이아웃 사라지게
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim = AnimationUtils.loadAnimation(this,R.anim.anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        //리스트뷰
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, urls);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listView.setVisibility(View.GONE);
                grid.setVisibility(View.VISIBLE);
                linearLayout.setAnimation(anim);
                anim.start();
                //url주소받아와서 그 주소로 이동
                et.setText(url2.get(position).getUrl());
                webView.loadUrl(et.getText().toString());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("삭제")
                        .setMessage("항목을 삭제하시겠습니까?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //항목 지우기
                                urls.remove(position);
                                url2.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("Cancel",null)
                        .show();
                adapter.notifyDataSetInvalidated();
                return true;
            }
        });


    }
    //옵션
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0 ,"즐겨찾기추가");
        menu.add(1, 2, 1 ,"즐겨찾기목록");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1){
            if(listView.getVisibility() == View.VISIBLE){
                listView.setVisibility(View.GONE);
                grid.setVisibility(View.VISIBLE);
            }
            //local에있는 html 불러오기
            webView.loadUrl("file:///android_asset/www/urladd");
            linearLayout.setAnimation(anim_top);
            anim_top.start();
        } else if(item.getItemId() == 2){
            if(listView.getVisibility() == View.GONE){
                listView.setVisibility(View.VISIBLE);
                grid.setVisibility(View.GONE);
            } else {
                listView.setVisibility(View.GONE);
                grid.setVisibility(View.VISIBLE);
            }
            linearLayout.setAnimation(anim_top);
            anim_top.start();
        }
        return super.onOptionsItemSelected(item);
    }

    Handler myhandler = new Handler();
    class JavaScriptMethods {
        @JavascriptInterface  //<< 웹페이지 호출하기위해
        public void displayToast() {
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setAnimation(anim);
                    anim.start();
                }
            });
        }
        @JavascriptInterface  //<< 웹페이지 호출하기위해
        public void add(final String name,final String url) {
            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    //같은거 있는지 없는지 구분기능 추가해야함
                    for(int i = 0; i <url2.size();i++){
                        if(url2.get(i).getUrl() == url){


                        }
                    }
                    url2.add(new url(name, url));
                    urls.add(new String("<" +url2.get(url2.size()-1).name +"> "+ url2.get(url2.size()-1).url));  //추가
                }
            });
        }
    }

    public void onClick( View v ){
        if (v.getId() == R.id.button){
            webView.loadUrl(et.getText().toString());
        }
    }

}

