package com.example.familypedigree;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

//String[][] familyImage={
//        {"张一","男","65","父/母"},
//        {"李怡","女","60","父/母"},
//        {"张二","男","40","户主"},
//        {"王二","女","38","配偶"},
//        {"张二二","女","38","兄弟姐妹"},
//        {"张三一","男","20","子"},
//        {"张三二","女","20","女"},
//        {"赵三","女","20","女婿"},
//        {"张四一","男","2","外孙子"},
//        {"张四二","女","1","外孙女"}};
String[][] familyImage={
//        {"张四搜索","男","2","11"},
//        {"张四一","女","2","11"},
//        {"张一","男","65","4"},
//        {"李怡","女","60","4"},
//        {"张二二","男","40","3"},
//        {"张二","男","40","1"},
//        {"张二三","女","38","3"},
//        {"张二四","女","36","3"},
        {"王二","女","38","2"},
        {"张三一","男","20","5"},  //子
//        {"张三二","女","20","6"},  //女
//        {"张三san","女","20","6"},  //女
        {"张三","男","20","5"},
//        {"赵三","男","20","9"},    //女婿
//        {"张四一","男","2","7"},
//        {"张四二","女","1","8"},
//        {"张四二","女","1","8"},
//        {"张四搜索","男","2","7"},
//        {"张四一","男","2","7"},

};

    FamilyImageView familyImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        familyImageView= (FamilyImageView) findViewById(R.id.fiv);
        familyImageView.startCanver(familyImage);

    }


    public void TiaoZhuan(View view){
//        startActivity(new Intent(MainActivity.this,TwoActivity.class));
        new Thread() {
            @Override
            public void run() {
                familyImageView.saveBitmap();
            }
        }.start();
    }
}
