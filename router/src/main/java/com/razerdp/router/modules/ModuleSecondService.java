package com.razerdp.router.modules;


import androidx.fragment.app.Fragment;

/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：组件暴露的接口
 */
public interface ModuleSecondService {

    Class<? extends Fragment> getFragmentClass();
}
