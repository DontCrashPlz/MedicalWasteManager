package com.hnsi.zheng.medicalwastemanager.collect;

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
import com.hnsi.zheng.medicalwastemanager.apps.BaseActivity;
import com.hnsi.zheng.medicalwastemanager.apps.MainActivity;
import com.hnsi.zheng.medicalwastemanager.beans.CollectedWasteEntity;
import com.hnsi.zheng.medicalwastemanager.beans.format.CardDataEntity;
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

public class CollectedListActivity extends BaseActivity {

    @BindView(R.id.edittext)
    EditText mSearchEdittext;
    @BindView(R.id.button)
    Button mSearchBtn;
    @BindView(R.id.recycler)
    RecyclerView mCollectedRecycler;

    private CollectedRecyclerAdapter mAdapter;
    private CardDataEntity mCollectPerson;
    private String mOrgId;
    private String mUserId;

    private ArrayList<CollectedWasteEntity> mCurrentListDatas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_list);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("已收集医废");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCollectPerson= (CardDataEntity) getIntent().getSerializableExtra(MainActivity.COLLECT_PERSON);
        mOrgId= mCollectPerson.getOrgId();
        mUserId= mCollectPerson.getUserId();

        mCollectedRecycler.setLayoutManager(new LinearLayoutManager(getRealContext()));
        mAdapter= new CollectedRecyclerAdapter(R.layout.item_collected_waste_recycler);
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showDeleteWasteDialog(position);
                return true;
            }
        });
        mAdapter.bindToRecyclerView(mCollectedRecycler);
        mCollectedRecycler.setAdapter(mAdapter);
        mCollectedRecycler.addItemDecoration(new CollectedWasteItemDecoration());

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchKey= mSearchEdittext.getText().toString().trim();
                if (searchKey== null || searchKey.length()< 1){
                    showShortToast("请输入关键字");
                    return;
                }
                requestCollectedList(searchKey);
            }
        });

        requestCollectedList(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home)
            finish();
        return true;
    }

    private void requestCollectedList(@Nullable String key){
        addNetWork(Network.getInstance().getCollectedList(mOrgId, mUserId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(ResponseTransformer.<ArrayList<CollectedWasteEntity>>handleResult())
                .subscribe(new Consumer<ArrayList<CollectedWasteEntity>>() {
                    @Override
                    public void accept(ArrayList<CollectedWasteEntity> collectedWasteEntities) throws Exception {
                        dismissDialog();
                        if (collectedWasteEntities.size()> 0){
                            mAdapter.setNewData(collectedWasteEntities);
                            mCurrentListDatas= collectedWasteEntities;
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

    private void deleteCollectedWaste(final int position){
        CollectedWasteEntity entity= mCurrentListDatas.get(position);
        String orgId= "" + entity.getOrgId();
        String guid= entity.getGuid();
        addNetWork(Network.getInstance().deleteCollectedWaste(orgId, guid)
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
        View view = LayoutInflater.from(getRealContext()).inflate(R.layout.layout_collected_waste_dialog, null);
        // 设置style 控制默认dialog带来的边距问题
        final Dialog dialog = new Dialog(getRealContext(), R.style.custom_dialog_no_titlebar);
        dialog.setContentView(view);
        dialog.show();

        final CollectedWasteEntity entity= mCurrentListDatas.get(position);

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
                deleteCollectedWaste(position);
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
