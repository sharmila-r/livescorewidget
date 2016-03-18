package com.sentiense.crickwidget.main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.developerworks.android.ParserType;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sentiense.crickwidget.R;
import com.sentiense.crickwidget.common.DataFetchUtil;



public class CricketScoreWidget extends AppWidgetProvider { // log tag
//	private static final String TAG = "CricketScoreWidgetProvider";
private static Map<Integer, Boolean> deletedWidget= new HashMap<Integer,Boolean>();
Timer timer = new Timer();
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		String matchList="";
		float refreshRate=60000;
		// For each widget that needs an update, get the text that we should
		// display:
		// - Create a RemoteViews object for it
		// - Set the text in the RemoteViews object
		// - Tell the AppWidgetManager to show that views object for the widget.
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.main);
			remoteViews.setOnClickPendingIntent(R.id.refresh_button, getPendingSelfIntent(context,"ham",appWidgetId));
			WlanTimer test = new WlanTimer(context, appWidgetId,
					matchList, appWidgetManager, remoteViews);
			timer.scheduleAtFixedRate(test, 1, (long) refreshRate);
		}
	}
	
	protected PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetId) {
	    Intent intent = new Intent(context, getClass());
	    intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	    return PendingIntent.getBroadcast(context, appWidgetId, intent, 0);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// When the user deletes the widget, delete the preference associated
		// with it.
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			CricketWidgetConfigure.deleteTitlePref(context, appWidgetIds[i]);
			deletedWidget.put(appWidgetIds[i], true);
		}
	}


	 void updateAppWidgetScore(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			String titlePrefix) {
		if(deletedWidget!=null && !deletedWidget.isEmpty()){
				timer.cancel();
				timer.purge();
			}
		String[] prefScoreView = titlePrefix.split("~");
		List<String> codes = Arrays.asList(prefScoreView);
		
		// Getting the string this way allows the string to be localized. The
		// format
		// string is filled in using java.util.Formatter-style format strings.

		List<String> scoreList = DataFetchUtil.loadFeed(ParserType.ANDROID_SAX);
		String scores = "";
		if (scoreList != null && scoreList.size() > 0) {
			for (String score : scoreList) {
				if (score.equals(R.string.appwidget_prefix_default)) {
					scores = "Please select matches in the home page ";
					break;
				}
				String countryName = DataFetchUtil.getCountryNames(score);
				if (codes.contains(countryName)) {
					scores = scores + " | " + score;
				}

			}
		} else {
			scores = "Sorry! No live scores available";
		}
		// Construct the RemoteViews object. It takes the package name (in our
		// case, it's our
		// package, but it needs this because on the other side it's the widget
		// host inflating
		// the layout from our package).
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.main);
		views.setTextViewText(R.id.tv, scores);
		// Tell the widget manager
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	  //@Override
	    public void onReceive(Context context, Intent intent) {
	   		
		  AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		  final int currentAppWidgetId =intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
	     String[] prefScoreViews;

			if(deletedWidget!=null && !deletedWidget.isEmpty()){
				if(deletedWidget.get(currentAppWidgetId)!=null&&deletedWidget.get(currentAppWidgetId)){
					timer.cancel();
					timer.purge();
				}			
			}
			String[] prefList = CricketWidgetConfigure.loadTitlePref(context , currentAppWidgetId);
			prefScoreViews = prefList[0].split("~");
			List<String> scoreList = DataFetchUtil
					.loadFeed(ParserType.ANDROID_SAX);
			String scores = "";
			if (scoreList != null && scoreList.size() > 0) {
				for (String score : scoreList) {
					if (score.equals(R.string.appwidget_prefix_default)) {
						scores = "Please select matches in the home page ";
						break;
					}
					
					if (prefScoreViews != null) {
						List<String> codes = Arrays.asList(prefScoreViews);
						String countryName = DataFetchUtil.getCountryNames(score);
						if (codes.contains(countryName)) {
							scores = scores + " | " + score;
						}
					} else {
						scores ="Sorry! No live scores available";
					}
				}
			} else {
				scores = "Sorry! No live scores available";
			}
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.main);
			remoteViews.setTextViewText(R.id.tv, scores);
			appWidgetManager.updateAppWidget(currentAppWidgetId, remoteViews);
			 super.onReceive(context, intent);
	 }

	private class WlanTimer extends TimerTask {
		RemoteViews remoteViews;
		AppWidgetManager appWidgetManager;
//		ComponentName thisWidget;
		int currentAppWidgetId;
		String[] prefScoreViews;
		Context thisContext;
		public WlanTimer(Context context, int appWidgetId, String prefList,
				AppWidgetManager appWidgetManager, RemoteViews remoteViews) {
			this.appWidgetManager = appWidgetManager;
			this.remoteViews = remoteViews;
			currentAppWidgetId = appWidgetId;
			thisContext = context;
		}		
	
		@Override
		public void run() {
			if(deletedWidget!=null && !deletedWidget.isEmpty()){
				if(deletedWidget.get(currentAppWidgetId)!=null&&deletedWidget.get(currentAppWidgetId)){
					timer.cancel();
					timer.purge();
					this.cancel();
				}			
			}
			String[] prefList = CricketWidgetConfigure.loadTitlePref(thisContext , currentAppWidgetId);
			prefScoreViews = prefList[0].split("~");
			List<String> scoreList = DataFetchUtil.loadFeed(ParserType.ANDROID_SAX);			
			String scores = "";
			if (scoreList != null && scoreList.size() > 0) {
				for (String score : scoreList) {
					if (prefScoreViews != null) {
						List<String> codes = Arrays.asList(prefScoreViews);
						String countryName = DataFetchUtil.getCountryNames(score);
						if (codes.contains(countryName)) {
							scores = scores + " | " + score;
						}
					} else {
						scores ="Sorry! No live scores available";
					}
				}
			} else {
				scores = "Sorry! No live scores available";
			}
			remoteViews.setTextViewText(R.id.tv, scores);			
			appWidgetManager.updateAppWidget(currentAppWidgetId, remoteViews);

		}
	}
}