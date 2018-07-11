package com.hnsi.zheng.medicalwastemanager.input;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hnsi.zheng.medicalwastemanager.R;
import com.hnsi.zheng.medicalwastemanager.adapters.CollectedRecyclerAdapter;
import com.hnsi.zheng.medicalwastemanager.adapters.InputedRecyclerAdapter;
import com.hnsi.zheng.medicalwastemanager.apps.BaseActivity;
import com.hnsi.zheng.medicalwastemanager.beans.CollectedWasteEntity;
import com.hnsi.zheng.medicalwastemanager.beans.InputedBucketEntity;
import com.hnsi.zheng.medicalwastemanager.https.Network;
import com.hnsi.zheng.medicalwastemanager.https.ResponseTransformer;
import com.hnsi.zheng.medicalwastemanager.widgets.CollectedWasteItemDecoration;
import com.hnsi.zheng.medicalwastemanager.widgets.progressDialog.ProgressDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Zheng on 2018/7/8.
 */

public class InputedListActivity extends BaseActivity {

    static final int BUCKET_DETAIL_REQUEST_CODE= 1;
    static final int BUCKET_DETAIL_RESULT_CODE= 2;

    @BindView(R.id.edittext)
    EditText mSearchEdittext;
    @BindView(R.id.button)
    Button mSearchBtn;
    @BindView(R.id.recycler)
    RecyclerView mInputedRecycler;

    private InputedRecyclerAdapter mAdapter;
    private String[] mCollectInfos;
    private String mOrgId;//医院id
    private String mUserId;//操作人员id

    private ArrayList<InputedBucketEntity> mCurrentListDatas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_list);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("已入库医废");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCollectInfos= getIntent().getStringExtra("input_person_info").split("_");
        mOrgId= mCollectInfos[3];
        mUserId= mCollectInfos[1];

        mInputedRecycler.setLayoutManager(new LinearLayoutManager(getRealContext()));
        mAdapter= new InputedRecyclerAdapter(R.layout.item_inputed_waste_recycler);
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDeleteWasteDialog(position);
                return true;
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                InputedBucketEntity entity= mCurrentListDatas.get(position);
                Intent intent= new Intent(getRealContext(), InputedBucketDetailActivity.class);
                intent.putExtra("inputed_bucket_entity", entity);
                startActivityForResult(intent, BUCKET_DETAIL_REQUEST_CODE);
            }
        });
        mAdapter.bindToRecyclerView(mInputedRecycler);
        mInputedRecycler.setAdapter(mAdapter);
        mInputedRecycler.addItemDecoration(new CollectedWasteItemDecoration());

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKey= mSearchEdittext.getText().toString().trim();
                if (searchKey== null || searchKey.length()< 1){
                    showShortToast("请输入关键字");
                    return;
                }
                requestInputedList(searchKey);
            }
        });

        requestInputedList(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            finish();
        return true;
    }

    private void requestInputedList(@Nullable String key){
        addNetWork(Network.getInstance().getInputedList(mOrgId, mUserId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(ResponseTransformer.<ArrayList<InputedBucketEntity>>handleResult())
                .subscribe(new Consumer<ArrayList<InputedBucketEntity>>() {
                    @Override
                    public void accept(ArrayList<InputedBucketEntity> inputedBucketEntities) throws Exception {
                        dismissDialog();
                        if (inputedBucketEntities.size()> 0){
                            mAdapter.setNewData(inputedBucketEntities);
                            mCurrentListDatas= inputedBucketEntities;
                        }else {
                            mAdapter.setEmptyView(R.layout.layout_recycler_empty);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dismissDialog();
                        showLongToast(throwable.toString());
                        mAdapter.setEmptyView(R.layout.layout_recycler_error);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                }));
    }

    private void deleteInputedBucket(final int position){
        InputedBucketEntity entity= mCurrentListDatas.get(position);
        String orgId= "" + entity.getOrgId();
        String userId= "" + entity.getCreateBy();
        String guid= entity.getGuid();
        String id= "" + entity.getId();
        addNetWork(Network.getInstance().deleteInputedBucket(orgId, userId, guid, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(ResponseTransformer.<String>handleResult())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        dismissDialog();
                        mCurrentListDatas.remove(position);
                        if (mCurrentListDatas.size()> 0){
                            mAdapter.setNewData(mCurrentListDatas);
                        }else {
                            mAdapter.setNewData(new ArrayList<InputedBucketEntity>());
                            mAdapter.setEmptyView(R.layout.layout_recycler_empty);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dismissDialog();
                        showLongToast(throwable.toString());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                }));
    }

    @Override
    public void initProgressDialog() {
        dialog= new ProgressDialog(getRealContext());
        dialog.setLabel("请稍候..");
    }

    /**
     * 弹出删除医废弹窗
     */
    private void showDeleteWasteDialog(final int position){
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_inputed_bucket_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.show();

        final InputedBucketEntity entity= mCurrentListDatas.get(position);

        TextView text1= view.findViewById(R.id.tv_bucketnum);
        text1.setText(entity.getGuid());
        TextView text2= view.findViewById(R.id.tv_wastenum);
        text2.setText(entity.getWasteAmount() + "(袋)");
        TextView text3= view.findViewById(R.id.tv_inputtime);
        text3.setText(entity.getCreateTime());
        TextView text4= view.findViewById(R.id.tv_bucketweigh);
        text4.setText(entity.getWasteWeight() + "KG");
        TextView text5= view.findViewById(R.id.tv_inputpreson);
        text5.setText(entity.getCreateByName());

        TextView button1= view.findViewById(R.id.dialog_btn1);
        TextView button2= view.findViewById(R.id.dialog_btn2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 删除医废
                dialog.dismiss();
                deleteInputedBucket(position);
            }
        });

        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== BUCKET_DETAIL_REQUEST_CODE && resultCode== BUCKET_DETAIL_RESULT_CODE){
            requestInputedList(null);
        }
    }
}
