package com.example.fluxrss;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MyRSSsaxHandler _rssHandler = new MyRSSsaxHandler();

    private TextView _title;
    private TextView _date;
    private TextView _description;
    private ImageView _image;
    private ProgressBar _progressBar;

    private Button nextButton;
    private Button prevButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        _title = findViewById(R.id.imageTitle);
        _date = findViewById(R.id.imageDate);
        _description = findViewById(R.id.imageDescription);
        _image = findViewById(R.id.imageDisplay);
        _progressBar = findViewById(R.id.progressBar);

        nextButton = findViewById(R.id.buttonNext);
        prevButton = findViewById(R.id.buttonPrev);

        ClickListener clickListener = new ClickListener();
        nextButton.setOnClickListener(clickListener);
        prevButton.setOnClickListener(clickListener);

        // Chargement initial du flux RSS
        updateRssFeed("https://www.lemonde.fr/international/rss_full.xml");
    }

    // Classe interne pour gérer les clics des boutons
    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == nextButton) {
                Item item = _rssHandler.nextItem();
                resetDisplay(item);
            } else if (v == prevButton) {
                Item item = _rssHandler.prevItem();
                resetDisplay(item);
            }
        }
    }

    private void updateRssFeed(String url) {
        _rssHandler.setUrl(url);

        runOnUiThread(() -> _progressBar.setVisibility(View.VISIBLE));

        new Thread(() -> {
            try {
                _rssHandler.processFeed();
                Item item = _rssHandler.getFirstItem();

                runOnUiThread(() -> {
                    if (item != null) {
                        resetDisplay(item);
                    } else {
                        Toast.makeText(MainActivity.this, "Aucun article trouvé", Toast.LENGTH_SHORT).show();
                    }
                    _progressBar.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                Log.e("rssview", "Erreur lors du traitement du flux RSS: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Erreur lors du chargement du flux RSS", Toast.LENGTH_SHORT).show();
                    _progressBar.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void resetDisplay(Item item) {
        if (item != null) {
            _title.setText(item.getTitle().toString());
            _date.setText(item.getDate().toString());
            _description.setText(item.getDescription().toString());
            Bitmap image = item.getImage();
            if (image != null) {
                _image.setImageBitmap(image);
            } else {
                _image.setImageResource(R.mipmap.ic_launcher); // Image par défaut
            }
        }
    }
}
