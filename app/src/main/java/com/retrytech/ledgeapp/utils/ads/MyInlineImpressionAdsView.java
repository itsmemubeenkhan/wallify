package com.retrytech.ledgeapp.utils.ads;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.retrytech.ledgeapp.R;
import com.retrytech.ledgeapp.model.SettingData;
import com.retrytech.ledgeapp.utils.SessionManager;

public class MyInlineImpressionAdsView extends FrameLayout {
    private static final String TAG = "InlineImpressionAd";
    private static final String TEST_BANNER_AD_UNIT = "ca-app-pub-3940256099942544/6300978111";
    private boolean initialized = false;
    private boolean triedFallbackTestUnit = false;
    private AdView adView;
    private String primaryAdUnitId = "";

    public MyInlineImpressionAdsView(@NonNull Context context) {
        super(context);
        initIfNeeded();
    }

    public MyInlineImpressionAdsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initIfNeeded();
    }

    public MyInlineImpressionAdsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initIfNeeded();
    }

    public MyInlineImpressionAdsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initIfNeeded();
    }

    private void initIfNeeded() {
        if (initialized) return;
        initialized = true;

        SessionManager sessionManager = new SessionManager(getContext());
        if (sessionManager.getPremium()) return;

        String adUnitId = null;
        SettingData.AdmobItem admob = sessionManager.getAdmob();
        if (admob != null) {
            adUnitId = admob.getBannerId();
        }
        primaryAdUnitId = TextUtils.isEmpty(adUnitId) ? TEST_BANNER_AD_UNIT : adUnitId;

        adView = new AdView(getContext());
        adView.setAdUnitId(primaryAdUnitId);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        adView.setLayoutParams(params);
        addView(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG, "onAdLoaded");
                showSponsoredText(true);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d(TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                if (!triedFallbackTestUnit && !TextUtils.equals(adView.getAdUnitId(), TEST_BANNER_AD_UNIT)) {
                    triedFallbackTestUnit = true;
                    adView.setAdUnitId(TEST_BANNER_AD_UNIT);
                    adView.loadAd(new AdRequest.Builder().build());
                    return;
                }

                showSponsoredText(false);
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void showSponsoredText(boolean visible) {
        try {
            if (getParent() instanceof android.view.View) {
                android.view.View parentView = (android.view.View) getParent();
                android.view.View sponsoredText = parentView.findViewById(R.id.tv_sponsored);
                if (sponsoredText != null) {
                    sponsoredText.setVisibility(visible ? VISIBLE : GONE);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
