package com.mazgi.voicenowtter.android.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Main extends Activity implements OnClickListener {
	private EditText host;
	private EditText port;
	private EditText user;
	private EditText pass;
	private Button loginButton;
	
	private void initWidgets(){
		host=(EditText) this.findViewById(R.id.HostName);
		port=(EditText) this.findViewById(R.id.Port);
		user=(EditText) this.findViewById(R.id.UserName);
		pass=(EditText) this.findViewById(R.id.Password);
		loginButton=(Button) this.findViewById(R.id.LoginButton);
		loginButton.setOnClickListener(this);
	}
	
	private Intent createLoginIntent(){
		Intent i=new Intent(this,Home.class);
		String h=host.getText().toString();
		int p=Integer.parseInt(port.getText().toString());
		String u=user.getText().toString();
		String pw=pass.getText().toString();
		i.putExtra(Home.ExtraKeyNames.HOST.name(), h);
		i.putExtra(Home.ExtraKeyNames.PORT.name(), p);
		i.putExtra(Home.ExtraKeyNames.USER.name(), u);
		i.putExtra(Home.ExtraKeyNames.PASS.name(), pw);
		return i;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initWidgets();
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.LoginButton:
			Intent i=createLoginIntent();
			this.startActivity(i);
			break;
		}
	}
}