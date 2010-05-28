package com.mazgi.voicenowtter.android.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener {
	public enum ExtraKeyNames{
		HOST,PORT,USER,PASS,
	}
	private TextView tweet;
	private TextView tweets;
	private Button tweetButton;
	private HttpClient client;
	private String token;

	private void initWidgets(){
		tweet=(TextView) this.findViewById(R.id.TweetText);
		tweets=(TextView) this.findViewById(R.id.Tweets);
		tweetButton=(Button) this.findViewById(R.id.TweetButton);
		tweetButton.setOnClickListener(this);
	}
	
	private HttpClient createHttpClient(){
		Intent i=this.getIntent();
		int port=i.getIntExtra(ExtraKeyNames.PORT.name(), 80);
			
		HttpParams params=new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 100);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		SchemeRegistry reg=new SchemeRegistry();
		reg.register(new Scheme("http",PlainSocketFactory.getSocketFactory(),port));
		ClientConnectionManager conMan=new ThreadSafeClientConnManager(params,reg);
		
		HttpClient c=new DefaultHttpClient(conMan,params);
		return c;
	}
	
	private String login(final HttpClient c) throws URISyntaxException, ClientProtocolException, IOException{
		Intent i=this.getIntent();
		String host=i.getStringExtra(ExtraKeyNames.HOST.name());
		int port=i.getIntExtra(ExtraKeyNames.PORT.name(), 80);
		String user=i.getStringExtra(ExtraKeyNames.USER.name());
		String pass=i.getStringExtra(ExtraKeyNames.PASS.name());
		
		URI uri=new URI("http", null, host, port, "/auth/login", null, null);
		HttpPost loginMethod=new HttpPost(uri);
		
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name",user));
		params.add(new BasicNameValuePair("password",pass));
		UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"UTF-8");
		loginMethod.setEntity(entity);
		tweets.append(entity.toString()+"\n");
		
		HttpResponse res=c.execute(loginMethod);
		if(200!=res.getStatusLine().getStatusCode())
			throw new IOException();//TODO:例外の種類検討
		
		BufferedReader reader=new BufferedReader(
				new InputStreamReader(
						res.getEntity().getContent()));
		
		//authenticity_token	RE902v7nHrVqPMjR1cuvMVTd/7E3Ax48Jofk8bgzcbc=
		String line;
		while(null!=(line=reader.readLine())){
			//String[] param=line.split("=");
			tweets.append(line+"\n");
		}
		reader.close();
		
		return null;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        initWidgets();        
        client=createHttpClient();
        try {
			login(client);
		} catch (ClientProtocolException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			tweets.append(e.getMessage()+"\n");
		} catch (URISyntaxException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			tweets.append(e.getMessage()+"\n");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			tweets.append(e.getMessage()+"\n");
		}
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.TweetButton:
			break;
		}
	}
}
