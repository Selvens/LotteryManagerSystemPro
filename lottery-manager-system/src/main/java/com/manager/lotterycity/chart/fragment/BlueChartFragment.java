package com.manager.lotterycity.chart.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

import com.manager.Interface.FragmentListener;
import com.manager.Interface.ICoallBack22;
import com.manager.adapter.lotterycity.ChartBallsGridviewAdapter;
import com.manager.adapter.lotterycity.GridViewAdapter1;
import com.manager.adapter.lotterycity.SsqPostzoneBallsAdapter;
import com.manager.bean.ChartBean;
import com.manager.common.Constants;
import com.manager.common.Tools;
import com.manager.lotterypro.R;
import com.manager.widgets.CHScrollView;
import com.manager.lotterycity.LotteryChartScrollAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 蓝球走势
 * @author donghuiyang
 * @create time 2016/7/15 0015.
 */
public class BlueChartFragment extends Fragment {
    private static final int MaxNum = 16;

    //上下文对象
    private Context mContext = null;

    //方便测试，直接写的public
    public HorizontalScrollView mTouchView;
    private ListView mListView;
    private LotteryChartScrollAdapter adapter;
    private List<Map<String, Object>> datas = new ArrayList<Map<String,Object>>();

    //装入所有的HScrollView
    protected List<CHScrollView> mHScrollViews =new ArrayList<CHScrollView>();
    private GridView headerGridView;
    //底部选号栏
    private GridView bottomGridView;
    private SsqPostzoneBallsAdapter blueAdapter;

    //已选号码栏
    private GridView mSelectedGridView;
    private ChartBallsGridviewAdapter ballSelectedAdapter;

    //号码数量
    private String[] blueBallNums = new String[MaxNum];
    private List<Integer> headerLists;

    //已选的红蓝球
    private ArrayList redBallNumList = new ArrayList();
    private ArrayList blueBallNumList = new ArrayList();

    private FragmentListener fragmentListener;

    private View rootView = null;
    private static BlueChartFragment mFragment = null;
    /**
     * 静态工厂方法需要一个int型的值来初始化fragment的参数，
     * 然后返回新的fragment到调用者
     */
    public static BlueChartFragment newInstance() {
        if (mFragment == null) {
            mFragment = new BlueChartFragment();
        }

        return mFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            fragmentListener = (FragmentListener)activity;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("", "activity 没有实现FragmentListener接口");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();

        loadRedBallNums();
        initHeaderData();
        initListData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mHScrollViews.clear();
        mHScrollViews = null;

        headerLists = null;

        redBallNumList.clear();
        redBallNumList = null;
        blueBallNumList.clear();
        blueBallNumList = null;

        blueBallNums = null;

        datas.clear();
        datas = null;

        mFragment = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != rootView) {
            //缓存view
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.ssq_chart_layout, container, false);

