package com.razerdp.module.third.services;

import androidx.fragment.app.Fragment;

import com.razerdp.annotations.modules.ServiceImpl;
import com.razerdp.module.third.ui.ModuleThirdFragment;
import com.razerdp.router.modules.ModuleThirdService;

/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：
 */
@ServiceImpl
public class ModuleThirdServiceImpl implements ModuleThirdService {
    @Override
    public Class<? extends Fragment> getFragmentClass() {
        return ModuleThirdFragment.class;
    }
}
