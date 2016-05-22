package com.ppamy.pptips.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ppamy.pptips.FragmentBase;
import com.ppamy.pptips.R;
import com.ppamy.pptips.datamgr.TipsDataManager;
import com.ppamy.pptips.dbs.tips_dao.Tips;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * 所有的记录的activity
 * */
public class AllItemsActivity extends FragmentBase {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private ProgressBar mProgressBar;
    private StaggeredGridLayoutManager mLayoutManager;
    private TipsDataManager mTipsDataManager;

    private static final int ANIM_DURATION_FAB = 400;

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
        }
    };
    
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_items, null);
        mTipsDataManager = TipsDataManager.getInstance();
        
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
//        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
//        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mAdapter = new MyAdapter(getActivity());
        mAdapter.setOnClickListener(new OnMyItemClick() {
            @Override
            public void onItemClick(int pos) {
                Tips tips = mAdapter.getBook(pos);
                AddNewItemActivity.startAddEditItemActivity(getActivity(), tips.getId());
            }

            @Override
            public boolean onItemLongClick(final int position) {
                final Tips tips = mAdapter.getBook(position);
                if (tips != null){
                    AlertDialog dlg = new AlertDialog.Builder(getActivity())
                            .setTitle("提示")
                            .setMessage("确定要删除此笔记吗？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mTipsDataManager.delItem(tips.getId());
                                    mAdapter.notifyItemRemoved(position);
                                    mAdapter.removeItem(position);
                                    mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();


                    return true;
                }
                return false;
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        getActivity().registerReceiver(mUpdateReceiver,new IntentFilter(com.ppamy.pptips.AppEnv.ACTION_TIPS_CHANGED));
        loadData();
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mUpdateReceiver);
    }

    private void loadData() {
	    new AsyncTask<Void, Void, List<Tips>>() {
	        @Override
	        protected void onPreExecute() {
	            mProgressBar.setVisibility(View.VISIBLE);
	            super.onPreExecute();
	        }

            @Override
            protected List<Tips> doInBackground(Void... params) {

                List<Tips> allData = mTipsDataManager.loadAllItems();
                Collections.sort(allData, new Comparator<Tips>() {

                    @Override
                    public int compare(Tips lhs, Tips rhs) {
                        long delta = rhs.getDate() - lhs.getDate();
                        if (delta > 0){
                            return 1;
                        }else if (delta < 0){
                            return -1;
                        }else{
                            return 0;
                        }
                    }
                });
                return allData;
            }
            @Override
            protected void onPostExecute(List<Tips> result) {
                
                super.onPostExecute(result);
                mAdapter.updateItems(result, true);
                mProgressBar.setVisibility(View.GONE);
            }
        }.execute();
	}

//    private RecyclerItemClickListener.OnItemClickListener onItemClickListener = new RecyclerItemClickListener.OnItemClickListener() {
//        @Override
//        public void onItemClick(View view, int position) {
//            Tips tips = mAdapter.getBook(position);
//            AddNewItemActivity.startAddEditItemActivity(getActivity(), tips.getId());
//
////            Intent intent = new Intent(AllItemsActivity.this, BookDetailActivity.class);
////            intent.putExtra("book", book);
////
////            ActivityOptionsCompat options =
////                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
////                            view.findViewById(R.id.ivBook), getString(R.string.transition_book_img));
////
////            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//
//        }
//    };
	
	public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private final int mBackground;
        private List<Tips> mBooks = new ArrayList<Tips>();
        private final TypedValue mTypedValue = new TypedValue();
        private OnMyItemClick mOnItemClick;

        private static final int ANIMATED_ITEMS_COUNT = 4;

        private boolean animateItems = false;
        private int lastAnimatedPosition = -1;

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public void setOnClickListener(OnMyItemClick l ){
            mOnItemClick = l;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
            public TextView tvTitle;
            public TextView tvDesc;
            public TextView tvDate;

            public int position;

            public ViewHolder(View v) {
                super(v);
                tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                tvDesc = (TextView) v.findViewById(R.id.tvDesc);
                tvDate = (TextView) v.findViewById(R.id.tvDate);

                v.setOnClickListener(this);
                v.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClick != null){
                    return mOnItemClick.onItemLongClick(position);
                }
                return false;
            }

            @Override
            public void onClick(View v) {
                if (mOnItemClick != null){
                    mOnItemClick.onItemClick(position);

                }
            }
        }
        public void removeItem(int position){
            mBooks.remove(position);
        }

        public void updateItems(List<Tips> books, boolean animated) {
            animateItems = animated;
            lastAnimatedPosition = -1;
            mBooks.clear();
            mBooks.addAll(books);
            notifyDataSetChanged();
        }

        public void clearItems() {
            mBooks.clear();
            notifyDataSetChanged();
        }


        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.tips_item, parent, false);
            //v.setBackgroundResource(mBackground);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
//            runEnterAnimation(holder.itemView, position);
            Tips book = mBooks.get(position);
            holder.position = position;
            holder.tvTitle.setText(book.getSubject());
            holder.tvDesc.setText(book.getBody());
            holder.tvDate.setText(DateFormat.format("yyyy-MM-dd hh:mm ", book.getDate()));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mBooks.size();
        }


        public Tips getBook(int pos) {
            return mBooks.get(pos);
        }
    }
    public interface OnMyItemClick {
        void onItemClick(int pos);
        boolean onItemLongClick(int pos);
    }
    @Override
    public String getTagText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onBackPressed() {
        // TODO Auto-generated method stub
        return false;
    }
}
