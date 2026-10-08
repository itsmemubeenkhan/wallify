package com.retrytech.ledgeapp.utils.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.retrytech.ledgeapp.utils.SessionManager;


public class RewardAds {

    private static final String TAG = "rewardads";
    RewardAdListnear rewardAdListnear;
    SessionManager sessionManager;
    private RewardedAd rewardedAd;
    private Context context;

    public RewardAds(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
        if (!sessionManager.getPremium()) {
            initGoogle();
        }
    }

    public RewardAdListnear getRewardAdListnear() {
        return rewardAdListnear;
    }

    public void setRewardAdListnear(RewardAdListnear rewardAdListnear) {
        this.rewardAdListnear = rewardAdListnear;
    }

    public void initGoogle() {
        if (sessionManager.getAdmob() == null || sessionManager.getAdmob().getRewardedId() == null) {
            return;
        }
        RewardedAd.load(context, sessionManager.getAdmob().getRewardedId(), new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                Log.i(TAG, "onAdLoaded reward again: ");
                RewardAds.this.rewardedAd = rewardedAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                Log.d(TAG, "onRewardedAdFailedToLoad: " + loadAdError);
            }
        });

    }


    public void showAd() {
        if (rewardedAd != null) {
            Activity activityContext = (Activity) context;

            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    rewardAdListnear.onAdClosed();

                }
            });
            rewardedAd.show(activityContext, rewardItem -> rewardAdListnear.onEarned());

        } else {
            rewardAdListnear.onFailed();
        }


    }


    public interface RewardAdListnear {
        void onAdClosed();

        void onEarned();

        void onFailed();
    }
}
