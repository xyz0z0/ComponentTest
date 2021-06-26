package xyz.xyz0z0.componenttest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import xyz.xyz0z0.arouter_annotations.ARouter;
import xyz.xyz0z0.arouter_annotations.Parameter;
import xyz.xyz0z0.arouter_api.RouterManager;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {


    @Parameter
    String name2; // 序列号 String


    private Button btnToUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnToUserInfo = findViewById(R.id.btnToUserInfo);
        btnToUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
//                startActivity(intent);
//                goUserInfo();

                RouterManager.getInstance().build("/userinfo/UserInfoActivity")
                        .navigation(MainActivity.this);
            }
        });
    }

    private void goUserInfo() {
        // 使用类加载器的方式
        try {
            Class<?> clazz = Class.forName("xyz.xyz0z0.userinfo.UserInfoActivity");
            Intent intent = new Intent(MainActivity.this, clazz);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}