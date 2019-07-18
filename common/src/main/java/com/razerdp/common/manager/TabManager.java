package com.razerdp.common.manager;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.razerdp.baselib.interfaces.SimpleReturnCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 大灯泡 on 2019/4/9
 * <p>
 * Description：多Fragment导航切换管理类
 */
public class TabManager {
    private final Context mContext;
    private FragmentManager mManager;
    private FragmentManagerProvider managerProvider;
    private final int mContainerId;
    private List<TabInfo> mTabInfos;
    private TabInfo mCurTabInfo;
    private TabInfo mLastTabInfo;
    private boolean tabAnimateEnable = false;
    private static final long ANIMATION_DURATION = 350;
    private static final String SAVE_TAG = "TabManager_CurTab";


    private TabManager(Context context, FragmentManager manager, int containerId) {
        this.mContext = context;
        this.mManager = manager;
        this.mContainerId = containerId;
        mTabInfos = new ArrayList<>();
    }


    public static TabManager create(Context context, FragmentManager mManager, int mContainerId) {
        return new TabManager(context, mManager, mContainerId);
    }

    public TabManager setFragmentManagerProvider(FragmentManagerProvider managerProvider) {
        this.managerProvider = managerProvider;
        return this;
    }

    private FragmentManager getManager() {
        if (mManager == null || mManager.isDestroyed()) {
            if (managerProvider != null) {
                mManager = managerProvider.onGetFragmentManager();
            }
        }
        return mManager;
    }

    public TabManager append(@NonNull String tag,
                             @NonNull Class<? extends Fragment> clas,
                             @Nullable Bundle arguments,
                             @Nullable View mTabView,
                             @Nullable SimpleReturnCallback<String> mClickCallback) {
        if (clas == null) return this;
        if (TextUtils.isEmpty(tag)) {
            tag = clas.getSimpleName();
        }
        TabInfo tabInfo = new TabInfo(tag, clas);
        tabInfo.mArguments = arguments;
        tabInfo.mTabView = mTabView;
        tabInfo.mClickCallback = mClickCallback;
        addToList(tabInfo);
        return this;
    }

    public TabManager append(@NonNull String tag,
                             @NonNull Fragment fragment,
                             @Nullable View mTabView,
                             @Nullable SimpleReturnCallback<String> mClickCallback) {
        if (fragment == null) return this;
        if (TextUtils.isEmpty(tag)) {
            tag = fragment.getClass().getSimpleName();
        }
        TabInfo tabInfo = new TabInfo(tag, fragment.getClass());
        tabInfo.mFragment = fragment;
        tabInfo.mTabView = mTabView;
        tabInfo.mClickCallback = mClickCallback;
        addToList(tabInfo);
        return this;
    }


