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
import net.twobeone.remotehelper.rest.NaverMapAPI;
import net.twobeone.remotehelper.rest.model.NaverMap;
import net.twobeone.remotehelper.rest.model.NaverMapItem;
import net.twobeone.remotehelper.util.GeoTrans;
import net.twobeone.remotehelper.util.GeoTransPoint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends MapActivityParent {

    private ActivityMapBinding mBinding;

    private View.OnClickListener mOnSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String query = mMyLocationName + " " + v.getTag().toString();

            NaverMapAPI.retrofit.create(NaverMapAPI.class).geocode(mMyLocationName + " " + v.getTag().toString()).enqueue(new Callback<NaverMap>() {
                @Override
                public void onResponse(Call<NaverMap> call, Response<NaverMap> response) {
                    NaverMap naverMap = response.body();
                    Log.d("TEST", "getLastBuildDate:" + naverMap.getLastBuildDate());

                    mOverlayManager.clearOverlays();
                    testPOIdataOverlay(naverMap);
                }

                @Override
                public void onFailure(Call<NaverMap> call, Throwable t) {

                }
            });
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

    private void testPOIdataOverlay(NaverMap naverMap) {

        int markerId = NMapPOIflagType.PIN;

        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(naverMap.getDisplay(), mMapViewerResourceProvider);
        poiData.beginPOIdata(naverMap.getDisplay());
        NMapPOIitem poiItem = poiData.addPOIitem(127.0630205, 37.5091300, "Pizza 777-111", markerId, 0);
        // poiItem.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);

        for (NaverMapItem item : naverMap.getItems()) {
            GeoTransPoint in_pt = new GeoTransPoint(item.getMapx(), item.getMapy());
            GeoTransPoint tm_pt = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, in_pt);
            Double lat = tm_pt.getY() * 1E6;
            Double lng = tm_pt.getX() * 1E6;
            // GeoPoint oLatLng = new GeoPoint(lat.intValue(), lng.intValue());  // 맵뷰에서 사용가능한 좌표계
            poiData.addPOIitem(tm_pt.getX(), tm_pt.getY(), item.getTitle(), markerId, 0);
            // Log.d("TEST", "item:" + item.getTitle() + ", " + item.getMapx() + ", " + item.getMapy() + "..." + tm_pt.getX() + ", " + tm_pt.getY());
        }

        // poiData.addPOIitem(127.061, 37.51, "Pizza 123-456", markerId, 0);
        poiData.endPOIdata();

        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        // select an item
        // poiDataOverlay.selectPOIitem(0, true); 해당 위치로 이동???

        // show all POI data
        //poiDataOverlay.showAllPOIdata(0);
    }

    private void startMyLocation() {
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
        }
    }
}
