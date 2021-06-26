package xyz.xyz0z0.userinfo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import xyz.xyz0z0.arouter_annotations.ARouter;
import xyz.xyz0z0.arouter_annotations.Parameter;

@ARouter(path = "/user/UserInfoActivity")
public class UserInfoActivity extends AppCompatActivity {


    @Parameter
    String name; // 序列号 String

    @Parameter
    String sex;

    @Parameter
    int age = 9;   // 序列号  int

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
    }
}