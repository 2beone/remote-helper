package net.twobeone.remotehelper.ui;

import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;

import net.twobeone.remotehelper.Constants;
import net.twobeone.remotehelper.map.NMapViewerResourceProvider;

public class MapActivityParent extends NMapActivity implements NMapView.OnMapStateChangeListener, NMapLocationManager.OnLocationChangeListener {

    protected NMapView mMapView;
    protected NMapController mMapController;
    protected NMapOverlayManager mOverlayManager;
    protected NMapMyLocationOverlay mMyLocationOverlay;
    protected NMapLocationManager mMapLocationManager;
    protected NMapViewerResourceProvider mMapViewerResourceProvider;

    protected void setMapView(NMapView mapView) {

        mMapView = mapView;
        mMapView.setClientId(Constants.NAVER_MAP_CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
        mMapView.setScalingFactor(2); // 글씨크기 확대
        mMapView.setOnMapStateChangeListener(this);
        mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);

        mMapController = mMapView.getMapController();
        mMapController.setZoomLevel(12);

        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(this);

        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, null);
    }

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {

    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {

    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

    }

    @Override
    public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
        if (mMapController != null) {
            mMapController.animateTo(nGeoPoint);
        }
        return true;
    }

    @Override
    public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {
        Toast.makeText(this, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {

    }

    private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

        @Override
        public boolean isLocationTracking() {
            if (mMapLocationManager != null) {
                if (mMapLocationManager.isMyLocationEnabled()) {
                    return mMapLocationManager.isMyLocationFixed();
                }
            }
            return false;
        }
    };
}
