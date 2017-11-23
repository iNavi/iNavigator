package com.wustrans.bukaiei.inavigator;

import java.util.List;

import com.rockcode.har.HarDataListener;
import com.rockcode.har.HarMode;
import com.rockcode.har.HumanActivity;
import com.rockcode.har.HumanActivityRecognizer;
import com.rockcode.har.RawData;
import com.wustrans.bukaiei.inavigator.base.LocationCollector;
import com.wustrans.bukaiei.inavigator.base.UserActivityInfo;
import com.wustrans.bukaiei.inavigator.util.LogUtil;
import com.wustrans.bukaiei.inavigator.util.StrUtil;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import org.greenrobot.eventbus.EventBus;

public class HarService extends Service {

	// actions
	private static final String ACTION_START_SERVICE
			= "com.rockcode.har.demo.harservice.action.service.start";
	private static final String ACTION_STOP_SERVICE
			= "com.rockcode.har.demo.harservice.action.service.stop";
	private static final String ACTION_GET_HAR_RUNNING_STATE
			= "com.rockcode.har.demo.harservice.action.get.har.running.state";
	private static final String ACTION_START_HAR
			= "com.rockcode.har.demo.harservice.action.har.start";
	private static final String ACTION_STOP_HAR
			= "com.rockcode.har.demo.harservice.action.har.stop";

	private final static int NOTIFICATION_ID = 6666;
	private Notification mNotification;

	private HumanActivityRecognizer mHAR;

	private boolean mIsHarRunning = false;
	private long mHarStartTime;

	private LocationCollector mLocationCollector;

	public static void startService(Context context) {
		Intent i = new Intent(context, HarService.class);
		i.setAction(ACTION_START_SERVICE);
		context.startService(i);
	}

	public static void stopService(Context context) {
		Intent i = new Intent(context, HarService.class);
		i.setAction(ACTION_STOP_SERVICE);
		context.startService(i);
	}

	public static void startHar(Context context) {
		Intent i = new Intent(context, HarService.class);
		i.setAction(ACTION_START_HAR);
		context.startService(i);
	}

	public static void stopHar(Context context) {
		Intent i = new Intent(context, HarService.class);
		i.setAction(ACTION_STOP_HAR);
		context.startService(i);
	}

	public static void getHarRunningState(Context context) {
		Intent i = new Intent(context, HarService.class);
		i.setAction(ACTION_GET_HAR_RUNNING_STATE);
		context.startService(i);
	}

	public HarService() {
		LogUtil.info("HarService()");
	}

	@Override
	public void onCreate() {
		LogUtil.info("HarService - onCreate()");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.info("HarService - onStartCommand()");
		if(intent != null) {
			String action = intent.getAction();
			if(action != null) {
				switch(action) {
					case ACTION_START_SERVICE:
						handleStartService();
						break;
					case ACTION_STOP_SERVICE:
						handleStopService();
						break;
					case ACTION_START_HAR:
						handleStartHar();
						break;
					case ACTION_STOP_HAR:
						handleStopHar();
						break;
					case ACTION_GET_HAR_RUNNING_STATE:
						handleGetHarRunningState();
						break;

					default:

				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogUtil.info("HarService - onDestroy()");
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void handleStartService() {
		LogUtil.info("HarService - handleStartService()");
		if(mIsHarRunning) {
			LogUtil.info("HarService - Har is Already Running");
			sendHarIsRunning(mIsHarRunning);
		} else {


			mHarStartTime = System.currentTimeMillis();
			LogUtil.info("HarService - Start Har at: " + mHarStartTime +
					" " + StrUtil.strTime(mHarStartTime));
		}
	}

	private void handleStopService() {
		LogUtil.info("HarService - handleStopService()");
		mIsHarRunning = false;
		stopSelf();
	}

	private void handleStartHar() {
		LogUtil.info("HarService - handleStartHar()");
		if(mHAR == null) {
			LogUtil.info("HarService - init HAR");
			mHAR = new HumanActivityRecognizer(getApplicationContext(),
					true, HarMode.CLASSIFY, mHarDataListener);
		}
		if (mLocationCollector == null) {
			mLocationCollector = new LocationCollector(getApplicationContext());
		}
		if (!mLocationCollector.isRunning()) {
			mLocationCollector.start();
		}
		if(!mIsHarRunning) {
			mIsHarRunning = true;
			mHAR.start();
			// notification
			if(mNotification == null) {
				PendingIntent intent = PendingIntent.getActivity(
						getApplicationContext(), 0,
						new Intent(getApplicationContext(), MainActivity.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
				mNotification = new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.notification_icon)
						.setContentTitle(getString(R.string.notification_title))
//					.setContentText(getString(R.string.notification_text))
						.setContentIntent(intent)
						.build();
			}
			startForeground(NOTIFICATION_ID, mNotification);
		}
	}

	private void handleStopHar() {
		LogUtil.info("HarService - handleStopHar()");
		if(mIsHarRunning){
			mIsHarRunning = false;
			mHAR.stop();
			stopForeground(true);
		}
	}

	private void handleGetHarRunningState() {
		LogUtil.info("HarService - handleGetHarRunningState()");
		sendHarIsRunning(mIsHarRunning);
	}

	private void sendHarIsRunning(boolean isRunning) {
		EventBus.getDefault().post(new HarRunningStateEvent(isRunning));
	}

	HarDataListener mHarDataListener = new HarDataListener() {
		@Override
		public void onHarDataChange(HumanActivity ha) {
			// get gps
			Location location = mLocationCollector.getLocation();
			UserActivityInfo data = new UserActivityInfo(ha.mStartTime, ha.mActivity,
					location.getTime(), location.getLatitude(), location.getLongitude());
			// send event
			EventBus.getDefault().post(new HarDataUpdateEvent(data));

		}

		@Override
		public void onHarRawDataChange(List<RawData> rawdataList) {

		}
	};

	public static class HarRunningStateEvent {
		public final boolean isRunning;

		public HarRunningStateEvent(boolean isRunning) {
			this.isRunning = isRunning;
		}
	}

	public static class HarDataUpdateEvent {
		public final UserActivityInfo harData;

		public HarDataUpdateEvent(UserActivityInfo harData) {
			this.harData = harData;
		}
	}

}
