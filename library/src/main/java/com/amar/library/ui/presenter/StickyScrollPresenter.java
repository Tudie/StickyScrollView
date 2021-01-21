package com.amar.library.ui.presenter;

import androidx.annotation.StyleableRes;

import com.amar.library.provider.interfaces.IResourceProvider;
import com.amar.library.ui.presentation.IStickyScrollPresentation;
import com.amar.library.provider.interfaces.IScreenInfoProvider;

/**
 * Created by Amar Jain on 17/03/17.
 */

public class StickyScrollPresenter {

    private final IResourceProvider mTypedArrayResourceProvider;
    private IStickyScrollPresentation mStickyScrollPresentation;

    private int mDeviceHeight;

    private int mStickyFooterHeight;
    private int mStickyFooterInitialTranslation;
    private int mStickyFooterInitialLocation;

    public int mStickyheigth=0;//悬停距离顶部高度
    private int mStickyHeaderInitialLocation;
    private boolean mIsFooterSticky;
    private boolean mIsHeaderSticky;
    public boolean mScrolled;

    public StickyScrollPresenter(IStickyScrollPresentation stickyScrollPresentation, IScreenInfoProvider screenInfoProvider, IResourceProvider typedArrayResourceProvider) {
        mDeviceHeight = screenInfoProvider.getScreenHeight();
        mTypedArrayResourceProvider = typedArrayResourceProvider;
        mStickyScrollPresentation = stickyScrollPresentation;
    }

    public void onGlobalLayoutChange(@StyleableRes int headerRes, @StyleableRes int footerRes) {
        int headerId = mTypedArrayResourceProvider.getResourceId(headerRes);
        if (headerId != 0) {
            mStickyScrollPresentation.initHeaderView(headerId);
        }
        int footerId = mTypedArrayResourceProvider.getResourceId(footerRes);
        if (footerId != 0) {
            mStickyScrollPresentation.initFooterView(footerId);
        }
        mTypedArrayResourceProvider.recycle();
    }

    public void initStickyFooter(int measuredHeight, int initialStickyFooterLocation) {
        mStickyFooterHeight = measuredHeight;
        mStickyFooterInitialLocation = initialStickyFooterLocation;
        mStickyFooterInitialTranslation = mDeviceHeight - initialStickyFooterLocation - mStickyFooterHeight;
        if (mStickyFooterInitialLocation > mDeviceHeight - mStickyFooterHeight) {
            mStickyScrollPresentation.stickFooter(mStickyFooterInitialTranslation);
            mIsFooterSticky = true;
        }
    }

    public void initStickyHeader(int headerTop) {
        mStickyHeaderInitialLocation = headerTop;
    }

    public void onScroll(int scrollY) {
        mScrolled = true;
        handleFooterStickiness(scrollY);
        handleHeaderStickiness(scrollY);
    }

    private void handleFooterStickiness(int scrollY) {
        if (scrollY > mStickyFooterInitialLocation - mDeviceHeight + mStickyFooterHeight) {
            mStickyScrollPresentation.freeFooter();
            mIsFooterSticky = false;
        } else {
            mStickyScrollPresentation.stickFooter(mStickyFooterInitialTranslation + scrollY);
            mIsFooterSticky = true;
        }
    }

    private void handleHeaderStickiness(int scrollY) {
        if (scrollY > mStickyHeaderInitialLocation-mStickyheigth) {
            mStickyScrollPresentation.stickHeader(scrollY - mStickyHeaderInitialLocation+mStickyheigth);
            mIsHeaderSticky = true;
        } else {
            mStickyScrollPresentation.freeHeader();
            mIsHeaderSticky = false;
        }
    }

    public void setmStickyheigth(int mStickyheigth) {
        this.mStickyheigth = mStickyheigth;
    }

    public boolean isFooterSticky() {
        return mIsFooterSticky;
    }

    public boolean isHeaderSticky() {
        return mIsHeaderSticky;
    }

    public void recomputeFooterLocation(int footerTop) {
        if (mScrolled) {
            mStickyFooterInitialTranslation = mDeviceHeight - footerTop - mStickyFooterHeight;
            mStickyFooterInitialLocation = footerTop;
        } else {
            initStickyFooter(mStickyFooterHeight, footerTop);
        }
        handleFooterStickiness(mStickyScrollPresentation.getCurrentScrollYPos());
    }

    public void recomputeHeaderLocation(int headerTop) {
        initStickyHeader(headerTop);
        handleHeaderStickiness(mStickyScrollPresentation.getCurrentScrollYPos());
    }
}
