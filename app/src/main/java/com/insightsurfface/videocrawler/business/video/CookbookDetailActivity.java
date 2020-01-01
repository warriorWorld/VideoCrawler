//package com.insightsurfface.videocrawler.business.video;
//
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.graphics.Bitmap;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.alibaba.android.arouter.facade.annotation.Route;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.assist.ImageScaleType;
//import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
//import com.sfbest.mapp.R;
//import com.sfbest.mapp.common.base.SfBaseActivity;
//import com.sfbest.mapp.common.bean.param.BatchCartProductParams;
//import com.sfbest.mapp.common.bean.param.DelCollectCookBookParam;
//import com.sfbest.mapp.common.bean.param.DeviceInfoParam;
//import com.sfbest.mapp.common.bean.param.GetCookBookDetailParam;
//import com.sfbest.mapp.common.bean.param.GetMaterialByIdsParam;
//import com.sfbest.mapp.common.bean.result.CartProduct;
//import com.sfbest.mapp.common.bean.result.CartProductResult;
//import com.sfbest.mapp.common.bean.result.DelCollectCookBookResult;
//import com.sfbest.mapp.common.bean.result.GetCookBookDetailResult;
//import com.sfbest.mapp.common.bean.result.GetMaterialsByidsResult;
//import com.sfbest.mapp.common.bean.result.bean.CommonResult;
//import com.sfbest.mapp.common.bean.result.bean.CookBook;
//import com.sfbest.mapp.common.bean.result.bean.CookBookMaterial;
//import com.sfbest.mapp.common.bean.result.bean.CookBookStep;
//import com.sfbest.mapp.common.clientproxy.Env;
//import com.sfbest.mapp.common.clientproxy.HttpService;
//import com.sfbest.mapp.common.http.BaseSubscriber;
//import com.sfbest.mapp.common.manager.AddressManager;
//import com.sfbest.mapp.common.manager.SfActivityManager;
//import com.sfbest.mapp.common.manager.ShopCartManager;
//import com.sfbest.mapp.common.manager.UserManager;
//import com.sfbest.mapp.common.share.ShareController;
//import com.sfbest.mapp.common.share.ShareDialog;
//import com.sfbest.mapp.common.ui.login.PhoneValidateLogonActivity;
//import com.sfbest.mapp.common.util.AddToCartUtil;
//import com.sfbest.mapp.common.util.GsonUtil;
//import com.sfbest.mapp.common.util.LogUtil;
//import com.sfbest.mapp.common.util.ProductListUtil;
//import com.sfbest.mapp.common.util.RetrofitExceptionAdapter;
//import com.sfbest.mapp.common.util.RetrofitUtil;
//import com.sfbest.mapp.common.util.ScreenUtils;
//import com.sfbest.mapp.common.util.SearchUtil;
//import com.sfbest.mapp.common.util.SfBestEvent;
//import com.sfbest.mapp.common.util.TimeUtil;
//import com.sfbest.mapp.common.view.InformationViewLayout;
//import com.sfbest.mapp.common.view.SfNavigationBar;
//import com.sfbest.mapp.common.view.SfToast;
//import com.sfbest.mapp.common.view.dialog.SfTipsDialog;
//import com.sfbest.mapp.module.homepage.MainActivity;
//import com.sfbest.mapp.module.productlist.ProductListActivity;
//import com.sfbest.mapp.module.shoppingcart.ShopCartActivity;
//import com.umeng.analytics.MobclickAgent;
//import com.zhy.view.flowlayout.FlowLayout;
//import com.zhy.view.flowlayout.TagAdapter;
//import com.zhy.view.flowlayout.TagFlowLayout;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.List;
//
//import de.greenrobot.event.EventBus;
//import rx.Observable;
//import rx.Observer;
//import rx.Subscription;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//import rx.subscriptions.CompositeSubscription;
//
///**
// * 菜谱详情页
// * Created by Acorn on 2016/6/27.
// */
//@Route(path = "/App/CookbookDetailActivity")
//public class CookbookDetailActivity extends SfBaseActivity implements View.OnClickListener, SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
//        MediaPlayer.OnVideoSizeChangedListener, SeekBar.OnSeekBarChangeListener {
//    private int cookBookId;
//    private ImageView ivActionBar;
//    private TextView tvActionBar;
//    private ImageView ivShopActionBar;
//    private TextView tvShopActionBar;
//    private ImageView ivMenuActionBar;
//    private InformationViewLayout informationView;
//    private CookBook cookBook;
//    private ImageView titleIv;
//    private TextView titleTv;
//    private TextView contentTv;
//    private TextView tagTv;
//    //食材列表
//    private TagFlowLayout mFlowLayout;
//    private RecyclerView cookStepRecycler;
//    private DetailCookStepAdapter cookStepAdapter;
//    private RecyclerView foodMaterialShopRecycler;
//    //一键添加
//    private Button addShopCarBtn;
//    //收藏
//    private ImageView collectIv;
//    private boolean isCollected;
//    //分享
//    private ImageView shareIv;
//
//    private TextView tvFoodMaterialNum;
//
//    //播放器
//    private View playerLayout;
//    private SurfaceView playerSfv;
//    private MediaPlayer mediaPlayer;
//    private View bottomBarLayout;
//    private ImageView bottomPlayIv, zoomIv;
//    private ImageView centerPlayIv;
//    private TextView curPlayTimeTv, totalPlayTimeTv;
//    private SeekBar playSeekBar;
//    private ImageView playerBackIv;
//    private boolean isFirstPlay = true;
//    private SurfaceHolder playerHolder;
//    private boolean isFullScreening = false;
//    private PlayerHandler playerHandler;
//    private ProgressBar progressBar;
//    //播放器UI更新间隔
//    private static int PLAYER_UPDATE_DELAY = 500;
//    //播放器UI隐藏间隔
//    private static int PLAYER_UI_HIDE_DELAY = 3000;
//    private boolean isPlayerPrepared = false;
//    private int shopCarNum = ShopCartManager.showCartTotalNum;
//    private ImageView fakePlayIv;
//
//
//    private int titleIvWidth = 0;
//    private boolean isAlreadyComputeHeight = false;
//    private int titleBitmapWidth = 0, titleBitmapHeight = 0;
//    //相关食材
//    private DetailMaterialGridAdapter adapter;
//
//    private boolean isPush = false;
//
//    private float surfaceWidth, surfaceLandWidth;
//    private float surfaceHeight, surfaceLandHeight;
//    private int videoWidth, videoHeight;
//
//    @Override
//    protected void initView() {
//        setCustomActionBar(R.layout.header_cook_details);
//        ivActionBar = flActionBar.findViewById(R.id.iv_back);
//        ivActionBar.setOnClickListener(this);
//        tvActionBar = flActionBar.findViewById(R.id.tv_actionbar_title);
//        tvActionBar.setText("菜谱详情");
//        ivShopActionBar = flActionBar.findViewById(R.id.bottombar_shoppingcar_iv);
//        ivShopActionBar.setOnClickListener(this);
//        tvShopActionBar = flActionBar.findViewById(R.id.bottombar_shoppingcar_full_tv);
//        ivMenuActionBar = flActionBar.findViewById(R.id.iv_menu);
//        ivMenuActionBar.setOnClickListener(this);
//        informationView = findViewById(R.id.cookbook_detail_ivl);
//        titleIv = (ImageView) findViewById(R.id.cookbook_detail_iv);
//        titleTv = (TextView) findViewById(R.id.cookbook_detail_title_tv);
//        contentTv = (TextView) findViewById(R.id.cookbook_detail_content_tv);
//        tagTv = (TextView) findViewById(R.id.cookbook_detail_tag_tv);
//        mFlowLayout = (TagFlowLayout) findViewById(R.id.food_material_tag);
//        cookStepRecycler = (RecyclerView) findViewById(R.id.cook_step_recycler);
//        cookStepRecycler.setFocusableInTouchMode(false);
//        cookStepRecycler.setFocusable(false);
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        cookStepRecycler.setLayoutManager(manager);
//        cookStepRecycler.setNestedScrollingEnabled(false);
//        foodMaterialShopRecycler = (RecyclerView) findViewById(R.id.food_material_shop_recycler);
//        foodMaterialShopRecycler.setFocusableInTouchMode(false);
//        foodMaterialShopRecycler.setFocusable(false);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        foodMaterialShopRecycler.setLayoutManager(layoutManager);
//        foodMaterialShopRecycler.setNestedScrollingEnabled(false);
//        addShopCarBtn = (Button) findViewById(R.id.cookbook_detail_add_shopcart);
//        collectIv = (ImageView) findViewById(R.id.collect_iv);
//        shareIv = (ImageView) findViewById(R.id.bottom_bar_share_iv);
//        tvFoodMaterialNum = (TextView) flContent.findViewById(R.id.tv_food_material_num);
//        //播放器
//        playerLayout = findViewById(R.id.player_layout);
//        playerSfv = (SurfaceView) findViewById(R.id.player_sfv);
//        bottomPlayIv = (ImageView) findViewById(R.id.player_bottom_play_iv);
//        zoomIv = (ImageView) findViewById(R.id.player_bottom_zoom_iv);
//        curPlayTimeTv = (TextView) findViewById(R.id.cur_play_time_tv);
//        totalPlayTimeTv = (TextView) findViewById(R.id.total_play_time_tv);
//        playSeekBar = (SeekBar) findViewById(R.id.play_progress_sb);
//        centerPlayIv = (ImageView) findViewById(R.id.player_center_play_iv);
//        bottomBarLayout = findViewById(R.id.operation_layout);
//        playerBackIv = (ImageView) findViewById(R.id.player_back_zoom_iv);
//        fakePlayIv = (ImageView) findViewById(R.id.fake_play_iv);
//        progressBar = (ProgressBar) findViewById(R.id.fake_play_pb);
//        bottomPlayIv.setEnabled(false);
//        zoomIv.setEnabled(false);
//        centerPlayIv.setEnabled(false);
//
//        addShopCarBtn.setOnClickListener(this);
//        collectIv.setOnClickListener(this);
//        shareIv.setOnClickListener(this);
//        playerBackIv.setOnClickListener(this);
//
//        bottomPlayIv.setOnClickListener(this);
//        zoomIv.setOnClickListener(this);
//        playSeekBar.setOnSeekBarChangeListener(this);
//        centerPlayIv.setOnClickListener(this);
//        playerSfv.setOnClickListener(this);
//        bottomBarLayout.setOnClickListener(this);
//        fakePlayIv.setOnClickListener(this);
//
//        informationView.setOnInformationClickListener(new InformationViewLayout.OnInformationClickListener() {
//            @Override
//            public void onInformationClick(InformationViewLayout.ResultType type) {
//                if (type == InformationViewLayout.ResultType.reload) {
//                    requestNetData();
//                    checkCollectStatus();
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void initData() {
//        requestNetData();
//        checkCollectStatus();
//    }
//
//    @Override
//    protected void getIntentData() {
//        Intent intent = getIntent();
//        if (null == intent)
//            return;
//        cookBookId = intent.getIntExtra("cookbookid", 0);
//    }
//
//    @Override
//    protected boolean showActionBar() {
//        return true;
//    }
//
//    @Override
//    protected String title() {
//        return null;
//    }
//
//    @Override
//    protected int loadLayout() {
//        return 0;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        isPush = getIntent().getBooleanExtra("push", false);
//        EventBus.getDefault().register(this);
//        setContentView(R.layout.activity_cookbook_detail);
//        surfaceWidth = surfaceLandHeight = ScreenUtils.getScreenWidth(this);
//        surfaceLandWidth = ScreenUtils.getScreenHeight(this);
//        surfaceHeight = getResources().getDimensionPixelOffset(R.dimen.sf750_390);
//    }
//
//    /**
//     * EventBus 在主线程中执行
//     *
//     * @param event
//     */
//    public void onEventMainThread(SfBestEvent event) {
//        if (null == event)
//            return;
//
//        if (event.getEventType() == SfBestEvent.EventType.ShopcarCountChange) { //购物车数量变化
//            if (shopCarNum != event.getIntMsg()) {
//                this.shopCarNum = event.getIntMsg();
//                if (tvShopActionBar != null) {
//                    if (shopCarNum > 0) {
//                        tvShopActionBar.setText(String.valueOf(shopCarNum));
//                        tvShopActionBar.setVisibility(View.VISIBLE);
//                    } else
//                        tvShopActionBar.setVisibility(View.INVISIBLE);
//                }
//            }
//        }
//    }
//
//
//    private void requestNetData() {
//        HttpService service = RetrofitUtil.getInstance().create(HttpService.class);
//        DeviceInfoParam deviceInfoParam = new DeviceInfoParam();
//        GetCookBookDetailParam param = new GetCookBookDetailParam(cookBookId);
//        //getCookBooks(null, null, pager,
//        Observable<GetCookBookDetailResult> observer = service.getCookBookDetail(GsonUtil.toJson(param), GsonUtil.toJson(deviceInfoParam));
//        // CompositeSubscription subscription = new CompositeSubscription();
//        subscription.add(observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<GetCookBookDetailResult>(mActivity, BaseSubscriber.MODEL_ALL) {
//
//            @Override
//            public void error(GetCookBookDetailResult result, Throwable e) {
//                super.error(result, e);
//            }
//
//            @Override
//            public void success(GetCookBookDetailResult result) {
//                super.success(result);
//                dataCallBack(result.data.getCookBook());
//            }
//
//        }));
//
//
//    }
//
//    private void requestMaterials(CookBook cookBook) {
//        if (null == cookBook || null == cookBook.getProductIds() || cookBook.getProductIds().isEmpty())
//            return;
//        HttpService service = RetrofitUtil.getInstance().create(HttpService.class);
//        DeviceInfoParam deviceInfoParam = new DeviceInfoParam();
//        deviceInfoParam.setAddress(AddressManager.getAddress());
//        List<String> list = new ArrayList<>(cookBook.getProductIds());
//        StringBuilder sb = new StringBuilder();
//        for (String str : list) {
//            sb.append(str + ",");
//        }
//        sb.deleteCharAt(sb.length() - 1);
//        String ids = sb.toString();
//        GetMaterialByIdsParam param = new GetMaterialByIdsParam(ids);
//        Observable<GetMaterialsByidsResult> observer = service.getMaterialsByids(GsonUtil.toJson(param), GsonUtil.toJson(deviceInfoParam));
//        subscription.add(observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<GetMaterialsByidsResult>(mActivity, BaseSubscriber.MODEL_TOAST) {
//
//
//            @Override
//            public void success(GetMaterialsByidsResult result) {
//                super.success(result);
//                adapter = new DetailMaterialGridAdapter(mActivity, null);
//                adapter.setData(result.data.getProductList());
//                tvFoodMaterialNum.setText("/共" + result.data.getProductList().size() + "件");
//                foodMaterialShopRecycler.setAdapter(adapter);
//            }
//        }));
//
//
//    }
//
//    private void dataCallBack(CookBook msg) {
//        this.cookBook = msg;
//        requestMaterials(cookBook);
//        ImageLoader.getInstance().displayImage(cookBook.getHeaderImgUrl(), titleIv, new DisplayImageOptions.Builder()
//                        .showImageOnLoading(R.drawable.sf_def)
//                        .showImageForEmptyUri(R.drawable.sf_def)
//                        .showImageOnFail(R.drawable.sf_def)
//                        .bitmapConfig(Bitmap.Config.RGB_565)
//                        // 防止内存溢出的
//                        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//                        .cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
//                        .displayer(new RoundedBitmapDisplayer(4))
//                        .build(),
//                new ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String s, View view) {
//                        // Do nothing
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String s, View view, FailReason failReason) {
//                        // Do nothing
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                        if (bitmap == null) {
//                            return;
//                        }
//                        titleBitmapWidth = bitmap.getWidth();
//                        titleBitmapHeight = bitmap.getHeight();
//                        if (titleIvWidth == 0) {
//                            return;
//                        }
//                        computeTitleIvHeight(bitmap.getWidth(), bitmap.getHeight());
////                        iv.setImageBitmap(bitmap);
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String s, View view) {
//                        // Do nothing
//                    }
//                });
//        setupPlayer();
//
//        titleTv.setText(cookBook.getCookBookTitle());
//        contentTv.setText(cookBook.getCookBookDesci());
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < cookBook.getCookBookTags().size() && i < 3; i++) { //最多显示3个标签
//            sb.append(cookBook.getCookBookTags().get(i));
//            if (i != cookBook.getCookBookTags().size() - 1 || i != 2)
//                sb.append("  ");
//        }
//        tagTv.setText(sb.toString());
//        if (shopCarNum > 0) {
//            tvShopActionBar.setText(String.valueOf(shopCarNum));
//            tvShopActionBar.setVisibility(View.VISIBLE);
//        } else
//            tvShopActionBar.setVisibility(View.INVISIBLE);
//        final List<CookBookMaterial> materials = cookBook.getCookMaterials();
//        if (materials != null && !materials.isEmpty()) {
//        }
//        mFlowLayout.setAdapter(new TagAdapter<CookBookMaterial>(materials) {
//            @Override
//            public View getView(FlowLayout parent, int position, final CookBookMaterial vo) {
//                TextView textView = new TextView(mActivity);
//                textView.setTextSize(15);
//                textView.setText(vo.getMaterialName() + "  (" + vo.getMaterialUsed() + ")");
//                textView.setTag(vo);
//                if (vo.getIsPrimary() == 1) {
//                    textView.setTextColor(0xff31C27C);
//                } else {
//                    textView.setTextColor(0xff333333);
//                }
//                return textView;
//            }
//        });
//        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
//            @Override
//            public boolean onTagClick(View view, int position, FlowLayout parent) {
//                CookBookMaterial vo = materials.get(position);
//                if (vo.getIsPrimary() == 1) {
//                    Intent intent = new Intent(mActivity, ProductListActivity.class);
//                    intent.putExtra(ProductListUtil.FROM_WHERE, ProductListUtil.FROM_SEARCH);
//                    intent.putExtra(SearchUtil.KEY_WORDS, vo.getMaterialName());
//                    intent.putExtra(ProductListUtil.SEARCH_DEFAULT, vo.getMaterialName());
//                    SfActivityManager.startActivity(mActivity, intent);
//                }
//                return true;
//            }
//        });
//        List<CookBookStep> steps = cookBook.getCookSteps();
//        cookStepAdapter = new DetailCookStepAdapter(this);
//        cookStepAdapter.setData(steps);
//        cookStepRecycler.setAdapter(cookStepAdapter);
//    }
//
//    private void computeTitleIvHeight(int bitmapWidth, int bitmapHeight) {
////        isAlreadyComputeHeight = true;
////        float adaptionRatio = (float) bitmapHeight / (float) bitmapWidth;
////        int adaptionHeight = (int) (titleIvWidth * adaptionRatio);
////        ViewGroup.LayoutParams lp = titleIv.getLayoutParams();
////        lp.height = adaptionHeight;
////        titleIv.setLayoutParams(lp);
//    }
//
//    private void recoverTitleIvHeight() {
//        ViewGroup.LayoutParams lp = titleIv.getLayoutParams();
//        lp.height = (int) getResources().getDimension(R.dimen.sf750_390);
//        titleIv.setLayoutParams(lp);
//    }
//
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            if (titleIvWidth == 0) {
//                titleIvWidth = titleIv.getWidth();
//                if (!isAlreadyComputeHeight && titleBitmapWidth != 0)
//                    computeTitleIvHeight(titleBitmapWidth, titleBitmapHeight);
//            }
//        }
//    }
//
//
//    @Override
//    public void onClick(View v) {
////        super.onClick(v);
//        switch (v.getId()) {
//            case R.id.iv_back:
//                onBackPressed();
//                break;
//            case R.id.iv_menu:
//                SfNavigationBar sfMenu = SfNavigationBar.getInstance();
//                sfMenu.showSfNavigation(this, ivMenuActionBar);
//                break;
//            case R.id.cookbook_detail_add_shopcart: //一键添加至购物车
//                MobclickAgent.onEvent(CookbookDetailActivity.this, "J2_003");
//                batchAddShopCar();
//                break;
//            case R.id.collect_iv: //收藏
//                MobclickAgent.onEvent(CookbookDetailActivity.this, "J2_005");
//                toggleCollect();
//                break;
//            case R.id.bottom_bar_share_iv: //分享
//                MobclickAgent.onEvent(CookbookDetailActivity.this, "J2_006");
//                share();
//                break;
//            case R.id.bottombar_shoppingcar_iv:  //购物车
//                MobclickAgent.onEvent(CookbookDetailActivity.this, "J2_007");
//                SfActivityManager.startActivity(CookbookDetailActivity.this, ShopCartActivity.class);
//                break;
//            case R.id.player_bottom_play_iv: //播放视频
//            case R.id.player_center_play_iv:
//            case R.id.fake_play_iv:
//                if (isFirstPlay)
//                    play();
//                else if (mediaPlayer.isPlaying())
//                    pause();
//                else
//                    start();
//                break;
//            case R.id.player_bottom_zoom_iv: //缩放视频
//            case R.id.player_back_zoom_iv:
//                toggleFullScreen();
//                break;
//            case R.id.player_sfv:  //视频主体
//                togglePlayerUI();
//                break;
//            case R.id.base_title_back_rl:
//                onBackPressed();
//                break;
//            default:
//                break;
//        }
//    }
//
//    private ShareDialog shareDialog;
//
//    private void share() {
//        /**
//         * 菜谱链接
//         */
//        String url = !Env.UAT ?
//                ShareController.SFBEST_WEB_URL + "/cookbook/getDetail?cookBookId=" + cookBook.getCookBookId() :
//                ShareController.TEST_SFBEST_WEB_URL + "/cookbook/getDetail?cookBookId=" + cookBook.getCookBookId();
//        //https://m.sfbest.com/cookbook/getDetail/菜谱id
//        //https://m-t.sfbest.com/cookbook/getDetail?cookBookId=33
//        if (null == shareDialog)
//            shareDialog = new ShareDialog(this);
//        shareDialog.setShareTitle(cookBook.getCookBookTitle());
//        shareDialog.setShareTitleUrl(url);
//        shareDialog.setWbShareContent("我在@顺丰优选官方微博 发现了一个很不错的菜谱，快来看看吧！");
//        shareDialog.setShareContent("我在顺丰优选上又学会了一道菜“#" + cookBook.getCookBookTitle() + "#”，精选食材，味道好极了！");
//        shareDialog.setShareImgRes(R.mipmap.common_sfbest_share_logo);
//        shareDialog.setShareSiteUrl(url);
//        shareDialog.show();
//    }
//
//    private void togglePlayerUI() {
//        if (bottomBarLayout.getVisibility() != View.VISIBLE) {
//            playerHandler.sendEmptyMessage(0);
//            bottomBarLayout.setVisibility(View.VISIBLE);
//        } else {
//            playerHandler.removeMessages(0);
//            bottomBarLayout.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    private void toggleFullScreen() {
//        if (isFullScreening) { //退出全屏
//            showActionBarLayout();
//            zoomIv.setVisibility(View.VISIBLE);
//            playerBackIv.setVisibility(View.GONE);
//            RelativeLayout.LayoutParams totalPlayTimeTvLayoutParams = (RelativeLayout.LayoutParams) totalPlayTimeTv.getLayoutParams();
//            totalPlayTimeTvLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0); //传0意为false
//            totalPlayTimeTvLayoutParams.addRule(RelativeLayout.LEFT_OF, zoomIv.getId());
//            totalPlayTimeTv.setLayoutParams(totalPlayTimeTvLayoutParams);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) playerLayout.getLayoutParams();
//            lp.width = (int) surfaceWidth;
//            lp.height = (int) surfaceHeight;
//            playerLayout.setLayoutParams(lp);
////            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerSfv.getLayoutParams();
////            params.height = (int) getResources().getDimension(R.dimen.sf750_600);
////            playerSfv.setLayoutParams(params);
////            playerHolder.setFixedSize(params.width, params.height);
//            computeVideoSize(true);
//        } else { //全屏
//            hideActionBarLayout();
//            zoomIv.setVisibility(View.GONE);
//            playerBackIv.setVisibility(View.VISIBLE);
//            RelativeLayout.LayoutParams totalPlayTimeTvLayoutParams = (RelativeLayout.LayoutParams) totalPlayTimeTv.getLayoutParams();
//            totalPlayTimeTvLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//            totalPlayTimeTv.setLayoutParams(totalPlayTimeTvLayoutParams);
//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) playerLayout.getLayoutParams();
//            lp.width = (int) surfaceLandWidth;
//            lp.height = (int) surfaceLandHeight;
//            playerLayout.setLayoutParams(lp);
////            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerSfv.getLayoutParams();
////            params.setMargins(0, 0, 0, 0);
////            params.height = WindowManager.LayoutParams.MATCH_PARENT;
////            playerSfv.setLayoutParams(params);
////            playerHolder.setFixedSize(params.width, params.height);
//            computeVideoSize(false);
//        }
//        isFullScreening = !isFullScreening;
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (!isFullScreening) {
//            if (!isPush) {
//                SfActivityManager.finishActivity(this);
//            } else {
//                SfActivityManager.startActivity(this, MainActivity.class);
//            }
//            MobclickAgent.onEvent(this, "J2_001");
//        } else {
//            toggleFullScreen();
//        }
//    }
//
//    /**
//     * 批量添加购物车
//     */
//    private void batchAddShopCar() {
//        if (adapter == null || adapter.getData() == null) {
//            return;
//        }
//        CartProduct[] mAsynCartProductArray = new CartProduct[adapter.getData().size()];
//        for (int i = 0; i < mAsynCartProductArray.length; i++) {
//            if (null != adapter.getData().get(i)) {
//                CartProduct cartProduct = new CartProduct();
//                cartProduct.setNumber(adapter.getData().get(i).getNumber());
//                cartProduct.setProductId(adapter.getData().get(i).getProductId());
//                cartProduct.setType(adapter.getData().get(i).getType());
//                cartProduct.setBusinessModel(adapter.getData().get(i).getBusinessModel());
//                mAsynCartProductArray[i] = cartProduct;
//            }
//        }
//        BatchCartProductParams batchCartProductParams = new BatchCartProductParams(true, mAsynCartProductArray);
//        Subscription subscribe = RetrofitUtil.getInstance().create(HttpService.class)
//                .batchCartProduct(GsonUtil.toJson(batchCartProductParams), GsonUtil.toJson(DeviceInfoParam.getDeviceInfoParamForShopCart()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseSubscriber<CartProductResult>(mActivity, BaseSubscriber.MODEL_TOAST) {
//
//
//                    @Override
//                    public void success(CartProductResult result) {
//                        super.success(result);
//                        SfToast.makeText(mActivity, "一键添加成功").show();
//                    }
//                });
//        subscription.add(subscribe);
//        AddToCartUtil.toLoadShopCartNum(this, null);
//    }
//
//
//    private void setCollectStatus(boolean isCollected) {
//        this.isCollected = isCollected;
//        if (isCollected) {
//            collectIv.setImageResource(R.mipmap.collect_press);
//        } else {
//            collectIv.setImageResource(R.mipmap.cookbook_bottom_collection);
//        }
//    }
//
//    private void checkCollectStatus() {
//        HttpService service = RetrofitUtil.getInstance().create(HttpService.class);
//        DelCollectCookBookParam param = new DelCollectCookBookParam(cookBookId);
//        DeviceInfoParam deviceInfoParam = new DeviceInfoParam();
//        Observable<DelCollectCookBookResult> observer = service.isCollectCookBook(GsonUtil.toJson(param), GsonUtil.toJson(deviceInfoParam));
//        CompositeSubscription subscription = new CompositeSubscription();
//        subscription.add(observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<DelCollectCookBookResult>() {
//            @Override
//            public void onCompleted() {
//                // Do nothing
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                // Do nothing
//            }
//
//            @Override
//            public void onNext(final DelCollectCookBookResult result1) {
//                RetrofitExceptionAdapter.fillData(result1, new RetrofitExceptionAdapter.FillDataInterface() {
//                    @Override
//                    public void fillData(CommonResult result) {
//                        // dealAddCollect(result1.data.isResult());
//                        dealCheckCollect(result1.data.isResult());
//                    }
//
//                    @Override
//                    public void showException() {
//                        // Do nothing
//                    }
//                });
//            }
//        }));
//
//
//    }
//
//    private void dealCheckCollect(boolean isCollect) {
//        setCollectStatus(isCollect);
//    }
//
//    private void toggleCollect() {
//        boolean isLogin = UserManager.isLogin(this);
//        if (!isLogin) {
//            Intent intent = new Intent(mActivity, PhoneValidateLogonActivity.class);
//            SfActivityManager.startActivityFromBottom(mActivity, intent);
//        } else {
//            if (!isCollected) {
//                addCollectCookbook();
//            } else {
//                delCollectCookbook();
//            }
//        }
//    }
//
//    private void addCollectCookbook() {
//        HttpService service = RetrofitUtil.getInstance().create(HttpService.class);
//        DelCollectCookBookParam param = new DelCollectCookBookParam(cookBookId);
//        DeviceInfoParam deviceInfoParam = new DeviceInfoParam();
//        Observable<DelCollectCookBookResult> observer = service.addCollectCookBooks(GsonUtil.toJson(param), GsonUtil.toJson(deviceInfoParam));
//        CompositeSubscription subscription = new CompositeSubscription();
//        subscription.add(observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<DelCollectCookBookResult>(mActivity, BaseSubscriber.MODEL_TOAST) {
//            @Override
//            public void success(DelCollectCookBookResult result) {
//                super.success(result);
//                dealAddCollect(result.data.isResult());
//            }
//        }));
//
//
//    }
//
//    private void dealAddCollect(boolean isSuccess) {
//        if (isSuccess) {
//            SfTipsDialog.makeDialog(mActivity, "收藏成功", "收藏的菜谱可在\"我的优选-我的收藏\"里查看").show();
//            EventBus.getDefault().post(new SfBestEvent(SfBestEvent.EventType.CookbookCollectStatusChange, 1));
//            setCollectStatus(true);
//        } else {
//            SfToast.makeText(this, "收藏失败").show();
//        }
//    }
//
//    private void delCollectCookbook() {
//        HttpService service = RetrofitUtil.getInstance().create(HttpService.class);
//        DelCollectCookBookParam param = new DelCollectCookBookParam(cookBookId);
//        DeviceInfoParam deviceInfoParam = new DeviceInfoParam();
//        Observable<DelCollectCookBookResult> observer = service.delCollectCookBook(GsonUtil.toJson(param), GsonUtil.toJson(deviceInfoParam));
//        CompositeSubscription subscription = new CompositeSubscription();
//        subscription.add(observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<DelCollectCookBookResult>(mActivity, BaseSubscriber.MODEL_TOAST) {
//
//            @Override
//            public void success(DelCollectCookBookResult result) {
//                super.success(result);
//                dealDelCollect(result.data.isResult());
//            }
//
//        }));
//
//
//    }
//
//
//    private void dealDelCollect(boolean isSuccess) {
//        if (isSuccess) {
//            SfToast.makeText(this, "取消收藏!").show();
//            EventBus.getDefault().post(new SfBestEvent(SfBestEvent.EventType.CookbookCollectStatusChange, 0));
//            setCollectStatus(false);
//        } else {
//            SfToast.makeText(this, "删除收藏失败").show();
//        }
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        pause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//        if (null != mediaPlayer) {
//            if (mediaPlayer.isPlaying())
//                mediaPlayer.stop();
//            mediaPlayer.release();
//        }
//        if (null != playerHandler)
//            playerHandler.removeCallbacksAndMessages(null);
//
//    }
//
//
//    private void setupPlayer() {
//        if (TextUtils.isEmpty(cookBook.getVideoUrl())) {
//            fakePlayIv.setVisibility(View.GONE);
//            playerLayout.setVisibility(View.GONE);
//            return;
//        }
////        fakePlayIv.setVisibility(View.VISIBLE);
//        progressBar.setVisibility(View.VISIBLE);
//        playerHandler = new PlayerHandler(this);
//        mediaPlayer = new MediaPlayer();
//        playerHolder = playerSfv.getHolder();
//        playerHolder.addCallback(this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mediaPlayer.reset();
//                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
////                    String testUrl = "http://v.jxvdy.com/sendfile/w5bgP3A8JgiQQo5l0hvoNGE2H16WbN09X-ONHPq3P3C1BISgf7C-qVs6_c8oaw3zKScO78I--b0BGFBRxlpw13sf2e54QA";
////                    mediaPlayer.setDataSource(CookbookDetailActivity.this, Uri.parse(testUrl));
//                    mediaPlayer.setDataSource(CookbookDetailActivity.this, Uri.parse(cookBook.getVideoUrl()));
//                    mediaPlayer.setOnVideoSizeChangedListener(CookbookDetailActivity.this);
//                    mediaPlayer.setDisplay(playerHolder);
//                    mediaPlayer.prepare();
//                    mediaPlayer.setOnPreparedListener(CookbookDetailActivity.this);
//                } catch (Exception e) {
//                    LogUtil.e(e);
//                }
//            }
//        }).start();
//    }
//
//    private void play() {
//        if (!isPlayerPrepared)
//            return;
//        recoverTitleIvHeight();
//        titleIv.setVisibility(View.INVISIBLE);
//        playerLayout.setVisibility(View.VISIBLE);
//        fakePlayIv.setVisibility(View.VISIBLE);
//        isFirstPlay = false;
//        start();
//    }
//
//    private void start() {
//        if (null == mediaPlayer || !isPlayerPrepared)
//            return;
//        if (!mediaPlayer.isPlaying()) {
////            mediaPlayer.setDisplay(playerHolder);
//            mediaPlayer.start();
//            progressBar.setVisibility(View.GONE);
//            playerHandler.sendEmptyMessageDelayed(0, PLAYER_UPDATE_DELAY);
//            playerHandler.sendEmptyMessageDelayed(1, PLAYER_UI_HIDE_DELAY);
//            playerHolder.setKeepScreenOn(true);
//            centerPlayIv.setVisibility(View.GONE);
//            bottomPlayIv.setImageResource(R.drawable.player_pause);
//        }
//    }
//
//    private void pause() {
//        if (null == mediaPlayer)
//            return;
//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//            playerHandler.removeMessages(0);
//            playerHolder.setKeepScreenOn(false);
//            centerPlayIv.setVisibility(View.VISIBLE);
//            bottomPlayIv.setImageResource(R.drawable.player_play);
//        }
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        mediaPlayer.setDisplay(holder);
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        // Do nothing
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        // Do nothing
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        playSeekBar.setMax(mp.getDuration());
//        totalPlayTimeTv.setText(TimeUtil.millisecond2Hour(mp.getDuration()));
//        updatePlayerUI(mp);
//        progressBar.setVisibility(View.GONE);
//        fakePlayIv.setVisibility(View.VISIBLE);
//        centerPlayIv.setVisibility(View.VISIBLE);
//        bottomPlayIv.setEnabled(true);
//        zoomIv.setEnabled(true);
//        centerPlayIv.setEnabled(true);
//        isPlayerPrepared = true;
//    }
//
//    @Override
//    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//        if (width == 0 || height == 0)
//            return;
//        computeVideoSize(getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//    }
//
//    private void computeVideoSize(boolean isPortrait) {
//        if (videoWidth == 0) {
//            videoWidth = mediaPlayer.getVideoWidth();
//        }
//        if (videoHeight == 0) {
//            videoHeight = mediaPlayer.getVideoHeight();
//        }
//
//
//        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
//        float max;
//        if (isPortrait) {
//            //竖屏模式下按视频宽度计算放大倍数值
//            max = Math.max((float) videoWidth / (float) surfaceWidth, (float) videoHeight / (float) surfaceHeight);
//        } else {
//            //横屏模式下按视频高度计算放大倍数值
//            max = Math.max(((float) videoWidth / (float) surfaceLandWidth), (float) videoHeight / (float) surfaceLandHeight);
//        }
//
//        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
//        videoWidth = (int) Math.ceil((float) videoWidth / max);
//        videoHeight = (int) Math.ceil((float) videoHeight / max);
//
//        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) playerSfv.getLayoutParams();
//        lp.width = videoWidth;
//        lp.height = videoHeight;
//        playerSfv.setLayoutParams(lp);
//        playerHolder.setFixedSize(lp.width, lp.height);
//    }
//
//    private void updatePlayerUI(MediaPlayer mp) {
//        if (null == curPlayTimeTv || null == playSeekBar)
//            return;
//        long curPosition = mp.getCurrentPosition();
//        curPlayTimeTv.setText(TimeUtil.millisecond2Hour(curPosition));
//        playSeekBar.setProgress((int) curPosition);
//    }
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (fromUser && null != mediaPlayer) {
//            mediaPlayer.seekTo(progress);
//        }
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        // Do nothing
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        // Do nothing
//    }
//
//    private static class PlayerHandler extends Handler {
//        private WeakReference<CookbookDetailActivity> weakActivity;
//
//        public PlayerHandler(CookbookDetailActivity activity) {
//            weakActivity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            CookbookDetailActivity activity = weakActivity.get();
//            if (null == activity)
//                return;
//            switch (msg.what) {
//                case 0: //更新视频UI
//                    if (null != activity.mediaPlayer) {
//                        activity.updatePlayerUI(activity.mediaPlayer);
//                        activity.playerHandler.sendEmptyMessageDelayed(0, PLAYER_UPDATE_DELAY);
//                    }
//                    break;
//                case 1: //隐藏视频UI
//                    if (activity.bottomBarLayout.getVisibility() == View.VISIBLE)
//                        activity.togglePlayerUI();
//                    break;
//                default:
//                    break;
//            }
//
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (null != shareDialog && null != shareDialog.getShareController()) {
//            shareDialog.getShareController().dealSsoActivityResult(requestCode, resultCode, data);
//        }
//    }
//}
