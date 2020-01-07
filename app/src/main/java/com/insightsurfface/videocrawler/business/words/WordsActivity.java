package com.insightsurfface.videocrawler.business.words;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insightsurface.lib.base.BaseRefreshListActivity;
import com.insightsurface.lib.listener.OnRecycleItemClickListener;
import com.insightsurfface.videocrawler.R;
import com.insightsurfface.videocrawler.adapter.WordsAdapter;
import com.insightsurfface.videocrawler.bean.WordsBookBean;
import com.insightsurfface.videocrawler.db.DbAdapter;

import java.util.ArrayList;

public class WordsActivity extends BaseRefreshListActivity {
    private ArrayList<WordsBookBean> wordsList = new ArrayList<WordsBookBean>();
    private DbAdapter db;//数据库
    private WordsAdapter mAdapter;
    private RelativeLayout topBar;
    private TextView topBarLeft;
    private TextView topBarRight;

    @Override
    protected void onCreateInit() {
        db = new DbAdapter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_words;
    }

    @Override
    protected void initUI() {
        super.initUI();
        topBar = (RelativeLayout) findViewById(R.id.top_bar);
        topBarLeft = (TextView) findViewById(R.id.top_bar_left);
        topBarRight = (TextView) findViewById(R.id.top_bar_right);
    }

    @Override
    protected void doGetData() {
        wordsList = db.queryAllWordsBook(this);
        initRec();
    }

    @Override
    protected void initRec() {
        topBarLeft.setText("总计：" + wordsList.size() + "个生词");
        try {
            if (null == mAdapter) {
                mAdapter = new WordsAdapter(this);
                mAdapter.setList(wordsList);
                mAdapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        db.deleteWordByWord(WordsActivity.this, wordsList.get(position).getWord());
                        doGetData();
                    }
                });
                refreshRcv.setAdapter(mAdapter);
            } else {
                mAdapter.setList(wordsList);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            noMoreData();
        }
        noMoreData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }
}
