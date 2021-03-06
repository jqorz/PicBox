package com.jqorz.picbox.frame;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jelly.mango.Mango;
import com.jelly.mango.MultiplexImage;
import com.jqorz.picbox.R;
import com.jqorz.picbox.adapter.ImageAdapter;
import com.jqorz.picbox.base.BaseActivity;
import com.jqorz.picbox.fingerprint.CryptoObjectHelper;
import com.jqorz.picbox.fingerprint.FingerprintAuthCallback;
import com.jqorz.picbox.helper.DialogHelper;
import com.jqorz.picbox.helper.FingerprintResultHelper;
import com.jqorz.picbox.model.ImageModel;
import com.jqorz.picbox.cons.Config;
import com.jqorz.picbox.utils.ImageSearch;
import com.jqorz.picbox.utils.LockUtil;
import com.jqorz.picbox.utils.ToastUtil;
import com.jqorz.picbox.utils.UserDataUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.USE_FINGERPRINT;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, FingerprintResultHelper.FingerprintResultListener {

    public static final int REQUEST_PERMISSIONS = 1;
    private static final String TAG = "MainActivity";
    private static final int GRID_COLUMN_SIZE = 3;

    private Disposable searchDisposable;
    private Disposable lockDisposable;

    private ImageAdapter mImageAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View emptyTipView;
    private AlertDialog checkFingerprintDialog;

    private LockUtil lockUtil;
    private FingerprintManagerCompat fingerprintManager;
    private FingerprintAuthCallback fingerprintAuthCallback;
    private CancellationSignal cancellationSignal;

    private boolean isRunning = false;//标记当前是否有后台任务

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        checkFingerPrint();
        initRecyclerView();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void initView() {

        mRecyclerView = findViewById(R.id.rl_image);

        emptyTipView = findViewById(R.id.tip_empty);

        mSwipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        fingerprintManager = FingerprintManagerCompat.from(this);
        cancellationSignal = new CancellationSignal();

        FingerprintResultHelper fingerprintResultHelper = new FingerprintResultHelper(this);
        fingerprintResultHelper.setFingerprintResultListener(this);

        fingerprintAuthCallback = new FingerprintAuthCallback(fingerprintResultHelper.getHandler());

        lockUtil = new LockUtil();

    }

    private void checkFingerPrint() {

        if (!UserDataUtil.loadSettingUseFingerprint()) {
//            onFingerprintSuccess();
            DialogHelper.createTestDialog(this, this);
            return;
        }

        if (!fingerprintManager.isHardwareDetected()) {
            // 无法检测到指纹输入硬件时，提示用户
            DialogHelper.createNoHardwareDialog(this);
            UserDataUtil.updateSettingUseFingerprint(false);
            return;
        }
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager != null && keyguardManager.isKeyguardSecure()) {
            //设备已使用了加锁，可以使用指纹
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // 未检测到已录入的指纹
                DialogHelper.createNoFingerprintDialog(this);
            } else {
                try {
                    CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
                    fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0,
                            cancellationSignal, fingerprintAuthCallback, null);
                    //弹出扫描指纹的对话框
                    checkFingerprintDialog = DialogHelper.createCheckFingerprintDialog(this, cancellationSignal);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    @TargetApi(M)
    private void requestPermissions() {
        final String[] permissions = new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, USE_FINGERPRINT};

        boolean showRationale = false;
        for (String perm : permissions) {
            showRationale |= shouldShowRequestPermissionRationale(perm);
        }
        if (!showRationale) {
            requestPermissions(permissions, REQUEST_PERMISSIONS);
            return;
        }
        DialogHelper.showRequestPermissionDialog(this, permissions);
    }


    private void initRecyclerView() {
        mImageAdapter = new ImageAdapter(GRID_COLUMN_SIZE);
        mImageAdapter.setOnItemClickListener((adapter, view, position) -> {
            List<ImageModel> models = mImageAdapter.getData();
            Mango.setImages(loadImage(models)); //设置图片源
            Mango.setPosition(position); //设置初始显示位置
            Mango.open(MainActivity.this); //开启图片浏览
        });
        mImageAdapter.bindToRecyclerView(mRecyclerView);


        //解决刷新闪烁的问题
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        mRecyclerView.setItemAnimator(itemAnimator);

        mRecyclerView.setLayoutManager( new GridLayoutManager(this, GRID_COLUMN_SIZE));
    }

    private boolean hasPermissions() {
        PackageManager pm = getPackageManager();
        String packageName = getPackageName();
        int granted = pm.checkPermission(READ_EXTERNAL_STORAGE, packageName) | pm.checkPermission(WRITE_EXTERNAL_STORAGE, packageName);
        return granted == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检索文件
     *
     * @param needUnLock 检索后是否需要执行解密的操作
     */
    private void startPicSearch(final boolean needUnLock, final boolean needFingerprint) {
        if (!hasPermissions()) {
            requestPermissions();
        }
        if (searchDisposable != null && !searchDisposable.isDisposed()) return;
        mSwipeRefreshLayout.setRefreshing(true);

        searchDisposable = Observable.just(Config.SCREEN_CAPTURE)
                .flatMap((Function<String, ObservableSource<File>>) s -> ImageSearch.listImageFiles(new File(s)))
                .map(file -> {
                    long time = file.lastModified();
                    return new ImageModel(time, file.getAbsolutePath(), ImageSearch.isLock(file));
                })
                .sorted((o1, o2) -> {
                    //按照时间倒序排列
                    return Long.compare(o2.getLongTime(), o1.getLongTime());
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    emptyTipView.setVisibility((mImageAdapter.getData().size() > 0) ? View.GONE : View.VISIBLE);
                    if (mImageAdapter.getData().size() == 0 && needFingerprint) {
                        checkFingerPrint();
                    }
                })
                .map(mData -> {
                    convertData(mData);//排序
                    return mData;
                })
                .subscribe(imageModels -> {
                    //如果图片存在加密的，则进行解密
                    if (needUnLock && getLockSize(imageModels) > 0) {
                        startLockOrUnlockPic(false, imageModels);
                    } else {
                        mImageAdapter.replaceData(getUnlockData(imageModels));
                    }
                }, throwable -> Log.e(TAG, "error = " + throwable.getMessage()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            int granted = PackageManager.PERMISSION_GRANTED;
            for (int r : grantResults) {
                granted |= r;
            }
            if (granted == PackageManager.PERMISSION_GRANTED) {
                startPicSearch(true, true);
            } else {
                ToastUtil.showToast("请先授予存储区&指纹权限");
            }
        }
    }

    //图片列表
    public List<MultiplexImage> loadImage(List<ImageModel> models) {
        List<MultiplexImage> images = new ArrayList<>();
        for (int i = 0, j = models.size(); i < j; i++) {
            String path = models.get(i).getPath();
            images.add(new MultiplexImage(path, MultiplexImage.ImageType.NORMAL));
        }
        return images;
    }

    /**
     * 将数据按照日期进行分组
     */
    private void convertData(List<ImageModel> mData) {
        LinkedHashMap<String, List<ImageModel>> skuIdMap = new LinkedHashMap<>();
        for (ImageModel resourceBean : mData) {
            List<ImageModel> tempList = skuIdMap.get(resourceBean.getDate());
            //如果取不到数据,那么直接new一个空的ArrayList
            if (tempList == null) {
                tempList = new ArrayList<>();
                tempList.add(resourceBean);
                skuIdMap.put(resourceBean.getDate(), tempList);
            } else {
                //某个sku之前已经存放过了,则直接追加数据到原来的List里
                tempList.add(resourceBean);
            }
        }

        Iterator<String> it = skuIdMap.keySet().iterator();
        for (int group = 0; it.hasNext(); group++) {
            List<ImageModel> imageModels = skuIdMap.get(it.next());
            for (int i = 0, j = imageModels.size(); i < j; i++) {
                imageModels.get(i).setNum(i);
                imageModels.get(i).setGroupNum(j);
                imageModels.get(i).setGroup(group);
            }
        }
        mData.clear();
        for (List<ImageModel> list : skuIdMap.values()) {
            mData.addAll(list);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_function, menu);
        MenuItem action_user_fingerprint = menu.findItem(R.id.action_user_fingerprint);
        action_user_fingerprint.setChecked(UserDataUtil.loadSettingUseFingerprint());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_lock:
                int sum = mImageAdapter.getData().size();
                if (sum > 0) {
                    int lockSize = getLockSize(mImageAdapter.getData());
                    showLockDialog(sum, lockSize);
                } else {
                    item.setEnabled(false);
                }

                break;
            case R.id.action_user_fingerprint:
                if (!fingerprintManager.isHardwareDetected()) {
                    ToastUtil.showToast("未检测到指纹识别模块，功能开启失败");
                    return false;
                }
                boolean oldState = item.isChecked();
                item.setChecked(!oldState);
                UserDataUtil.updateSettingUseFingerprint(!oldState);
        }
        return false;
    }

    private int getLockSize(List<ImageModel> allData) {
        int lockSize = 0;
        for (ImageModel imageModel : allData) {
            if (imageModel.isLock()) {
                lockSize++;
            }
        }
        return lockSize;
    }

    private List<ImageModel> getUnlockData(List<ImageModel> allData) {
        List<ImageModel> data = new ArrayList<>();
        for (ImageModel imageModel : allData) {
            if (!imageModel.isLock()) {
                data.add(imageModel);
            }
        }
        return data;
    }

    private void showLockDialog(final int sumSize, final int lockSize) {
        new AlertDialog.Builder(this)
                .setTitle("一键加密")
                .setMessage("共有" + sumSize + "张图片，其中" + lockSize + "已加密，" + (sumSize - lockSize) + "张未加密")
                .setNegativeButton("取消", null)
                .setPositiveButton("加密", (dialog, which) -> startLockOrUnlockPic(true, mImageAdapter.getData())).show();
    }

    private void startLockOrUnlockPic(final boolean toLock, List<ImageModel> data) {
        final AlertDialog dialog = DialogHelper.createProgressDialog(this, toLock);
        List<ImageModel> models = new ArrayList<>(data);
        lockDisposable = Observable.fromIterable(models)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSubscribe(disposable -> isRunning = true)
                .doOnNext(imageModel -> {
                    if (!toLock & imageModel.isLock()) {
                        lockUtil.unlock(new File(imageModel.getPath()));
                    } else if (toLock & !imageModel.isLock()) {
                        lockUtil.lock(new File(imageModel.getPath()));
                    }
                })
                .filter(imageModel -> !imageModel.isLock())
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    dialog.dismiss();
                    isRunning = false;
                })
                .doOnError(throwable -> ToastUtil.showToast(toLock ? "加密失败" : "解密失败"))
                .subscribe(imageModel -> {
                    ToastUtil.showToast(toLock ? "加密完成" : "解密完成");
                    startPicSearch(false, toLock);
                }, throwable -> com.jqorz.aydassistant.util.Logg.e(throwable.getMessage()));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchDisposable != null && !searchDisposable.isDisposed()) {
            searchDisposable.dispose();
        }
        if (lockDisposable != null && !lockDisposable.isDisposed()) {
            lockDisposable.dispose();
        }
    }

    @Override
    public void onRefresh() {
        if (isRunning) {
            mSwipeRefreshLayout.setRefreshing(false);
            ToastUtil.showToast("当前有任务正在进行");
        } else {
            startPicSearch(false, true);
        }
    }

    @Override
    public void onFingerprintSuccess() {
        if (checkFingerprintDialog != null) {
            checkFingerprintDialog.dismiss();
        }
        startPicSearch(true, false);
    }

    @Override
    public void onFingerprintFail() {

    }
}
