package com.forfan.bigbang.component.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.forfan.bigbang.R;
import com.forfan.bigbang.baseCard.AbsCard;
import com.forfan.bigbang.baseCard.CardListAdapter;
import com.forfan.bigbang.baseCard.DividerItemDecoration;
import com.forfan.bigbang.component.activity.BigBangActivity;
import com.forfan.bigbang.component.activity.HomeActivity;
import com.forfan.bigbang.component.activity.SplashActivity;
import com.forfan.bigbang.component.base.BaseActivity;
import com.forfan.bigbang.component.contentProvider.SPHelper;
import com.forfan.bigbang.component.service.BigBangMonitorService;
import com.forfan.bigbang.util.ConstantUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.forfan.bigbang.util.ConstantUtil.BROADCAST_RELOAD_SETTING;


public class SettingActivity extends BaseActivity {

    private static final String TAG="SettingActivity";


    protected RecyclerView cardList;
    protected List<AbsCard> cardViews=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        cardList = (RecyclerView) findViewById(R.id.card_list);

        cardList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));


        cardViews.add(new FunctionSettingCard(this));
        cardViews.add(new MonitorSettingCard(this));
        cardViews.add(new FeedBackAndUpdateCard(this));


        CardListAdapter newAdapter = new CardListAdapter(this, false);
        newAdapter.setCardViews(cardViews);
        cardList.setItemAnimator(new FadeInAnimator());
        cardList.setAdapter(newAdapter);

        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Long,Observable<String>>(){
                    @Override
                    public Observable<String> call(Long aLong){
                        return Observable.just("");
                    }
                })
                .subscribe(s -> {
                    if (s.equals("")){
                        boolean hasShared=SPHelper.getBoolean(ConstantUtil.HAD_SHARED,false);
                        if (!hasShared){
                            newAdapter.addView(new ShareCard(this),0);
                        }
                    }
                });



    }

    @Override
    protected void onPause() {
        super.onPause();
        sendBroadcast(new Intent(BROADCAST_RELOAD_SETTING));
    }
}