    private void addToList(final TabInfo info) {
        if (info == null) return;
        int index = -1;
        int cursor = 0;
        for (TabInfo tabInfo : mTabInfos) {
            if (TextUtils.equals(tabInfo.tag, info.tag)) {
                index = cursor;
                break;
            }
            cursor++;
        }
        if (index != -1) {
            mTabInfos.set(index, info);
        } else {
            mTabInfos.add(info);
        }
        if (info.mTabView != null) {
            info.mTabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info.mClickCallback != null && info.mClickCallback.onCall(info.tag)) {
                        return;
                    }
                    switchTab(info);
                }
            });
        }
    }

    public TabManager enableTabAnimation() {
        this.tabAnimateEnable = true;
        return this;
    }

    public TabManager disableTabAnimation() {
        this.tabAnimateEnable = false;
        return this;
    }

    public void switchTab(String tag) {
        for (TabInfo mTabInfo : mTabInfos) {
            if (TextUtils.equals(mTabInfo.tag, tag)) {
                switchTab(mTabInfo);
                break;
            }
        }
    }

    private void switchTab(TabInfo info) {
        if (mCurTabInfo == null) {
            mCurTabInfo = info;
        } else {
            if (mCurTabInfo.compareTo(info)) {
                return;
            }
            mLastTabInfo = mCurTabInfo;
            mCurTabInfo = info;
        }

        FragmentTransaction ft = onTabChange(info);
        if (ft != null) {
            ft.commitAllowingStateLoss();
        }

    }

    private FragmentTransaction onTabChange(TabInfo info) {
        if (info == null) return null;

        FragmentTransaction ft = getManager().beginTransaction();

        Fragment target = info.getFragment();
        Fragment last = mLastTabInfo != null ? mLastTabInfo.getFragment() : null;
        if (target == null) {
            Log.e("", "Can not find tab for " + info.tag);
            return null;
        }

        if (target.isAdded()) {
            ft.show(target);
        } else {
            ft.add(mContainerId, target, info.tag);
        }
        if (last != null) {
            if (last.isAdded()) {
                ft.hide(last);
            }
        }

        for (TabInfo mTabInfo : mTabInfos) {
            if (mTabInfo.compareTo(info)) {
                selecteTab(mTabInfo);
            } else {
                unselectTab(mTabInfo);
            }
        }
        return ft;
    }

    private void selecteTab(TabInfo mTabInfo) {
        if (mTabInfo == null) return;
        if (mTabInfo.mTabView != null) {
            mTabInfo.mTabView.setSelected(true);
            if (tabAnimateEnable) {
                mTabInfo.mTabView.animate()
                        .setDuration(ANIMATION_DURATION)
                        .setInterpolator(new OvershootInterpolator(6))
                        .scaleX(1)
                        .scaleY(1).start();
            }
        }
    }

    private void unselectTab(TabInfo mTabInfo) {
        if (mTabInfo == null) return;
        if (mTabInfo.mTabView != null) {
            mTabInfo.mTabView.setSelected(false);
            if (tabAnimateEnable) {
                mTabInfo.mTabView.animate()
                        .setDuration(ANIMATION_DURATION)
                        .setInterpolator(new OvershootInterpolator(6))
                        .scaleX(.9f)
                        .scaleY(.9f).start();
            }
        }
    }


    public void destroy() {
        for (TabInfo mTabInfo : mTabInfos) {
            mTabInfo.destroy();
        }
        mTabInfos.clear();
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null || getManager() == null || getManager().isDestroyed())
            return;

        String tag = savedInstanceState.getString(SAVE_TAG, null);
        if (TextUtils.isEmpty(tag)) {
            tag = mTabInfos.get(0).tag;
        }

        TabInfo tabInfo = null;
        List<TabInfo> hidedTabs = new ArrayList<>();

        for (TabInfo mTabInfo : mTabInfos) {
            String tabTag = mTabInfo.tag;
            Fragment fragment = getManager().findFragmentByTag(tabTag);
            if (fragment != null) {
                mTabInfo.mFragment = fragment;
                if (fragment.isHidden() || !fragment.isAdded()) {
                    if (TextUtils.equals(tag, tabTag)) {
                        tabInfo = mTabInfo;
                    } else {
                        hidedTabs.add(mTabInfo);
                    }
                }
            }
        }
        FragmentTransaction ft = onTabChange(tabInfo);
        if (ft != null) {
            for (TabInfo hidedTab : hidedTabs) {
                if (hidedTab.mFragment != null) {
                    ft.hide(hidedTab.mFragment);
                }
            }
            ft.commitAllowingStateLoss();
        }


    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) return;
        if (mCurTabInfo != null) {
            outState.putString(SAVE_TAG, mCurTabInfo.tag);
        }
    }

    final class TabInfo {
        private final String tag;
        private final Class<? extends Fragment> clas;
        private Bundle mArguments;
        private Fragment mFragment;
        private View mTabView;
        private SimpleReturnCallback<String> mClickCallback;

        public TabInfo(String tag, Class<? extends Fragment> clss) {
            this.tag = tag;
            this.clas = clss;
        }

        boolean compareTo(TabInfo mOther) {
            if (mOther == null) return false;
            if (this == mOther) return true;
            return TextUtils.equals(tag, mOther.tag);
        }


        void destroy() {
            mArguments = null;
            mFragment = null;
            mTabView.setOnClickListener(null);
            mTabView = null;
            mClickCallback = null;
        }


        Fragment getFragment() {
            if (mFragment != null) return mFragment;
            if (clas == null) return null;
            mFragment = Fragment.instantiate(mContext, clas.getName(), mArguments);
            return mFragment;
        }


    }

    public interface FragmentManagerProvider {
        FragmentManager onGetFragmentManager();
    }

}
