package com.razerdp.module.second.services;

import androidx.fragment.app.Fragment;

import com.razerdp.annotations.modules.ServiceImpl;
import com.razerdp.module.second.ui.ModuleSecondFragment;
import com.razerdp.router.modules.ModuleSecondService;

/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：
 */
@ServiceImpl
public class ModuleSecondServiceImpl implements ModuleSecondService {
    @Override
    public Class<? extends Fragment> getFragmentClass() {
        return ModuleSecondFragment.class;
    }
}
