package com.ismth.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;

import com.ismth.utils.Constants;
import com.ismth.utils.ISmthLog;
import com.ismth.utils.SharePreferencesUtils;

/**
 *2012-2-12 上午11:42:39
 *author:cary
 */
public class PersonInfoActivity extends Activity{

	//用户名
	EditText username;
	//密码
	EditText password;
	String oldUserName;
	String oldPassword;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.person_info);
		username=(EditText)findViewById(R.id.username);
		password=(EditText)findViewById(R.id.password);
		oldUserName=SharePreferencesUtils.getString(Constants.USERNAME, "");
		oldPassword=SharePreferencesUtils.getString(Constants.PASSWORD, "");
		username.setText(oldUserName);
		password.setText(oldPassword);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//监听回退按钮事件，如果当前用户名，密码发生改变提示用户是否保存。
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			if(!"".equals(username.getText().toString()) && !"".equals(password.getText().toString())) {
				//当用户名框和密码框和原有系统存的发生变化，则提示用户是否保存
				if(!oldUserName.equals(username) || !oldPassword.equals(password)) {
					showConfirmSave();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 提示用户是否保存对话框
	 */
	public void showConfirmSave(){
		AlertDialog.Builder builder=new Builder(this);
		builder.setMessage("是否保存新的用户名或密码。");
		builder.setTitle("温馨提示：");
		builder.setPositiveButton("保存", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				saveNewPersonInfo();
				finish();
			}
		});
		builder.setNegativeButton("不保存", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.create().show();
	}

	/**
	 * 保存新的用户信息
	 */
	public void saveNewPersonInfo() {
		SharePreferencesUtils.setString(Constants.USERNAME, username.getText().toString());
		SharePreferencesUtils.setString(Constants.PASSWORD, password.getText().toString());
	}
}
