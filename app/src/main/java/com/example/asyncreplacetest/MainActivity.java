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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        asyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SomethingAsync somethingAsync = new SomethingAsync(progressBar);
                somethingAsync.execute();
            }
        });

    }

    public void init() {

        progressBar.findViewById(R.id.progressBar);
        asyncButton.findViewById(R.id.asyncButton);
        rxJavaButton.findViewById(R.id.rxJavaButton);
        corountineButton.findViewById(R.id.corountineButton);
        titleTextView.findViewById(R.id.titleTextView);
    }


    public class SomethingAsync extends AsyncTask<Void, Integer, String> {

        ProgressBar progressBar;


        public SomethingAsync(ProgressBar progressBar) {
            this.progressBar = progressBar;
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
                document = Jsoup.connect(URL).timeout(3000).get();
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
            Document document = Jsoup.connect(URLs).timeout(3000).get();
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

                    rxBackgroundTask.dispose();
                });
    }


}