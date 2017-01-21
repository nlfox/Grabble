package com.example.nlfox.grabble;

import android.content.ClipData;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import mbanje.kurt.fabbutton.FabButton;

public class ScrabbleActivity extends AppCompatActivity implements View.OnClickListener {

    private DataHolder dataHolder;
    private String word;
    private FabButton fabSubmit;
    private Trie t;

    private void resetCharList() {
        t = GrabbleApplication.getAppContext(getApplicationContext()).getTrie();
        ((ViewGroup) findViewById(R.id.topleft)).removeAllViews();
        ((ViewGroup) findViewById(R.id.suggestion1)).removeAllViews();
        ((ViewGroup) findViewById(R.id.suggestion2)).removeAllViews();
        Map<Character, Integer> letter_map = DataHolder.getInstance().getLetters();
        for (char i : letter_map.keySet()) {
            for (int j = 0; j < letter_map.get(i); j++) {
                ImageView imageView = new ImageView(getBaseContext());
                imageView.setTag(i);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.imageview_width), (int) getResources().getDimension(R.dimen.imageview_height));
                imageView.setLayoutParams(layoutParams);
                imageView.setImageResource(getResources().getIdentifier("letter_" + Character.toString(i), "drawable", this.getPackageName()));
                imageView.setOnTouchListener(new MyTouchListener());
                ((ViewGroup) findViewById(R.id.topleft)).addView(imageView);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onRequestSuggest(MenuItem item) {

        ((ViewGroup) findViewById(R.id.suggestion1)).removeAllViews();
        ((ViewGroup) findViewById(R.id.suggestion2)).removeAllViews();
        Boolean pre = true;
        String w = "";
        for (int i = 1; i <= 7; i++) {
            View v = findViewById(getResources().getIdentifier("slot" + Integer.toString(i), "id", this.getPackageName()));
            if (v.getTag().toString().equals("-1")) {
                if (pre)
                    pre = false;
                continue;
            }
            if (!pre && !v.getTag().toString().equals("-1")) {
                showSnackbar("Invalid");
                return;
            }
            w += v.getTag().toString();
        }
        List<String> s = t.getSuggestion(w);
        if (s == null) {
            showSnackbar("No word with the prefix.");
            return;
        }
        for (int i = 0; i < s.size(); i++) {
            ViewGroup v = (ViewGroup) findViewById(getResources().getIdentifier("suggestion" + Integer.toString(i + 1), "id", this.getPackageName()));
            v.removeAllViews();
            for (Character j : s.get(i).toCharArray()) {
                ImageView imageView = new ImageView(getBaseContext());
                imageView.setTag(j);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) getResources().getDimension(R.dimen.imageview_width), (int) getResources().getDimension(R.dimen.imageview_height));
                imageView.setLayoutParams(layoutParams);
                imageView.setImageResource(getResources().getIdentifier("letter_" + Character.toString(j), "drawable", this.getPackageName()));
                v.addView(imageView);
                imageView.setVisibility(View.VISIBLE);
            }
        }

    }

    private void initBlank(Boolean first) {
        for (int i = 1; i <= 7; i++) {
            View v = findViewById(getResources().getIdentifier("slot" + Integer.toString(i), "id", this.getPackageName()));

            if (first) {
                v.setOnDragListener(new ReplaceDragListener());
            } else {
                ((ImageView) v).setImageResource(R.mipmap.ic_round);
            }
            v.setTag(-1);
        }
    }

    private void showSnackbar(String msg) {
        Snackbar.make(findViewById(R.id.scrabble_submit), msg, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrabble);

        resetCharList();
        initBlank(true);

        fabSubmit = (FabButton) findViewById(R.id.scrabble_submit);
        fabSubmit.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.activity_scrabble_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onItemClicked(MenuItem item) {
        initBlank(false);
        resetCharList();
        return true;
    }


    @Override
    public void onClick(View view) {
        word = "";
        fabSubmit.setIndeterminate(true);
        fabSubmit.showProgress(true);

        boolean error = false;
        for (int i = 1; i <= 7; i++) {
            View v = findViewById(getResources().getIdentifier("slot" + Integer.toString(i), "id", this.getPackageName()));
            if (v.getTag().toString().equals("-1")) {
                error = true;
            }
            word += v.getTag().toString();
        }
        if (error || !t.validWord(word)) {
            showSnackbar("Wrong word. Try again");
            word = "";
            initBlank(false);
            resetCharList();
            fabSubmit.showProgress(false);
            fabSubmit.resetIcon();
            return;
        }
        CollectLetterTask t = new CollectLetterTask();
        t.execute();
        Log.v("Submitted: ", word);

        return;


    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");

                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);

                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }


    private class CollectLetterTask extends AsyncTask<Object, Void, Boolean> {
        protected Boolean doInBackground(Object... params) {
            try {

                GrabbleApplication.getAppContext(getApplication()).makeWord(word);
//                GrabbleApplication.getAppContext(getApplication()).updateScoreboard();

            } catch (IOException e) {
                showSnackbar("Network Error. Try again later");
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            initBlank(false);
            fabSubmit.setIndeterminate(false);
            fabSubmit.setProgress(100);
            showSnackbar("Submitted:" + word);

        }

    }


    class ReplaceDragListener implements View.OnDragListener {

        private boolean containsDragable;

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            int dragAction = dragEvent.getAction();
            View dragView = (View) dragEvent.getLocalState();
            if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
                containsDragable = false;
            } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
                containsDragable = true;
            } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
                if (dropEventNotHandled(dragEvent)) {
                    dragView.setVisibility(View.VISIBLE);
                }
            } else if (dragAction == DragEvent.ACTION_DROP && containsDragable) {
                View srcView = (View) dragEvent.getLocalState();
                ViewGroup owner = (ViewGroup) srcView.getParent();
                ImageView targetView = (ImageView) view;
                if (!targetView.getTag().toString().equals("-1")) {
                    srcView.setVisibility(View.VISIBLE);
                } else {
                    owner.removeView(srcView);
                    targetView.setImageDrawable(((ImageView) srcView).getDrawable());
                    targetView.setTag(srcView.getTag());
                }
            }
            return true;
        }

        private boolean dropEventNotHandled(DragEvent dragEvent) {
            return !dragEvent.getResult();
        }


    }


}
