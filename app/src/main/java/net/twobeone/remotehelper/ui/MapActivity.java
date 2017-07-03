package net.twobeone.remotehelper.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.nhn.android.maps.overlay.NMapPOIdata;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.databinding.ActivityMapBinding;
import net.twobeone.remotehelper.nmap.NMapActivityParent;
import net.twobeone.remotehelper.nmap.NMapPOIflagType;
import net.twobeone.remotehelper.rest.NMap;
import net.twobeone.remotehelper.rest.NMapAPI;
import net.twobeone.remotehelper.util.GeoTrans;
import net.twobeone.remotehelper.util.GeoTransPoint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends NMapActivityParent {

    private ActivityMapBinding mBinding;

    private View.OnClickListener mOnSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            search(mMyLocationName + " " + v.getTag().toString());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        setMapView(mBinding.mapView);
        setSearchButtons(mBinding.fire, mBinding.police, mBinding.policeBox, mBinding.hospital, mBinding.pharmacy, mBinding.mart, mBinding.subway, mBinding.school);
    }

    private void setSearchButtons(ImageButton... buttons) {
        for (ImageButton button : buttons) {
            button.setOnClickListener(mOnSearchClickListener);
        }
    }

    private void search(String keyword) {
        NMapAPI.retrofit.create(NMapAPI.class).geocode(keyword).enqueue(new Callback<NMap>() {
            @Override
            public void onResponse(Call<NMap> call, Response<NMap> response) {
                showSearchResults(response.body());
            }

            @Override
            public void onFailure(Call<NMap> call, Throwable t) {

            }
        });
    }

    private void showSearchResults(NMap nMap) {
        NMapPOIdata poiData = new NMapPOIdata(nMap.getDisplay(), mMapViewerResourceProvider);
        poiData.beginPOIdata(nMap.getDisplay());
        for (NMap.Item item : nMap.getItems()) {
            GeoTransPoint katec = new GeoTransPoint(item.getMapx(), item.getMapy());
            GeoTransPoint geo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, katec);
            poiData.addPOIitem(geo.getX(), geo.getY(), item.getTitle(), NMapPOIflagType.PIN, 0);
        }
        poiData.endPOIdata();

        mOverlayManager.clearOverlays();
        mOverlayManager.createPOIdataOverlay(poiData, null);
    }
}
