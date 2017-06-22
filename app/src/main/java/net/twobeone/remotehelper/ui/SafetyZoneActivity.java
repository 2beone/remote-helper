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

package net.twobeone.remotehelper.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import daum.android.map.openapi.search.Item;
import daum.android.map.openapi.search.OnFinishSearchListener;
import daum.android.map.openapi.search.Searcher;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.service.GPSInfo;

import java.util.HashMap;
import java.util.List;

public class SafetyZoneActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, MapView.CurrentLocationEventListener {

    private ImageButton back;
    private TextView title_text;
    private String API_KEY = "61026b71ae7bd08e62c008367f213d25";
    private MapView mapView;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
    private ImageButton police;
    private ImageButton fire;
    private ImageButton police_box;
    private ImageButton hospital;
    private ImageButton pharmacy;
    private ImageButton mart;
    private ImageButton subway;
    private ImageButton school;
    private ImageButton search_poi;
    private TextView point_name;
    private TextView distance;
    private GPSInfo gps;
    private double lati;
    private double longi;
    private double poilat;
    private double poilong;
    private double distanceKiloMeter;
    private MapPoint[] mPolyline;
    private String keyword = "";
    private ImageView point_icon;
    private static final int SEARCH_POI_ACTIVITY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_zone);

        Toolbar toobar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toobar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_safety_zone);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        gps = new GPSInfo(SafetyZoneActivity.this);
        // GPS ������� ��������
        if (gps.isGetLocation()) {
            lati = gps.getLatitude();
            longi = gps.getLongitude();
        } else {
            // GPS �� ����Ҽ� �����Ƿ�
            lati = 0;
            longi = 0;
        }

        mapView = new MapView(this);
        mapView.setDaumMapApiKey(API_KEY);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);

        mapView.zoomIn(true);
        mapView.zoomOut(true);

        police = (ImageButton) findViewById(R.id.police);
        fire = (ImageButton) findViewById(R.id.fire);
        police_box = (ImageButton) findViewById(R.id.police_box);
        hospital = (ImageButton) findViewById(R.id.btn_hospital);
        pharmacy = (ImageButton) findViewById(R.id.pharmacy);
        mart = (ImageButton) findViewById(R.id.mart);
        subway = (ImageButton) findViewById(R.id.subway);
        school = (ImageButton) findViewById(R.id.school);
//        search_poi = (ImageButton) findViewById(R.id.btnsearch);

//        search_poi.setVisibility(search_poi.VISIBLE);
//        search_poi.setOnClickListener(mClickListener);

        police.setOnClickListener(mClickListener);
        fire.setOnClickListener(mClickListener);
        police_box.setOnClickListener(mClickListener);
        hospital.setOnClickListener(mClickListener);
        pharmacy.setOnClickListener(mClickListener);
        mart.setOnClickListener(mClickListener);
        subway.setOnClickListener(mClickListener);
        school.setOnClickListener(mClickListener);

        police.setSelected(false);
        fire.setSelected(false);
        police_box.setSelected(false);
        hospital.setSelected(false);
        pharmacy.setSelected(false);
        mart.setSelected(false);
        subway.setSelected(false);
        school.setSelected(false);

        point_name = (TextView) findViewById(R.id.point_name);
        distance = (TextView) findViewById(R.id.distance);
        point_icon = (ImageView) findViewById(R.id.point_icon);

