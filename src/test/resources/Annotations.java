package com.android.launcher3.folder;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

public class Annotations {

    public static ArrayList<Object> toArrayList(@NotNull Object[] array) {
        ArrayList<Object> list = new ArrayList<>();
        for (Object o : array) { list.add(o); }
        return list;
    }

    public void animateOpen() {

        // Footer animation
        if (mContent.getPageCount() > 1 && !mInfo.hasOption(FolderInfo.FLAG_MULTI_PAGE_ANIMATION)) {

            // Do not update the flag if we are in drag mode. The flag will be updated, when we
            // actually drop the icon.
            final boolean updateAnimationFlag = !mDragInProgress;
            anim.addListener(new AnimatorListenerAdapter() {

                @SuppressLint("InlinedApi")
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFolderName.animate().setDuration(FOLDER_NAME_ANIMATION_DURATION)
                            .translationX(0)
                            .setInterpolator(AnimationUtils.loadInterpolator(
                                    mLauncher, android.R.interpolator.fast_out_slow_in));
                    mPageIndicator.playEntryAnimation();

                    if (updateAnimationFlag) {
                        mInfo.setOption(FolderInfo.FLAG_MULTI_PAGE_ANIMATION, true,
                                mLauncher.getModelWriter());
                    }
                }
            });
        } else {
            mFolderName.setTranslationX(0);
        }
    }

    @SuppressLint("InflateParams")
    static Folder fromXml(Launcher launcher) {
        return (Folder) launcher.getLayoutInflater()
                .inflate(R.layout.user_folder_icon_normalized, null);
    }

    @Override
    protected boolean isOfType(int type) {
        return (type & TYPE_FOLDER) != 0;
    }
}