            initBottomView();
            initListView();
        }

        return rootView;
    }

    /**
     * 更新选中的红球
     * @param value 选中的值
     * @param isRemove 添加或者删除
     */
    public void update(String value, boolean isRemove){
        if (!isRemove){
            redBallNumList.add(value);
        }else{
            redBallNumList.remove(value);
        }

        ballSelectedAdapter.notifyDataSetChanged();
    }

    private void initHeaderData(){
        headerLists = Constants.ssqBlueChartHeaderItemLits;
    }

    private void initListData(){
        Map<String, Object> data = null;
        for(int i = 0; i < Constants.SsqChartLists.size(); i++) {
            ChartBean att = Constants.SsqChartLists.get(i);
            if (att != null){
                data = new HashMap<String, Object>();
                data.put("title", att.getTitle());
                data.put("data", att.getBlueLists());
                datas.add(data);
            }
        }
    }

    /**
     * 动态加载红球
     */
    private void loadRedBallNums() {
        for (int i = 0; i < blueBallNums.length; i++) {
            if (i >= 9) {
                blueBallNums[i] = String.valueOf(i + 1);
            } else {
                blueBallNums[i] = "0" + (i + 1);
            }
        }
    }

    /**
     * 底部选号栏
     */
    private void initBottomView(){
        CHScrollView bottomScroll = (CHScrollView) rootView.findViewById(R.id.chart_select_number_tem_scroll_title);
        bottomScroll.setTouchView(mTouchView);
        bottomScroll.setCallBack(new CHScrollView.ICoallBack() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                mFragment.onScrollChanged(l, t, oldl, oldt);
            }
        });
        //添加表头滑动事件
        mHScrollViews.add(bottomScroll);

        bottomGridView = (GridView) rootView.findViewById(R.id.chart_select_number_grid);
        setBottomGridView();

        //已选号码
        mSelectedGridView = (GridView) rootView.findViewById(R.id.lottery_chart_gridview1);
        ballSelectedAdapter = new ChartBallsGridviewAdapter(mContext, R.layout.red_number1, redBallNumList, blueBallNumList);
        mSelectedGridView.setAdapter(ballSelectedAdapter);
    }

    private void initListView(){
        CHScrollView headerScroll = (CHScrollView) rootView.findViewById(R.id.chart_item_ssq_item_scroll_title);
        headerScroll.setTouchView(mTouchView);
        headerScroll.setCallBack(new CHScrollView.ICoallBack() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                mFragment.onScrollChanged(l, t, oldl, oldt);
            }
        });

        headerGridView = (GridView) rootView.findViewById(R.id.chart_item_grid);
        setHeaderGridView();

        //添加表头滑动事件
        mHScrollViews.add(headerScroll);
        //表内容
        mListView = (ListView) rootView.findViewById(R.id.lottery_chart_scroll_list);
        adapter = new LotteryChartScrollAdapter(mContext, datas, R.layout.chart_item_header
                , new String[] { "title", "data"}
                , new int[] {R.id.chart_item_title_tv
                , R.id.chart_item_grid
                , R.id.chart_item_ssq_rootview}, MaxNum);
        mListView.setAdapter(adapter);
        adapter.setCallBack(new LotteryChartScrollAdapter.ICoallBack() {
            @Override
            public void dealWithScrollView(CHScrollView hScrollView) {
                addHViews(hScrollView);
            }
        });
    }

    /**
     * 设置GirdView参数，绑定数据
     */
    private void setHeaderGridView() {
        Tools.updateHorizontalGridView(getActivity(), headerLists.size(), 50, 1, headerGridView);

        GridViewAdapter1 adapter = new GridViewAdapter1(mContext, headerLists);
        headerGridView.setAdapter(adapter);
    }

    public void addHViews(final CHScrollView hScrollView) {
        if(!mHScrollViews.isEmpty()) {
            int size = mHScrollViews.size();
            CHScrollView scrollView = mHScrollViews.get(size - 1);
            scrollView.setCallBack(new CHScrollView.ICoallBack() {
                @Override
                public void onScrollChanged(int l, int t, int oldl, int oldt) {
                    mFragment.onScrollChanged(l, t, oldl, oldt);
                }
            });
            final int scrollX = scrollView.getScrollX();
            //第一次满屏后，向下滑动，有一条数据在开始时未加入
            if(scrollX != 0) {
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        //当listView刷新完成之后，把该条移动到最终位置
                        hScrollView.scrollTo(scrollX, 0);
                    }
                });
            }
        }
        mHScrollViews.add(hScrollView);
    }

    private void setBottomGridView() {
        Tools.updateHorizontalGridView(getActivity(), headerLists.size(), 50, 1, bottomGridView);

        blueAdapter = new SsqPostzoneBallsAdapter(mContext, R.layout.white_number, blueBallNums, blueBallNumList, 2);
        blueAdapter.setGridView(bottomGridView);
        bottomGridView.setAdapter(blueAdapter);
        blueAdapter.setonClick(new ICoallBack22() {
            @Override
            public void addBlueView(String value) {
                //添加球
                // 将选择的号码加入一个集合，以便于好管理
                if (!blueBallNumList.contains(value)) {
                    blueBallNumList.add(value);

                    //添加后更新已选号码列表
                    ballSelectedAdapter.notifyDataSetChanged();

                    fragmentListener.onFragmentClickListener(0, value, false);
                }
            }

            @Override
            public void removeBlueView(String value) {
                //删除选中的红球
                if (cutBlueNumList(value)) {
                    //删除后更新已选号码列表
                    ballSelectedAdapter.notifyDataSetChanged();
                    fragmentListener.onFragmentClickListener(0, value, true);
                }
            }
        });
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt){
        for(HorizontalScrollView scrollView : mHScrollViews) {
            //防止重复滑动
            if(mTouchView != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }



    /**
     * 取消所选的蓝球，blueBallNumList移除此球号码
     *
     * @param ballNum
     */
    private boolean cutBlueNumList(String ballNum) {
        for (int i = 0; i < blueBallNumList.size(); i++) {
            if (blueBallNumList.get(i).equals(ballNum)) {
                blueBallNumList.remove(i);
                return true;
            }
        }

        return false;
    }
}
