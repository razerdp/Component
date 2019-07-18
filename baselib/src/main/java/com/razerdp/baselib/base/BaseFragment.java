package com.razerdp.baselib.base;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by 大灯泡 on 2019/4/9
 * <p>
 * Description：
 */
public abstract class BaseFragment extends Fragment {
    protected final String TAG = getClass().getSimpleName();

    private Context mContext;
    protected View mRootView;
    protected final State mState = new State();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        onHandledArguments(getArguments());
    }

    private void onHandledArguments(@Nullable Bundle arguments) {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mContext == null) {
            mContext = requireContext();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.d("openFragment", "当前打开fragment： " + this.getClass().getSimpleName());
            mRootView = inflater.inflate(contentViewLayoutId(), container, false);
            onInitViews(mRootView);
            onAfterInitViews();
        }
        mState.handleShow();
        mState.state |= State.FLAG_INIT;
        return mRootView;

    }


    //region abstract
    @LayoutRes
    public abstract int contentViewLayoutId();

    protected abstract void onInitViews(View mRootView);


    //endregion
    protected void onAfterInitViews() {

    }


    protected void onBackPressed() {
        try {
            if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                getActivity().finish();
            }
        } catch (Exception e) {

        }
    }

    protected void finishActivity() {
        try {
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public Context getContext() {
        if (mContext != null) {
            return mContext;
        }
        return super.getContext();
    }

    //region life


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //for viewpager，因为创建的时候就回调这货，可能ui还没开始创建，需要判断
        if (mState.hasFlag(State.FLAG_INIT)) {
            if (isVisibleToUser) {
                mState.handleShow();
            } else {
                mState.handleHided();
            }
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //for show / hide
        if (mState.hasFlag(State.FLAG_INIT)) {
            if (hidden) {
                mState.handleHided();
            } else {
                mState.handleShow();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //当activity回调resume的时候，其所有fragment都会回调，因此需要判断具体的
        //getUserVisibleHint不在vp的时候默认就是true
        if (getUserVisibleHint() && !isHidden()) {
            mState.handleShow();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (getUserVisibleHint()) {
            mState.handleHided();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRootView = null;
    }

    //endregion

    //region callback

    public void onShow() {
    }

    public void onHided() {
    }

    //endregion


    private class State {
        private long lastShowingTime = 0;
        static final int FLAG_IDLE = 0x00000000;
        static final int FLAG_INIT = 0x00000001;

        static final int FLAG_SHOWING = 0x00010000;
        static final int FLAG_HIDED = FLAG_SHOWING << 1;

        int state = FLAG_IDLE;


        void handleShow() {
            if ((state & FLAG_SHOWING) != 0) return;
            state |= FLAG_SHOWING;
            onShow();
            state &= ~FLAG_HIDED;
        }


        void handleHided() {
            if ((state & FLAG_HIDED) != 0) return;
            state |= FLAG_HIDED;
            onHided();
            lastShowingTime = SystemClock.uptimeMillis();
            state &= ~FLAG_SHOWING;
        }

        void computeFlag(int mFlag) {
            state |= mFlag;
        }

        void removeFlag(int mFlag) {
            state &= ~mFlag;
        }

        void resetFlag() {
            state = FLAG_IDLE;
        }

        boolean hasFlag(int mFlag) {
            return (state & mFlag) != 0;
        }
    }
}