//        title_text.setText("��������");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            String query = "";

            police.setSelected(false);
            fire.setSelected(false);
            police_box.setSelected(false);
            hospital.setSelected(false);
            pharmacy.setSelected(false);
            mart.setSelected(false);
            subway.setSelected(false);
            school.setSelected(false);

            switch (v.getId()) {
                case R.id.police:
                    keyword = "police";
                    query = "������";
                    police.setSelected(true);
                    break;
                case R.id.fire:
                    fire.setSelected(true);
                    query = "�ҹ漭";
                    keyword = "fire";
                    break;
                case R.id.police_box:
                    query = "������";
                    keyword = "police_box";
                    police_box.setSelected(true);
                    break;
                case R.id.btn_hospital:
                    query = "����";
                    keyword = "hospital";
                    hospital.setSelected(true);
                    break;
                case R.id.pharmacy:
                    query = "�౹";
                    keyword = "pharmacy";
                    pharmacy.setSelected(true);
                    break;
                case R.id.mart:
                    query = "������";
                    keyword = "mart";
                    mart.setSelected(true);
                    break;
                case R.id.subway:
                    query = "����ö";
                    keyword = "subway";
                    subway.setSelected(true);
                    break;
                case R.id.school:
                    query = "�б�";
                    keyword = "school";
                    school.setSelected(true);
                    break;
//                case R.id.btnsearch:
//                    keyword = "default";
//                    Intent i = new Intent(WebRTC_MapView.this, POI_Search.class);
//                    startActivityForResult(i, SEARCH_POI_ACTIVITY);
//                    return;
            }
            //
            MapPolyline existingPolyline = mapView.findPolylineByTag(2000);
            if (existingPolyline != null) {
                mapView.removePolyline(existingPolyline);
            }
            point_name.setText("");
            distance.setText("");
            point_icon.setVisibility(point_icon.INVISIBLE);

            MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
            double latitude = geoCoordinate.latitude; // ����
            double longitude = geoCoordinate.longitude; // �浵
            int radius = 1000; // �߽� ��ǥ������ �ݰ�Ÿ�. Ư�� ������ �߽����� �˻��Ϸ��� �� ��� ���.
            // meter ���� (0 ~ 10000)
            int page = 1; // ������ ��ȣ (1 ~ 3). ���������� 15��

            Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
            searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, API_KEY,
                    new OnFinishSearchListener() {
                        @Override
                        public void onSuccess(List<Item> itemList) {
                            mapView.removeAllPOIItems(); // ���� �˻� ��� ����
                            showResult(itemList); // �˻� ��� ������
                        }

                        @Override
                        public void onFail() {
                            // TODO Auto-generated method stub
                        }
                    });
        }
    };

    public void showResult(List<Item> itemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            if (keyword.equals("police")) {
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.icon_police);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.icon_police);
            } else if (keyword.equals("fire")) {
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.icon_fire);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.icon_fire);
            } else if (keyword.equals("police_box")) {
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.icon_police_box);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.icon_police_box);
            } else if (keyword.equals("hospital")) {
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.icon_hospital);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.icon_hospital);
            } else if (keyword.equals("pharmacy")) {
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.icon_pharmacy);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.icon_pharmacy);
            } else if (keyword.equals("mart")) {
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.icon_mart);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.icon_mart);
            } else if (keyword.equals("subway")) {
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.icon_subway);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.icon_subway);
            } else if (keyword.equals("school")) {
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.icon_school);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.icon_school);
            } else if (keyword.equals("default")) {
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.poi_dot);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.poi_dot);
            }
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
        }
    }

    @Override
    public void onMapViewCenterPointMoved(MapView arg0, MapPoint arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapViewDoubleTapped(MapView arg0, MapPoint arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapViewDragEnded(MapView arg0, MapPoint arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapViewDragStarted(MapView arg0, MapPoint arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapViewInitialized(MapView arg0) {
        // TODO Auto-generated method stub

        mapView.removeAllPOIItems(); // ���� �˻� ��� ����
        mapView.removeAllPolylines();
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lati, longi), true);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        mapView.setCurrentLocationRadius(1); // meter
        mapView.setCurrentLocationRadiusFillColor(Color.argb(77, 255, 255, 0));
        mapView.setCurrentLocationRadiusStrokeColor(Color.argb(77, 255, 165, 0));

        MapPOIItem.ImageOffset trackingImageAnchorPointOffset = new MapPOIItem.ImageOffset(33, 0); // ���ϴ�(0,0)
        // ����
        // ��Ŀ����Ʈ
        // ������
        MapPOIItem.ImageOffset offImageAnchorPointOffset = new MapPOIItem.ImageOffset(0, 13);

        mapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.icon_now, trackingImageAnchorPointOffset);
        mapView.setCustomCurrentLocationMarkerImage(R.drawable.icon_now, offImageAnchorPointOffset);

    }

    @Override
    public void onMapViewLongPressed(MapView arg0, MapPoint arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapViewMoveFinished(MapView arg0, MapPoint arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapViewSingleTapped(MapView arg0, MapPoint arg1) {
        // TODO Auto-generated method stub
        MapPolyline existingPolyline = mapView.findPolylineByTag(2000);
        if (existingPolyline != null) {
            mapView.removePolyline(existingPolyline);
        }

        point_name.setText("");
        distance.setText("");
        point_icon.setVisibility(point_icon.INVISIBLE);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1, MapPOIItem.CalloutBalloonButtonType arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDraggablePOIItemMoved(MapView arg0, MapPOIItem arg1, MapPoint arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPOIItemSelected(MapView arg0, MapPOIItem arg1) {
        // TODO Auto-generated method stub

        MapPolyline existingPolyline = mapView.findPolylineByTag(2000);
        if (existingPolyline != null) {
            mapView.removePolyline(existingPolyline);
        }

        poilat = 0;
        poilong = 0;

        point_name.setText(arg1.getItemName());
        point_icon.setVisibility(point_icon.VISIBLE);

        poilat = arg1.getMapPoint().getMapPointGeoCoord().latitude;
        poilong = arg1.getMapPoint().getMapPointGeoCoord().longitude;

        mPolyline = new MapPoint[] { MapPoint.mapPointWithGeoCoord(lati, longi),
                MapPoint.mapPointWithGeoCoord(poilat, poilong) };

        MapPolyline polyline2 = new MapPolyline(21);
        polyline2.setTag(2000);
        polyline2.setLineColor(Color.argb(128, 0, 0, 255));
        polyline2.addPoints(mPolyline);
        mapView.addPolyline(polyline2);

        distanceKiloMeter = Math.round(distance(lati, longi, poilat, poilong, "meter"));

        distance.setText(String.valueOf((int) distanceKiloMeter) + "m");
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView arg0, float arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCurrentLocationUpdate(MapView arg0, MapPoint arg1, float arg2) {
        // TODO Auto-generated method stub
        Log.d("SSSSSS", "onCurrentLocationUpdate");
        MapPolyline existingPolyline = mapView.findPolylineByTag(2000);
        if (existingPolyline != null) {
            mapView.removePolyline(existingPolyline);

            lati = arg1.getMapPointGeoCoord().latitude;
            longi = arg1.getMapPointGeoCoord().longitude;

            mPolyline = new MapPoint[] { MapPoint.mapPointWithGeoCoord(lati, longi),
                    MapPoint.mapPointWithGeoCoord(poilat, poilong) };

            MapPolyline polyline2 = new MapPolyline(21);
            polyline2.setTag(2000);
            polyline2.setLineColor(Color.argb(128, 0, 0, 255));
            polyline2.addPoints(mPolyline);
            mapView.addPolyline(polyline2);

            distanceKiloMeter = Math.round(distance(lati, longi, poilat, poilong, "meter"));

            distance.setText(String.valueOf((int) distanceKiloMeter) + "m");
        }
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView arg0) {
        // TODO Auto-generated method stub

    }

    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }

        return (dist);
    }

    // This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        String getData = "";

        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case SEARCH_POI_ACTIVITY:
                getData = data.getExtras().getString("data");

                MapPolyline existingPolyline = mapView.findPolylineByTag(2000);
                if (existingPolyline != null) {
                    mapView.removePolyline(existingPolyline);
                }
                point_name.setText("");
                distance.setText("");
                point_icon.setVisibility(point_icon.INVISIBLE);

                MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude; // ����
                double longitude = geoCoordinate.longitude; // �浵
                int radius = 1000; // �߽� ��ǥ������ �ݰ�Ÿ�. Ư�� ������ �߽����� �˻��Ϸ��� �� ��� ���.
                // meter ���� (0 ~ 10000)
                int page = 1; // ������ ��ȣ (1 ~ 3). ���������� 15��

                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
                searcher.searchKeyword(getApplicationContext(), getData, latitude, longitude, radius, page, API_KEY,
                        new OnFinishSearchListener() {
                            @Override
                            public void onSuccess(List<Item> itemList) {
                                mapView.removeAllPOIItems(); // ���� �˻� ��� ����
                                showResult(itemList); // �˻� ��� ������
                            }

                            @Override
                            public void onFail() {
                                // TODO Auto-generated method stub
                            }
                        });

                break;
        }
    }
}
