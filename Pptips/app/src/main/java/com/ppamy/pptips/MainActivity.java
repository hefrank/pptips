package com.ppamy.pptips;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.ppamy.pptips.ui.AddNewItemActivity;
import com.ppamy.pptips.ui.AllItemsActivity;
import com.ppamy.pptips.ui.MainFragment;

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
