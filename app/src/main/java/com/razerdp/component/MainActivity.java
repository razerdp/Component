package com.razerdp.component;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.StringDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.razerdp.common.manager.TabManager;
import com.razerdp.router.core.ModuleManager;
import com.razerdp.router.modules.ModuleFirstService;
import com.razerdp.router.modules.ModuleSecondService;
import com.razerdp.router.modules.ModuleThirdService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MainActivity extends AppCompatActivity {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TAB_FIRST, TAB_SECOND, TAB_THIRD})
    public @interface TabIndex {
    }

    public static final String TAB_FIRST = "TAB_FIRST";
    public static final String TAB_SECOND = "TAB_SECOND";
    public static final String TAB_THIRD = "TAB_THIRD";

    private TabManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        manager = TabManager.create(this, getSupportFragmentManager(), R.id.container).setFragmentManagerProvider(new TabManager.FragmentManagerProvider() {
            @Override
            public FragmentManager onGetFragmentManager() {
                return getSupportFragmentManager();
            }
        });

        TextView tvHomeFirst = findViewById(R.id.tv_home_first);
        TextView tvHomeSecond = findViewById(R.id.tv_home_second);
        TextView tvHomeThird = findViewById(R.id.tv_home_third);

        manager.append(TAB_FIRST, ModuleManager.INSTANCE.getService(ModuleFirstService.class).getFragmentClass(), null, tvHomeFirst, null)
                .append(TAB_SECOND, ModuleManager.INSTANCE.getService(ModuleSecondService.class).getFragmentClass(), null, tvHomeSecond, null)
                .append(TAB_THIRD, ModuleManager.INSTANCE.getService(ModuleThirdService.class).getFragmentClass(), null, tvHomeThird, null)
                .enableTabAnimation();
        manager.switchTab(TAB_FIRST);

    }
}
