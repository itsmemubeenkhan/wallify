package com.retrytech.ledgeapp.utils.ads;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Keep;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.retrytech.ledgeapp.utils.SessionManager;


@Keep
public class BannerAds {
    SessionManager sessionManager;
    private Context context;
    private FrameLayout adsContainer;

    public BannerAds(Context context, FrameLayout adsContainer) {
        this.context = context;
        this.adsContainer = adsContainer;
        if (context != null) {
            sessionManager = new SessionManager(context);
            if (!sessionManager.getPremium()) {
                initAds();
            }
        }
    }

    private void initAds() {
        if (sessionManager.getAdmob() == null || sessionManager.getAdmob().getBannerId() == null) {
            return;
        }
        AdView adView = new AdView(context);
        adView.setAdUnitId(sessionManager.getAdmob().getBannerId());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d("TAG", "onAdFailedToLoad: " + loadAdError.toString());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (adsContainer != null) {
                    adsContainer.removeAllViews();
                    adsContainer.addView(adView);
                }
                Log.d("TAG", "onAdLoaded: ");
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }

}
