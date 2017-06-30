package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.databinding.ActivityMapBinding;
import net.twobeone.remotehelper.map.NMapPOIflagType;

public class MapActivity extends MapActivityParent {

    private ActivityMapBinding mBinding;

    private View.OnClickListener mOnSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            Log.d("TEST", v.getTag().toString());
            Log.d("TEST", "getMyLocation:" + mMapLocationManager.getMyLocation());

            mOverlayManager.clearOverlays();
            testPOIdataOverlay();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        setMapView(mBinding.mapView);
        setOnClickListener(mBinding.fire, mBinding.police, mBinding.policeBox, mBinding.hospital, mBinding.pharmacy, mBinding.mart, mBinding.subway, mBinding.school);
    }

    private void setOnClickListener(ImageButton... buttons) {
        for (ImageButton button : buttons) {
            button.setOnClickListener(mOnSearchClickListener);
        }
    }

    @Override
    protected void onStop() {
        stopMyLocation();
        super.onStop();
    }

    private void testPOIdataOverlay() {

        // Markers for POI item
        int markerId = NMapPOIflagType.PIN;

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poiData.beginPOIdata(2);
        NMapPOIitem item = poiData.addPOIitem(127.0630205, 37.5091300, "Pizza 777-111", markerId, 0);
        item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
        poiData.addPOIitem(127.061, 37.51, "Pizza 123-456", markerId, 0);
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        // select an item
        poiDataOverlay.selectPOIitem(0, true);

        // show all POI data
        //poiDataOverlay.showAllPOIdata(0);
    }

    private void startMyLocation() {

        // TODO
        // 한번만 호출되나??
        Log.d("TEST", "startMyLocation");

        if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
            mOverlayManager.addOverlay(mMyLocationOverlay);
        }
        boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
        if (!isMyLocationEnabled) {
            Toast.makeText(MapActivity.this, "Please enable a My Location source in system settings", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();
        }
    }

    @Override
    public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
        Toast.makeText(MapActivity.this, "알 수 없는 위치입니다.", Toast.LENGTH_LONG).show();
        stopMyLocation();
    }

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        super.onMapInitHandler(nMapView, nMapError);
        if (nMapError == null) {
            startMyLocation();
            // restoreInstanceState();
        }
    }
}
