package com.razerdp.module;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.razerdp.module.second.ui.ModuleSecondFragment;
import com.razerdp.module_second_private.R;


/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new ModuleSecondFragment(), "ModuleSecondFragment")
                .commitAllowingStateLoss();
    }
}
