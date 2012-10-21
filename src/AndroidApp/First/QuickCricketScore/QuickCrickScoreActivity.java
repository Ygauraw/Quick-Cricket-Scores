package AndroidApp.First.QuickCricketScore;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class QuickCrickScoreActivity extends ListActivity {
	com.google.ads.AdView adView;	
	ArrayList<String> updates;
	static final int PROGRESS_DIALOG = 0;
	Button button;
	ProgressThread progressThread;
	ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);
      
      try{
    	  
    	 //code on click of any item of list view
      	 ListView listView = (ListView)findViewById(android.R.id.list);
       	 Button btnRecent = (Button)findViewById(R.id.btnRecent);
		 Button btnUpcoming = (Button)findViewById(R.id.btnUpcoming);
		 Button btnRefresh = (Button)findViewById(R.id.btnRefresh);
	     
			 
	     if(!isNetworkAvailable())
	   	 {
			btnRecent.setEnabled(false);
			btnRefresh.setEnabled(false);
			btnUpcoming.setEnabled(false);
			Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_LONG).show();
			return;
	   	 }
		
	     ProgressBar myProgressBar = (ProgressBar)findViewById(R.id.pbApp); 
       	 myProgressBar.setProgress(20);	
      	 getAds();	 
         
	     //final ArrayList<String> updates = getRecentBCCIResults();
		  
		 myProgressBar.setProgress(50);
      	 loadNews();
      	 
      	 //Toast.makeText(getApplicationContext(), "loading complete...", Toast.LENGTH_LONG).show();
      	 
      	 myProgressBar.setProgress(100);
      	 
      	 btnRecent.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i= new Intent(QuickCrickScoreActivity.this, RecentAndUpcoming.class);
					i.putExtra("ScoreType","Recent");
					startActivity(i);
				}
			});
			 
      	 btnUpcoming.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i= new Intent(QuickCrickScoreActivity.this, RecentAndUpcoming.class);
					i.putExtra("ScoreType","Upcoming");
					startActivity(i);

				}
			});
      	 
      	 btnRefresh.setOnClickListener(new OnClickListener() {
					
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						updates = getCurrentBCCIResults();
						Toast.makeText(getApplicationContext(), "loading complete...", Toast.LENGTH_SHORT).show();
					}
				});
			 
      	 listView.setOnItemClickListener(new OnItemClickListener() 
			 {

				public void onItemClick(AdapterView<?> arg0, View selectedItem,
						int position, long arg3) 
				{
					try
					{
						// TODO Auto-generated method stub
						if(updates.size() > 1)
						{
							Intent i= new Intent(QuickCrickScoreActivity.this, ScoreBoard.class);
							LinearLayout catchThis = (LinearLayout)selectedItem;
							TextView txtUpdate = (TextView)catchThis.findViewById(R.id.txtUpdate);
							i.putExtra("ScoreBoardURL",updates.get(position) + "#" + txtUpdate.getText().toString() );
							startActivity(i);
						}
						else
						{
							Toast.makeText(getApplicationContext(), "Match will start at scheduled date time.", 
									Toast.LENGTH_SHORT).show();
						}
					}
					catch (Exception e) {
						// TODO: handle exception
						String Message = e.getMessage();
						Message = "1";
					}
				}
			});
      
      	 
      
      }
      catch (Exception e) {
			// TODO: handle exception
      	String Message = e.getMessage();
      	Message = "1";
      }


      
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showDialog(PROGRESS_DIALOG);
		int count = 0;
		do
		{
			updates = getCurrentBCCIResults();
			count++;
		}while(updates.size() == 0 && count < 5);
		
	
	}
	
	protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(QuickCrickScoreActivity.this);
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

	private void loadNews() {
		 
		 WebView webView = (WebView)findViewById(R.id.webViewNews);
		 WebSettings webSettings = webView.getSettings();
		 webSettings.setJavaScriptEnabled(true);
		 webView.loadUrl("http://bcci.com//News/News_brief.html");
	}

	public ArrayList<String> getCurrentBCCIResults() 
	{
		ArrayList<HashMap<String, ?>> data = new ArrayList<HashMap<String, ?>>();
		
		ArrayList<String> updates =  new ArrayList<String>();
		
		try
		{
					
			ScoreFetcher sf = new ScoreFetcher();
			updates = sf.getXpathContent("http://bcci.com/Updates/Updates.html", "//html//body//table/tbody");
	    	
			//code to put it in list view man
			 String[] update = updates.get(updates.size() - 1).split("                         ");
			 		 
			 for(int i = 0; i<update.length; i++)
			 {
				 update[i] = update[i].replaceAll("^\\s+", "");
				 
				 if(!update[i].equals("") && !update[i].contains("TargetDate"))
				 {
					 String temp = update[i].replace("Time left:", "");
					 HashMap<String, Object> row  = new HashMap<String, Object>();
					 row.put("Update", temp.replaceAll("\\s+", " "));
					 data.add(row);
					 
				 }
			 }
			 
			 
			 SimpleAdapter adapter = new SimpleAdapter(this,
	                    data,
	                      R.layout.row,
	                      new String[] {"Update"},
	                      new int[] { R.id.txtUpdate }); 
			 
			 setListAdapter(adapter);
			 
			 return updates;
			 
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			String Message = e.getMessage().toString();
			return updates;

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