/**
 * AppWidgetSample
 */
package com.sentiense.crickwidget.main;

import java.util.ArrayList;
import java.util.List;

import org.developerworks.android.ParserType;

import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.sentiense.crickwidget.R;
import com.sentiense.crickwidget.common.DataFetchUtil;

/**
 * @author sharmila Dec 21, 2011
 * 
 */
public class CricketWidgetConfigure extends Activity {
	static final String TAG = "CricketWidgetConfigure";

	private static final String PREFS_NAME = "com.sentiense.crickwidget.main.CricketScoreWidget";
	private static final String MATCH_LIST = "prefix_";
	private static final String REFRESH_RATE = "";
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	GoogleAnalyticsTracker tracker;

	List<String> listItems;

	public CricketWidgetConfigure() {
		super();
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// Set the result to CANCELED. This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);

		// Set the view layout resource to use.
		setContentView(R.layout.appwidget_configure);
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.start("UA-5782596-4", 10, this);

		// Bind the action for the save button.
		findViewById(R.id.save_button).setOnClickListener(mOnClickListener);

		if (!haveNetworkConnection()) {
//			Log.d(TAG, "Inside no connection block");
			final Dialog in_dialog = new Dialog(CricketWidgetConfigure.this);
			in_dialog.setContentView(R.layout.no_internet);
			in_dialog.setCancelable(true);

			Button okbutton = (Button) in_dialog.findViewById(R.id.no_internet_ok);
			okbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent resultValue = new Intent();
					resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
							mAppWidgetId);
					tracker.trackEvent("CricketWidgetDialog", "DialogOk", "",
							-1);
					setResult(RESULT_CANCELED, resultValue);
					in_dialog.dismiss();
					finish();
				}
			});
			in_dialog.show();
		
		}
		getTeamList();

		// Find the widget id from the intent.
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

	}

	@Override
	public void onDestroy() {
		tracker.stop();
		super.onDestroy();
	}

	public void onResume() {
		tracker.trackPageView("/CricketWidgetConfigure");
		super.onResume();
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			final Context context = CricketWidgetConfigure.this;
			tracker.trackEvent("CricketWidgetConfigure", "ConfigOk", "", -1);
			// When the button is clicked, save the string in our prefs and
			// return that they
			// clicked OK.

			String titlePrefix = "";
			ListView lView = (ListView) findViewById(R.id.scoreList);
			if(lView.getChildCount()>0){			
			SparseBooleanArray checked = lView.getCheckedItemPositions();
			for (int i = 0; i < checked.size(); i++) {
				if (checked.valueAt(i)) {
					titlePrefix = titlePrefix + DataFetchUtil.getCountryNames(listItems.get(checked.keyAt(i)))
							+ "~";
				}

			}
			
			if (titlePrefix.equals("")) {
				final Dialog dialog = new Dialog(CricketWidgetConfigure.this);
				dialog.setContentView(R.layout.select_match_popup);
				dialog.setCancelable(true);

				Button button = (Button) dialog.findViewById(R.id.select_match_ok);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent resultValue = new Intent();
						resultValue.putExtra(
								AppWidgetManager.EXTRA_APPWIDGET_ID,
								mAppWidgetId);
						tracker.trackEvent("CricketWidgetDialog", "DialogOk",
								"", -1);
						setResult(RESULT_CANCELED, resultValue);
						dialog.dismiss();
					}
				});
				dialog.show();
			} else {

//				Log.d(TAG, "Selected values are " + titlePrefix);

				// String titlePrefix = mAppWidgetPrefix.getText().toString();
				saveTitlePref(context, mAppWidgetId, titlePrefix, "1");

				// Push widget update to surface with newly set prefix
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
				CricketScoreWidget cricketScoreWidget = new CricketScoreWidget();
				cricketScoreWidget.updateAppWidgetScore(context,
						appWidgetManager, mAppWidgetId, titlePrefix);

				// Make sure we pass back the original appWidgetId
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						mAppWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
			}
		}
		else{
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					mAppWidgetId);
			setResult(RESULT_OK, resultValue);
			finish();
		}
		}
	};

	// Write the prefix to the SharedPreferences object for this widget
	static void saveTitlePref(Context context, int appWidgetId, String text,
			String refreshRate) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(MATCH_LIST + appWidgetId, text);
		prefs.putString(REFRESH_RATE + appWidgetId, refreshRate);
		prefs.commit();
	}

	// Read the prefix from the SharedPreferences object for this widget.
	// If there is no preference saved, get the default from a resource
	static String[] loadTitlePref(Context context, int appWidgetId) {
		String[] prefix = new String[2];
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		prefix[0] = prefs.getString(MATCH_LIST + appWidgetId, null);
		prefix[1] = prefs.getString(REFRESH_RATE + appWidgetId, null);

		if (prefix[0] == null) {
			prefix[0] = context.getString(R.string.appwidget_prefix_default);
		}
		if (prefix[1] == null) {
			prefix[1] = "5";
		}
		return prefix;
	}

	static void deleteTitlePref(Context context, int appWidgetId) {
	}

	static void loadAllTitlePrefs(Context context,
			ArrayList<Integer> appWidgetIds, ArrayList<String> texts) {
	}

	private void getTeamList() {
		ListView lView = (ListView) findViewById(R.id.scoreList);
		listItems = DataFetchUtil.loadFeed(ParserType.ANDROID_SAX);
		if (listItems != null && !listItems.isEmpty()) {
			lView.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_multiple_choice,
					listItems));
			lView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		}else{
			TextView txtView = (TextView) findViewById(R.id.no_live_match);
			TextView selectList = (TextView)findViewById(R.id.config_instruction);
			selectList.setText("");
			txtView.setText(R.string.live_match_alert);
		}
	}

	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;
		boolean haveConnection = false;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			haveConnection = true;
		} else {
			haveConnection = false;
		}
		return haveConnection || haveConnectedWifi || haveConnectedMobile;
	}

	
	
}
