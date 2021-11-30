package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewClick(View view) {
        ViewGroup group= (ViewGroup) view;
        TextView textView= (TextView) group.getChildAt(1);
        String text = textView.getText().toString();
        Toast.makeText(this,text+"被点击了",Toast.LENGTH_SHORT).show();
    }
}