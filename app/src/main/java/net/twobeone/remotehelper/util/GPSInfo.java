/*
 * Copyright 2014 Pierre Chabardes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.twobeone.remotehelper.util;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class GPSInfo extends Service implements LocationListener {

	private final Context mContext;

	boolean isGPSEnabled = false;

	boolean isNetworkEnabled = false;

	boolean isGetLocation = false;

	Location location;
	double lat;
	double lon;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

	private static final long MIN_TIME_BW_UPDATES = 1000 * 20 * 1;

	protected LocationManager locationManager;

	public GPSInfo(Context context) {
		this.mContext = context;
		getLocation();
	}

	public Location getLocation() {

		try {
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
			} else {
				this.isGetLocation = true;

				if (isNetworkEnabled) {
					try {
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							if (location != null) {
								lat = location.getLatitude();
								lon = location.getLongitude();
							}
						}
					} catch (SecurityException e) {
						Log.e("SSSSSS", "location network error : " + e.getLocalizedMessage());
					}
				}

				if (isGPSEnabled) {
					if (location == null) {
						try {
							locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
									MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
							if (locationManager != null) {
								location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								if (location != null) {
									lat = location.getLatitude();
									lon = location.getLongitude();
								}
							} else {
							}
						} catch (SecurityException e) {
							Log.e("SSSSSS", "location GPS error : " + e.getLocalizedMessage());
						}

					}
				}
			}

		} catch (Exception e) {
			Log.e("SSSSSS", "getLocation error : " + e.getLocalizedMessage());
		}
		return location;
	}

	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSInfo.this);
		}
	}

	public double getLatitude() {
		if (location != null) {
			lat = location.getLatitude();
		}
		return lat;
	}

	public double getLongitude() {
		if (location != null) {
			lon = location.getLongitude();
		}
		return lon;
	}

	public boolean isGetLocation() {
		return this.isGetLocation;
	}

	public void showSettingsAlert() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
		alertDialog.setMessage("위치서비스를 이용할 수 있도록 설정해 주시기 바랍니다.");

		alertDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alertDialog.show();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onLocationChanged(Location location) {
		Log.d("SSSSSS", "onLocationChanged");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("SSSSSS", "onStatusChanged");
	}

	public void onProviderEnabled(String provider) {

	}

	public void onProviderDisabled(String provider) {

	}
}