/**
    Copyright 2010 Paschar LLC.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 * @author Peter Ma
 * Twitter: @Nyceane
 * Site: http://www.paschar.com
 */

package com.yourapp.utility;

import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.yourapp.R;
import com.yourapp.helpers.AlertBox;


public class TwitterIntegrator {
	private static final String TWITTER_PACKAGE = "com.twitter.android";
	private static final String TWITDROID_PACKAGE = "com.twidroid";
	
	private static final String TWITTER_MARKET_URI = "http://market.android.com/search?q=pname:" + TWITTER_PACKAGE;
	private static final String TWITDROID_MARKET_URI = "http://market.android.com/search?q=pname:" + TWITDROID_PACKAGE;

	private static final String TWITTER_TWEET_ACTIVITY = "com.twitter.android.PostActivity";
	private static final String TWITDROID_TWEET_ACTIVITY = "com.twidroid.SendTweet";
	private static final String TWITDROID_TWEET_TYPE = "com.twidroid.extra.MESSAGE";
	
	private final Context _context;

	/**
	 * Constructor
	 * @param context The current context.
	 */
	public TwitterIntegrator(Context context)
	{
		_context = context;
	}
	
	/**
	 * Tweet the message to Twitter app or the web
	 * @param message message Being Tweeted
	 */
	public void TweetStatusMessage(String message)
	{
		boolean isSuccess = false;
		if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR)
		{
			//Higher than 2.0, which is 2.1 or above
			isSuccess = tweetThroughTwitter(message);
		}
		
		if(!isSuccess)
		{
			isSuccess = TweetThroughTwitDroid(message);
		}
		
		if(!isSuccess)
		{
			//Any Icon would work
			Drawable picture = _context.getResources().getDrawable(R.drawable.twitter_icon);
			
			//Your own alert box method
			AlertBox.DoubleButtonAlertBox(_context, 
											picture, 
											_context.getString(R.string.twitter_fail_title), 
											_context.getString(R.string.twitter_fail_message),
											_context.getString(R.string.twitter_download), 
											_context.getString(R.string.twitter_cancel), 
											new DialogInterface.OnClickListener() {
									             public void onClick(DialogInterface dialog, int whichButton) {
										     		//All Failed, redirect user to download twitter client			
										     		String url = "";
										     		if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR)
										     		{
										     			url = TWITTER_MARKET_URI;
										     		}
										     		else
										     		{
										     			url = TWITDROID_MARKET_URI;
										     		}
										     		Uri uri = Uri.parse(url);
										     		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
										     		_context.startActivity(intent); 
									             }
									        },
									        new DialogInterface.OnClickListener() {
									             public void onClick(DialogInterface dialog, int whichButton) {
									            	 
									             }
									        }
			);
		}
	}
	
	/**
	 * Tweet through official twitter app, this is for android 2.1 or above
	 * @param message message Being Tweeted
	 * @return True if the user tweeted through official twitter app
	 */
	@SuppressWarnings("unchecked")
	public boolean tweetThroughTwitter(String message) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, message);
			intent.setType("text/plain");
			final PackageManager pm = _context.getPackageManager();
			final List activityList = pm.queryIntentActivities(intent, 0);
			int len = activityList.size();
			for (int i = 0; i < len; i++) {
				final ResolveInfo app = (ResolveInfo) activityList.get(i);
				if (TWITTER_TWEET_ACTIVITY.equals(app.activityInfo.name)) {
					final ActivityInfo activity = app.activityInfo;
					final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
					intent = new Intent(Intent.ACTION_SEND);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					intent.setComponent(name);
					intent.putExtra(Intent.EXTRA_TEXT, message);
					_context.startActivity(intent);
					return true;
				}
			}
		}catch (ActivityNotFoundException e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Tweet through official twitDroid, this is for android below 2.1
	 * @param message message Being Tweeted
	 * @return True if the user has TwitDroid and posted through TwitDroid
	 */
	public boolean TweetThroughTwitDroid(String message) {
		Intent intent = new Intent(TWITDROID_TWEET_ACTIVITY);
		intent.putExtra(TWITDROID_TWEET_TYPE, message);
		try {
			_context.startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}
}
