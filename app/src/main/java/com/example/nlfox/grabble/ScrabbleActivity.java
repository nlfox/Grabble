package com.example.nlfox.grabble;

import android.content.ClipData;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;

public class ScrabbleActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Character> letter_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrabble);
        letter_list = new ArrayList<Character>();
        //dummy data for test
        letter_list.add('a');
        letter_list.add('b');
        letter_list.add('a');
        letter_list.add('b');
        letter_list.add('a');
        letter_list.add('b');
        letter_list.add('a');
        letter_list.add('b');

        for (char i : letter_list) {
            ImageView imageView = new ImageView(getBaseContext());
            imageView.setTag(i);
            imageView.setImageResource(getResources().getIdentifier("ic_" + Character.toString(i), "mipmap", this.getPackageName()));
            imageView.setOnTouchListener(new MyTouchListener());
            ((ViewGroup) findViewById(R.id.topleft)).addView(imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        for (int i = 1; i <= 7; i++) {
            View v = findViewById(getResources().getIdentifier("slot" + Integer.toString(i), "id", this.getPackageName()));
            v.setOnDragListener(new ReplaceDragListener());
            v.setTag(-1);
            //v.setOnTouchListener(new ReplaceTouchListener());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.activity_scrabble_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onItemClicked(MenuItem item) {
        ((ViewGroup) findViewById(R.id.topleft)).removeAllViews();
        for (char i : letter_list) {
            ImageView imageView = new ImageView(getBaseContext());
            imageView.setTag(i);
            imageView.setImageResource(getResources().getIdentifier("ic_" + Character.toString(i), "mipmap", this.getPackageName()));
            imageView.setOnTouchListener(new MyTouchListener());

            ((ViewGroup) findViewById(R.id.topleft)).addView(imageView);
            imageView.setVisibility(View.VISIBLE);
        }
        for (int i = 1; i <= 7; i++) {
            View v = findViewById(getResources().getIdentifier("slot" + Integer.toString(i), "id", this.getPackageName()));
            ((ImageView) v).setImageResource(R.mipmap.ic_round);
            v.setTag(-1);
            //v.setOnTouchListener(new ReplaceTouchListener());
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_refresh: {
                break;
            }
            case R.id.fab: {
                for (int i = 1; i <= 7; i++) {
                    View v = findViewById(getResources().getIdentifier("slot" + Integer.toString(i), "id", this.getPackageName()));
                    Log.v("sss", String.valueOf(v.getTag()));
                }
                break;
            }
        }
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
