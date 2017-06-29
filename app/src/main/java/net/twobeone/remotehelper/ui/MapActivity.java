package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.NMapActivityParent;

public class MapActivity extends NMapActivityParent {

    private NMapPOIdataOverlay mPOIdataOverlay;
    private NMapPOIitem mPOIitem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setMapView(R.id.map_view);

//        int markerMe = NMapPOIflagType.PIN;
//        NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
//        poiData.beginPOIdata(1);
//
//        mPOIitem = poiData.addPOIitem(null, "Touch & Drag to Move", markerMe, 0);
//        mPOIitem.setPoint(mMapController.getMapCenter());
//
//        poiData.endPOIdata();
//        mPOIdataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
//        mPOIdataOverlay.setOnStateChangeListener(this);

//        int markerId = NMapPOIflagType.PIN;
//        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
//        poiData.beginPOIdata(2);
//
//        NMapPOIitem item1 = poiData.addPOIitem(126.872772, 37.546848, "KB국민은행 염창역 지점 앞", markerId, 0); //POI아이템 설정
//        item1.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW); //마커 선택 시 표시되는 말풍선의 오른쪽 아이콘을 설정한다.
//        item1.hasRightAccessory(); //마커 선택 시 표시되는 말풍선의 오른쪽 아이콘 설정 여부를 반환한다.
//        item1.setRightButton(true); //마커 선택 시 표시되는 말풍선의 오른쪽 버튼을 설정한다.
//        item1.showRightButton(); //마커 선택 시 표시되는 말풍선의 오른쪽 버튼 설정 여부를 반환한다.
//
//        NMapPOIitem item2 = poiData.addPOIitem(126.914925, 37.528728, "국회의원회관", markerId, 0);
//        item2.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
//        item2.hasRightAccessory();
//        item2.setRightButton(true);
//        item2.showRightButton();
//
//        poiData.endPOIdata(); //POI 아이템 추가를 종료한다.
//        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null); //POI 데이터를 인자로 전달하여 NMapPOIdataOverlay 객체를 생성한다.
//        poiDataOverlay.showAllPOIdata(0); //POI 데이터가 모두 화면에 표시되도록 지도 축척 레벨 및 지도 중심을 변경한다. zoomLevel이 0이 아니면 지정한 지도 축척 레벨에서 지도 중심만 변경한다.
//        poiDataOverlay.setOnStateChangeListener(this); //POI 아이템의 선택 상태 변경 시 호출되는 콜백 인터페이스를 설정한다.
    }


    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        if (nMapError == null) {
            if (!mMapLocationManager.enableMyLocation(true)) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                finish();
            }
        }
    }

    @Override
    public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError) {
        Log.d("TEST", "onReverseGeocoderResponse:" + nMapPlacemark);
        if (nMapError == null) {

            // nMapPlacemark.latitude
//            mPOIitem.setTitle(nMapPlacemark.toString());
//            mPOIdataOverlay.deselectFocusedPOIitem();
//            mPOIdataOverlay.selectPOIitem(mPOIitem.getId(), false);
        }
    }
}
