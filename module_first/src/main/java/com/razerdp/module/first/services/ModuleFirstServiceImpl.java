package com.razerdp.module.first.services;

import androidx.fragment.app.Fragment;

import com.razerdp.annotations.modules.ServiceImpl;
import com.razerdp.module.first.ui.ModuleFirstFragment;
import com.razerdp.router.modules.ModuleFirstService;

/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：
 */
@ServiceImpl
public class ModuleFirstServiceImpl implements ModuleFirstService {
    @Override
    public Class<? extends Fragment> getFragmentClass() {
        return ModuleFirstFragment.class;
    }
}
