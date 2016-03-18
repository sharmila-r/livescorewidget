/**
 * CricketScoreWidget
 */
package com.sentiense.crickwidget.main;

import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Sharmila
 * Dec 28, 2011
 * 
 */
public class CricketBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // For our example, we'll also update all of the widgets when the timezone
        // changes, or the user or network sets the time.
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                || action.equals(Intent.ACTION_TIME_CHANGED)) {
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            ArrayList<Integer> appWidgetIds = new ArrayList<Integer>();
            ArrayList<String> texts = new ArrayList<String>();

            CricketWidgetConfigure.loadAllTitlePrefs(context, appWidgetIds, texts);
            CricketScoreWidget cricketScoreWidget = new CricketScoreWidget();
            final int N = appWidgetIds.size();
            for (int i=0; i<N; i++) {
            	cricketScoreWidget.updateAppWidgetScore(context, gm, appWidgetIds.get(i), texts.get(i));
            }
        }
    }

}
