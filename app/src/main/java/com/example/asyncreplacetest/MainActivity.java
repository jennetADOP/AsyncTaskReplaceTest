package com.example.asyncreplacetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    AppCompatButton asyncButton;
    AppCompatButton rxJavaButton;
    AppCompatButton corountineButton;
    TextView titleTextView;

    String title;

    Disposable rxBackgroundTask;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        url = "https://www.yna.co.kr/view/AKR20210811064300004";

        asyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SomethingAsync somethingAsync = new SomethingAsync(progressBar, url);
                somethingAsync.execute();
            }
        });

        rxJavaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxJavaBackgroundTask(url);
            }
        });

        corountineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Coroutine coroutine = new Coroutine(progressBar, url, titleTextView);
            }
        });

    }

    public void init() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        asyncButton = (AppCompatButton) findViewById(R.id.asyncButton);
        rxJavaButton = (AppCompatButton) findViewById(R.id.rxJavaButton);
        corountineButton= (AppCompatButton) findViewById(R.id.corountineButton);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
    }


    public class SomethingAsync extends AsyncTask<Void, Integer, String> {

        ProgressBar progressBar;
        String url;


        public SomethingAsync(ProgressBar progressBar, String url) {
            this.progressBar = progressBar;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            title ="";
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Document document = null;

            try {
                document = Jsoup.connect(url).timeout(3000).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(document != null){
                Elements element = document.select("title");
                title = element.get(0).text();

                return "";
            }

            return title;
        }

        @Override
        protected void onPostExecute(String string) {
            progressBar.setVisibility(View.GONE);
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
    }



    public void RxJavaBackgroundTask(String URLs) {
        //onPreExcute
        progressBar.setVisibility(View.VISIBLE);

        rxBackgroundTask = Observable.fromCallable(() -> {
        //doInBackground
            title ="";
            Document document = Jsoup.connect(url).timeout(3000).get();
            Elements elements = document.select("title");
            title = elements.get(0).text();
            Log.d("로그 doInBackground", title);

            return false;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    //onPostExcute
                    titleTextView.setVisibility(View.VISIBLE);
                    titleTextView.setText(title);
                    progressBar.setVisibility(View.GONE);
                    rxBackgroundTask.dispose();
                });
    }





}