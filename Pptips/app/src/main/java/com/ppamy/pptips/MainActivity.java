package com.ppamy.pptips;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.ppamy.pptips.datamgr.TipsDataManager;
import com.ppamy.pptips.ui.AddNewItemActivity;
import com.ppamy.pptips.ui.AllItemsActivity;
import com.ppamy.pptips.ui.MainFragment;
import com.ppamy.pptips.util.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements FragmentBase.SelectedFragmentInterface{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private FragmentBase selectedFragment;
    private View mOperView;
    
    private long exitTime = 0L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//title bar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupDrawerContent(mNavigationView);
        
        mOperView = findViewById(R.id.oper_view);
        
		final View contentView = findViewById(R.id.add_view);
		contentView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				gotoAddNewItem();
			}
		});
		
		View view = findViewById(R.id.history);
		view.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				gotoAllItem();
			}
		});
		
		gotoMainFragment();
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void hideOperViews(){
	    if (mOperView != null && mOperView.isShown()){
	        mOperView.setVisibility(View.GONE);
	    }
	}
	private void showOperViews(){
	    if (mOperView != null && !mOperView.isShown()){
	        mOperView.setVisibility(View.VISIBLE);
	    }
	}
	
	private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_item_notes:
                                gotoAllItem();
                                break;
                            case R.id.navigation_save_2_sd:
                                save2sd();
                                break;
                            case R.id.navigation_load_from_sd:
                                loadFromSd();
                                break;
                            case R.id.navigation_item_favor:
                                Toast.makeText(MainActivity.this, R.string.wait_pls, Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.navigation_item_about:
                                break;


                        }
                        menuItem.setChecked(false);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    /**将笔记保存到sd卡中*/
    private void save2sd() {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return Utils.copyFile(getDatabasePath(AppEnv.DB_TIPS_NAME).getAbsolutePath(), AppEnv.getBackupFilePath());
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    Toast.makeText(MainActivity.this, R.string.save2sd_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.save2sd_error, Toast.LENGTH_SHORT).show();

                }
            }
        };
        asyncTask.execute();

    }

    private void loadFromSd(){
        File file = new File(AppEnv.getBackupFilePath());
        if ( file.exists()){
            new AsyncTask<Void,Void,Boolean>(){

                @Override
                protected Boolean doInBackground(Void... params) {
                    return Utils.copyFile(AppEnv.getBackupFilePath(),getDatabasePath(AppEnv.DB_TIPS_NAME).getAbsolutePath());
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    if(aBoolean){
                        Toast.makeText(MainActivity.this, R.string.loadsd_success, Toast.LENGTH_SHORT).show();
//                        Utils.killSelf();
                        TipsDataManager.getInstance().destory();
                        gotoMainFragment();

                    }else{
                        Toast.makeText(MainActivity.this, R.string.loadsd_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();

        }else{
            Toast.makeText(MainActivity.this, R.string.loadsd_error, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 重新启动 activity
     * */
    public void restartActivity(){
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void recreate() {
        try {//避免重启太快 恢复
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
        }
        super.recreate();
    }


    /**
	 * 进入主页面
	 * */
	private void gotoMainFragment() {
		Fragment fg = new MainFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fg).commit();
	}
	
	private void gotoAddNewItem() {
        Intent intent = new Intent(MainActivity.this,AddNewItemActivity.class);
        startActivity(intent);
//	    Fragment fg = new AddNewItemActivity();
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fg).commit();
//        mToolbar.setTitle("");
//        hideOperViews();
    }

    private void gotoAllItem() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new AllItemsActivity()).commit();
        mToolbar.setTitle("所有记录");
        hideOperViews();
    }
    
    public void setTitleTxt(String txt){
        mToolbar.setTitle(txt);
    }

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再次点击返回键退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
            	if (selectedFragment != null && !(selectedFragment instanceof MainFragment)){
            		showOperViews();
            		gotoMainFragment();
            		mToolbar.setTitle(R.string.app_name);
            	}else{
                    doExitApp();
                }
            }

    }

    @Override
    public void setSelectedFragment(FragmentBase fragment) {
        this.selectedFragment = fragment;
    }
	
	
}
