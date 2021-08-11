package com.example.asyncreplacetest

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class Coroutine(progressBar: ProgressBar, url: String, text: TextView) {

    companion object {

        fun BackgroundTask (progressBar: ProgressBar, url: String, text: TextView) {
            //onPreExcute
            progressBar.visibility = View.VISIBLE


            CoroutineScope(Dispatchers.Main).launch {
                //doInBackground
                val Title = async(Dispatchers.Default) {
                    val document = Jsoup.connect(url).timeout(3000).get()
                    val elements = document.select("title")
                    elements[0].text().toString()

                }.await()
                //onPostExcute
                progressBar.visibility = View.GONE
                text.setText(Title)

            }
        }

    }
}