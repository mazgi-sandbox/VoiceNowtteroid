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
import org.apache.http.client.methods.HttpGet;
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
	private EditText tweet;
	private TextView tweets;
	private Button tweetButton;
	private HttpClient client;
	private HttpPost loginMethod;
	private HttpGet showMethod;
	private HttpPost tweetMethod;

	private void initWidgets(){
		tweet=(EditText) this.findViewById(R.id.TweetText);
		tweets=(TextView) this.findViewById(R.id.Tweets);
		tweetButton=(Button) this.findViewById(R.id.TweetButton);
		tweetButton.setOnClickListener(this);
	}
	
	private HttpClient createHttpClient(int port){
		HttpParams params=new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 100);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		SchemeRegistry reg=new SchemeRegistry();
		reg.register(new Scheme("http",PlainSocketFactory.getSocketFactory(),port));
		ClientConnectionManager conMan=new ThreadSafeClientConnManager(params,reg);
		HttpClient c=new DefaultHttpClient(conMan,params);
		return c;
	}
	
	private void createLoginMethod(final String host, int port) throws URISyntaxException{
		URI uri=new URI("http", null, host, port, "/auth/login", null, null);
		loginMethod=new HttpPost(uri);
	}
	
	private void createShowMethod(final String host, int port, final String user) throws URISyntaxException{
		URI uri=new URI("http", null, host, port, "/users/"+user, null, null);
		showMethod=new HttpGet(uri);
	}
	
	private void createTweetMethod(String host, int port) throws URISyntaxException{
		URI uri=new URI("http", null, host, port, "/statuses", null, null);
		tweetMethod=new HttpPost(uri);
	}
	
	private void login(HttpClient c, String user, String pass) throws URISyntaxException, ClientProtocolException, IOException{
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name",user));
		params.add(new BasicNameValuePair("password",pass));
		UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"UTF-8");
		loginMethod.setEntity(entity);
		HttpResponse res=c.execute(loginMethod);
		if(200!=res.getStatusLine().getStatusCode()) throw new IOException();//TODO:例外の種類検討
//		BufferedReader reader=new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
//		String line;
//		while(null!=(line=reader.readLine())){
//			//String[] param=line.split("=");
//			tweets.append(line+"\n");
//		}
//		reader.close();
//		return null;
	}
	
	private void tweet(HttpClient c) throws ClientProtocolException, IOException{
		String text=tweet.getText().toString();
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("status[text]",text));
		UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"UTF-8");
		tweetMethod.setEntity(entity);
		HttpResponse res=c.execute(tweetMethod);
		if(200!=res.getStatusLine().getStatusCode()) throw new IOException();//TODO:例外の種類検討
	}
	
	private void reload(HttpClient c) throws ClientProtocolException, IOException{
		HttpResponse res=c.execute(showMethod);
		if(200!=res.getStatusLine().getStatusCode()) throw new IOException();//TODO:例外の種類検討
		tweets.setText("suc\n");
		BufferedReader reader=new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
		String line;
		while(null!=(line=reader.readLine())){
			//String[] param=line.split("=");
			tweets.append(line+"\n");
		}
		reader.close();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        initWidgets();        
        
		Intent i=this.getIntent();
		String host=i.getStringExtra(ExtraKeyNames.HOST.name());
		int port=i.getIntExtra(ExtraKeyNames.PORT.name(), 80);
		String user=i.getStringExtra(ExtraKeyNames.USER.name());
		String pass=i.getStringExtra(ExtraKeyNames.PASS.name());
        
        client=createHttpClient(port);
        try {
			createLoginMethod(host, port);
			createShowMethod(host, port, user);
			createTweetMethod(host, port);
			login(client, user, pass);
			reload(client);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			tweets.append(e.getMessage()+"\n");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			tweets.append(e.getMessage()+"\n");
		} catch (IOException e) {
			e.printStackTrace();
			tweets.append(e.getMessage()+"\n");
		}
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.TweetButton:
			try {
				tweet(client);
				reload(client);
			} catch (ClientProtocolException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				tweets.setText(e.getMessage()+"\n");
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				tweets.setText(e.getMessage()+"\n");
			}
			break;
		}
	}
}
