package AndroidApp.First.QuickCricketScore;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import AndroidApp.First.QuickCricketScore.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ScoreBoard extends Activity {
	com.google.ads.AdView adView;
	WebView webView ;
	String url = "";
	String recentURLs = "";
	static final int PROGRESS_DIALOG = 0;
	Button button;
	ProgressThread progressThread;
	ProgressDialog progressDialog;
	 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scoreboard);
	
		final Button btnSMS_Score = (Button)findViewById(R.id.btnShareScore);
		final Button btnFullScore = (Button)findViewById(R.id.btnFullScore);
		final Button btnDeskScore = (Button)findViewById(R.id.btnDesktopScore);
		
		
		//get link
		try
		{
			getAds();
			
			if(!isNetworkAvailable())
		   	{
				Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_LONG).show();
				return;
		   	}
			final Bundle extras = getIntent().getExtras();
			if(extras !=null) 
			{
				String values = extras.getString("ScoreBoardURL");
				if(values != null && values != "")
				{
					
					String value[] = values.split("#");
					url = "http://bcci.com" + value[0];
					ProgressBar pbApp = (ProgressBar)findViewById(R.id.pbApp);
					pbApp.setProgress(100);
					
				}
				else {
					
					recentURLs = extras.getString("RecentURL");
					if(recentURLs  != "")
					{
						String value[] = recentURLs.split("#");
						recentURLs = "http://bcci.com" + value[0];
				
						Toast.makeText(getApplicationContext(), "loading complete...", Toast.LENGTH_SHORT).show();
						
						ProgressBar pbApp = (ProgressBar)findViewById(R.id.pbApp);
						pbApp.setProgress(100);
					
					}
				}
				
			}
		
						
			btnFullScore.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(!isNetworkAvailable())
				   	{
						Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_LONG).show();
						return;
				   	}
					if(extras !=null) 
					{
						String value = extras.getString("ScoreBoardURL");
						if(value != null && value != "")
						{
							value = "http://bcci.com" + value;
							WebView webView  = (WebView)findViewById(R.id.myWebView);
							value = value.replace(".html", "_full.html");
							value = value.replace("Main_", "");
							webView.loadUrl(value);
							//Toast.makeText(getApplicationContext(), "loading complete...", Toast.LENGTH_SHORT).show();							
						}
						
						
					}
				}
			});
			
			btnDeskScore.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!isNetworkAvailable())
				   	{
						Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_LONG).show();
						return;
				   	}
					if(extras !=null) 
					{
						String value = extras.getString("ScoreBoardURL");
						value = "http://bcci.com" + value;
						WebView webView  = (WebView)findViewById(R.id.myWebView);
						value = value.replace(".html", "_dtop.html");
						value = value.replace("Main_", "");
						webView.loadUrl(value);
						//Toast.makeText(getApplicationContext(), "loading complete...", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			btnSMS_Score.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if(extras != null)
					{
						String values = extras.getString("ScoreBoardURL");
						String value[] = values.split("#");
						//old code
						//Intent i= new Intent(ScoreBoard.this, Share_Score.class);
						//i.putExtra("ScoreBoardURL",value[1]);
						//startActivity(i);
						Intent sendIntent = new Intent();
				        sendIntent.setAction(Intent.ACTION_SEND);
				        sendIntent.putExtra(Intent.EXTRA_TEXT, "Live Cricket Score : " + value[1] + " via " + "http://tinyurl.com/6nnqz9j");
				        sendIntent.setType("text/plain");
				        startActivity(sendIntent);
						
					}
				}
			});
			
			
		
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			String Message = e.getMessage();
			Message = "1";
		}
	}

	protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(ScoreBoard.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading...");
            return progressDialog;
        default:
            return null;
        }
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        	// Define the Handler that receives messages from the thread and update the progress
        	int total = msg.arg1;
            progressDialog.setProgress(total);
            if (total >= 100){
                dismissDialog(PROGRESS_DIALOG);
                progressThread.setState(ProgressThread.STATE_DONE);
            }
        }
    };
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog.setProgress(0);
            progressThread = new ProgressThread(handler);
            progressThread.start();
        }

    }   

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showDialog(PROGRESS_DIALOG);
		if(url != "")
		{
			webView  = (WebView)findViewById(R.id.myWebView);
			
			if(url.contains("Main"))
			{
				url = url.replace(".html", "_dtop.html");
				url = url.replace("Main_", "");
				webView.loadUrl(url);
			}
			else {
				webView.loadUrl(url);
			}
		}
		else{
			webView  = (WebView)findViewById(R.id.myWebView);
			webView.loadUrl(recentURLs);
			
			final Button btnDeskScore = (Button)findViewById(R.id.btnDesktopScore);
			final Button btnFullScore = (Button)findViewById(R.id.btnFullScore);
			final Button btnSMS_Score = (Button)findViewById(R.id.btnShareScore);
			
			btnDeskScore.setEnabled(false);
			btnFullScore.setEnabled(false);
			btnSMS_Score.setEnabled(false);
		}
		
		
		if(!url.contains("_dtop"))
		{
			final Button btnDeskScore = (Button)findViewById(R.id.btnDesktopScore);
			final Button btnFullScore = (Button)findViewById(R.id.btnFullScore);
			final Button btnSMS_Score = (Button)findViewById(R.id.btnShareScore);
			
			btnDeskScore.setEnabled(false);
			btnFullScore.setEnabled(false);
			btnSMS_Score.setEnabled(false);
		}
			
		
	}
	
	public void getAds()
	{
		// Create the adView
        adView = new AdView(this, AdSize.BANNER, "a14f0428973c871");

        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/mainLayout"
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayoutAdMob);

        // Add the adView to it
        layout.addView(adView);

        // Initiate a generic request to load it with an ad
        AdRequest request = new AdRequest();
        //request.setTesting(true);
        adView.loadAd(request);

		
	}
	
	public boolean isNetworkAvailable() {
			boolean haveConnectedWifi = false;
		    boolean haveConnectedMobile = false;

		    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		    for (NetworkInfo ni : netInfo) {
		        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
		            if (ni.isConnected())
		                haveConnectedWifi = true;
		        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
		            if (ni.isConnected())
		                haveConnectedMobile = true;
		    }
		    return haveConnectedWifi || haveConnectedMobile;

		}

}
