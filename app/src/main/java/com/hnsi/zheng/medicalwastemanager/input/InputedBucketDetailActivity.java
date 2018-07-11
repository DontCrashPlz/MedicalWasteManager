package com.hnsi.zheng.medicalwastemanager.input;

import android.app.Dialog;
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

public class InputedBucketDetailActivity extends BaseActivity {

    @BindView(R.id.tv_bucketnum)
    TextView mBucketNumTv;
    @BindView(R.id.tv_wastenum)
    TextView mWasteNumTv;
    @BindView(R.id.tv_bucketweigh)
    TextView mBucketWeighTv;
    @BindView(R.id.tv_inputtime)
    TextView mInputTimeTv;

    @BindView(R.id.edittext)
    EditText mSearchEdittext;
    @BindView(R.id.button)
    Button mSearchBtn;
    @BindView(R.id.recycler)
    RecyclerView mWasteRecycler;

    private CollectedRecyclerAdapter mAdapter;

    private InputedBucketEntity bucketEntity;

    private ArrayList<CollectedWasteEntity> mCurrentListDatas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_detail_list);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("已入库医废");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bucketEntity= (InputedBucketEntity) getIntent().getSerializableExtra("inputed_bucket_entity");
        mBucketNumTv.setText(bucketEntity.getGuid());
        mWasteNumTv.setText(bucketEntity.getWasteAmount() + "袋");
        mBucketWeighTv.setText(bucketEntity.getWasteWeight() + "KG");
        mInputTimeTv.setText(bucketEntity.getCreateTime());

        mWasteRecycler.setLayoutManager(new LinearLayoutManager(getRealContext()));
        mAdapter= new CollectedRecyclerAdapter(R.layout.item_collected_waste_recycler);
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDeleteWasteDialog(position);
                return true;
            }
        });
        mAdapter.bindToRecyclerView(mWasteRecycler);
        mWasteRecycler.setAdapter(mAdapter);
        mWasteRecycler.addItemDecoration(new CollectedWasteItemDecoration());

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKey= mSearchEdittext.getText().toString().trim();
                if (searchKey== null || searchKey.length()< 1){
                    showShortToast("请输入关键字");
                    return;
                }
                requestBucketDetail(searchKey);
            }
        });

        requestBucketDetail(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            setResult(InputedListActivity.BUCKET_DETAIL_RESULT_CODE);
            finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(InputedListActivity.BUCKET_DETAIL_RESULT_CODE);
        finish();
    }

    private void requestBucketDetail(@Nullable String key){
        addNetWork(Network.getInstance().getInputedBucketInfo("" + bucketEntity.getOrgId(), "" + bucketEntity.getCreateBy(), bucketEntity.getGuid(), key, "" + bucketEntity.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(ResponseTransformer.<InputedBucketEntity>handleResult())
                .subscribe(new Consumer<InputedBucketEntity>() {
                    @Override
                    public void accept(InputedBucketEntity inputedBucketEntities) throws Exception {
                        dismissDialog();
                        if (inputedBucketEntities.getWasteList().size()> 0){
                            mAdapter.setNewData(inputedBucketEntities.getWasteList());
                            mCurrentListDatas= inputedBucketEntities.getWasteList();
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

    private void deleteInputedWaste(final int position){
        CollectedWasteEntity entity= mCurrentListDatas.get(position);
        String orgId= "" + bucketEntity.getOrgId();
        String guid= bucketEntity.getGuid();
        String wasteGuid= entity.getGuid();
        addNetWork(Network.getInstance().deleteInputedWaste(orgId, guid, wasteGuid)
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
                            mAdapter.setNewData(new ArrayList<CollectedWasteEntity>());
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
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_inputed_waste_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.show();

        CollectedWasteEntity entity= mCurrentListDatas.get(position);

        TextView typeTv= view.findViewById(R.id.tv_wastetype);
        typeTv.setText(entity.getWasteTypeDictName());
        TextView weighTv= view.findViewById(R.id.tv_wasteweigh);
        weighTv.setText(entity.getWeight() + "kg");
        TextView numTv= view.findViewById(R.id.tv_wastenum);
        numTv.setText(entity.getGuid());
        TextView timeTv= view.findViewById(R.id.tv_wastetime);
        timeTv.setText(entity.getCreateTime());
        TextView departmentTv= view.findViewById(R.id.tv_department);
        departmentTv.setText(entity.getDepartmentName() + "(" + entity.getDepartmentUserName() + ")");
        TextView collectPresonTv= view.findViewById(R.id.tv_collectpreson);
        collectPresonTv.setText(entity.getCreateByName());

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
                deleteInputedWaste(position);
            }
        });

        // 设置相关位置，一定要在 show()之后
        Window window = dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

}
