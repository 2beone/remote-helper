package net.twobeone.remotehelper.nmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ListView;

import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import net.twobeone.remotehelper.R;

public class NMapViewerResourceProvider extends NMapResourceProvider implements NMapCalloutCustomOldOverlay.ResourceProvider {

    private static final Bitmap.Config BITMAP_CONFIG_DEFAULT = Bitmap.Config.ARGB_8888;

    private static final int POI_FONT_COLOR_NUMBER = 0xFF909090;
    private static final float POI_FONT_SIZE_NUMBER = 10.0F;

    private static final int POI_FONT_COLOR_ALPHABET = 0xFFFFFFFF;
    private static final Typeface POI_FONT_TYPEFACE = null;//Typeface.DEFAULT_BOLD;

    private static final int CALLOUT_TEXT_COLOR_NORMAL = 0xFFFFFFFF;
    private static final int CALLOUT_TEXT_COLOR_PRESSED = 0xFF9CA1AA;
    private static final int CALLOUT_TEXT_COLOR_SELECTED = 0xFFFFFFFF;
    private static final int CALLOUT_TEXT_COLOR_FOCUSED = 0xFFFFFFFF;

    private final Rect mTempRect = new Rect();
    private final Paint mTextPaint = new Paint();

    public NMapViewerResourceProvider(Context context) {
        super(context);

        mTextPaint.setAntiAlias(true);
    }

    public Bitmap getBitmap(Drawable marker) {
        Bitmap bitmap = null;

        if (marker != null) {
            int width = marker.getIntrinsicWidth();
            int height = marker.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(width, height, BITMAP_CONFIG_DEFAULT);

            marker.setBounds(0, 0, width, height);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(0x00000000);

            marker.draw(canvas);
        }

        return bitmap;
    }

    @Override
    public Drawable getDrawableForInfoLayer(NMapOverlayItem item) {
        return null;
    }

    private class ResourceIdsOnMap {

        int markerId;
        int resourceId;
        int resourceIdFocused;

        ResourceIdsOnMap(int markerId, int resourceId, int resourceIdFocused) {
            this.markerId = markerId;
            this.resourceId = resourceId;
            this.resourceIdFocused = resourceIdFocused;
        }
    }

    // Resource Ids for single icons
    private final ResourceIdsOnMap mResourceIdsForMarkerOnMap[] = {
            // Spot, Pin icons
            new ResourceIdsOnMap(NMapPOIflagType.PIN, R.drawable.ic_pin_01, R.drawable.ic_pin_02),
            new ResourceIdsOnMap(NMapPOIflagType.SPOT, R.drawable.ic_pin_01, R.drawable.ic_pin_02),

            // Direction POI icons: From, To
            new ResourceIdsOnMap(NMapPOIflagType.FROM, R.drawable.ic_map_start, R.drawable.ic_map_start_over),
            new ResourceIdsOnMap(NMapPOIflagType.TO, R.drawable.ic_map_arrive, R.drawable.ic_map_arrive_over),
    };

    @Override
    protected int findResourceIdForMarker(int markerId, boolean focused) {
        int resourceId = 0;
        if (markerId < NMapPOIflagType.SINGLE_MARKER_END) {
            resourceId = getResourceIdOnMapView(markerId, focused, mResourceIdsForMarkerOnMap);
            if (resourceId > 0) {
                return resourceId;
            }
        }
        return resourceId;
    }

    /**
     * Set bounds for this marker depending on its shape.
     */
    @Override
    protected void setBounds(Drawable marker, int markerId, NMapOverlayItem item) {

        // check shape of the marker to set bounds correctly.
        if (NMapPOIflagType.isBoundsCentered(markerId)) {
            if (marker.getBounds().isEmpty()) {
                NMapOverlayItem.boundCenter(marker);
            }

            if (null != item) {
                item.setAnchorRatio(0.5f, 0.5f);
            }

        } else {
            if (marker.getBounds().isEmpty()) {
                NMapOverlayItem.boundCenterBottom(marker);
            }

            if (null != item) {
                item.setAnchorRatio(0.5f, 1.0f);
            }
        }
    }

    @Override
    public Drawable[] getLocationDot() {
        Drawable[] drawable = new Drawable[2];

        drawable[0] = mContext.getResources().getDrawable(R.drawable.pubtrans_ic_mylocation_off);
        drawable[1] = mContext.getResources().getDrawable(R.drawable.pubtrans_ic_mylocation_on);

        for (int i = 0; i < drawable.length; i++) {
            int w = drawable[i].getIntrinsicWidth() / 2;
            int h = drawable[i].getIntrinsicHeight() / 2;

            drawable[i].setBounds(-w, -h, w, h);
        }

        return drawable;
    }

    @Override
    public Drawable getDirectionArrow() {
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_angle);
        if (drawable != null) {
            int w = drawable.getIntrinsicWidth() / 2;
            int h = drawable.getIntrinsicHeight() / 2;
            drawable.setBounds(-w, -h, w, h);
        }
        return drawable;
    }

    public Drawable getDrawableWithNumber(int resourceId, String strNumber, float offsetY, int fontColor, float fontSize) {
        Bitmap textBitmap = getBitmapWithText(resourceId, strNumber, fontColor, fontSize, offsetY);
        Drawable marker = new BitmapDrawable(mContext.getResources(), textBitmap);
        if (marker != null) {
            NMapOverlayItem.boundCenter(marker);
        }
        return marker;
    }

    @Override
    protected Drawable getDrawableForMarker(int markerId, boolean focused, NMapOverlayItem item) {
        Drawable drawable = null;
        if (markerId >= NMapPOIflagType.NUMBER_BASE && markerId < NMapPOIflagType.NUMBER_END) { // Direction Number icons
            int resourceId = (focused) ? R.drawable.ic_map_no_02 : R.drawable.ic_map_no_01;
            int fontColor = (focused) ? POI_FONT_COLOR_ALPHABET : POI_FONT_COLOR_NUMBER;
            String strNumber = String.valueOf(markerId - NMapPOIflagType.NUMBER_BASE);
            drawable = getDrawableWithNumber(resourceId, strNumber, 0.0F, fontColor, POI_FONT_SIZE_NUMBER);
        } else if (markerId >= NMapPOIflagType.CUSTOM_BASE && markerId < NMapPOIflagType.CUSTOM_END) { // Custom POI icons

        }
        return drawable;
    }

    private Bitmap getBitmapWithText(int resourceId, String strNumber, int fontColor, float fontSize, float offsetY) {
        Bitmap bitmapBackground = BitmapFactory.decodeResource(mContext.getResources(), resourceId);

        int width = bitmapBackground.getWidth();
        int height = bitmapBackground.getHeight();

        Bitmap textBitmap = Bitmap.createBitmap(width, height, BITMAP_CONFIG_DEFAULT);
        Canvas canvas = new Canvas(textBitmap);
        canvas.drawBitmap(bitmapBackground, 0, 0, null);

        // set font style
        mTextPaint.setColor(fontColor);
        // set font size
        mTextPaint.setTextSize(fontSize * mScaleFactor);
        // set font type
        if (POI_FONT_TYPEFACE != null) {
            mTextPaint.setTypeface(POI_FONT_TYPEFACE);
        }

        mTextPaint.getTextBounds(strNumber, 0, strNumber.length(), mTempRect);
        float offsetX = (width - mTempRect.width()) / 2 - mTempRect.left;
        if (offsetY == 0.0F) {
            offsetY = (height - mTempRect.height()) / 2 + mTempRect.height();
        } else {
            offsetY = offsetY * mScaleFactor + mTempRect.height();
        }
        canvas.drawText(strNumber, offsetX, offsetY, mTextPaint);
        return textBitmap;
    }

    @Override
    public Drawable getCalloutBackground(NMapOverlayItem item) {
        if (item instanceof NMapPOIitem) {
            NMapPOIitem poiItem = (NMapPOIitem) item;
            if (poiItem.showRightButton()) {
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.bg_speech);
                return drawable;
            }
        }
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.pin_ballon_bg);
        return drawable;
    }

    @Override
    public String getCalloutRightButtonText(NMapOverlayItem item) {
        if (item instanceof NMapPOIitem) {
            NMapPOIitem poiItem = (NMapPOIitem) item;
            if (poiItem.showRightButton()) {
                return "완료";
            }
        }

        return null;
    }

    @Override
    public Drawable[] getCalloutRightButton(NMapOverlayItem item) {
        if (item instanceof NMapPOIitem) {
            NMapPOIitem poiItem = (NMapPOIitem) item;

            if (poiItem.showRightButton()) {
                Drawable[] drawable = new Drawable[3];

                drawable[0] = mContext.getResources().getDrawable(R.drawable.btn_green_normal);
                drawable[1] = mContext.getResources().getDrawable(R.drawable.btn_green_pressed);
                drawable[2] = mContext.getResources().getDrawable(R.drawable.btn_green_highlight);

                return drawable;
            }
        }

        return null;
    }

    @Override
    public Drawable[] getCalloutRightAccessory(NMapOverlayItem item) {
        if (item instanceof NMapPOIitem) {
            NMapPOIitem poiItem = (NMapPOIitem) item;

            if (poiItem.hasRightAccessory() && (poiItem.getRightAccessoryId() > 0)) {
                Drawable[] drawable = new Drawable[3];

                switch (poiItem.getRightAccessoryId()) {
                    case NMapPOIflagType.CLICKABLE_ARROW:
                        drawable[0] = mContext.getResources().getDrawable(R.drawable.pin_ballon_arrow);
                        drawable[1] = mContext.getResources().getDrawable(R.drawable.pin_ballon_on_arrow);
                        drawable[2] = mContext.getResources().getDrawable(R.drawable.pin_ballon_on_arrow);
                        break;
                }

                return drawable;
            }
        }

        return null;
    }

    @Override
    public int[] getCalloutTextColors(NMapOverlayItem item) {
        int[] colors = new int[4];
        colors[0] = CALLOUT_TEXT_COLOR_NORMAL;
        colors[1] = CALLOUT_TEXT_COLOR_PRESSED;
        colors[2] = CALLOUT_TEXT_COLOR_SELECTED;
        colors[3] = CALLOUT_TEXT_COLOR_FOCUSED;
        return colors;
    }

    @Override
    public int getParentLayoutIdForOverlappedListView() {
        return 0;
    }

    @Override
    public int getOverlappedListViewId() {
        return 0;
    }

    @Override
    public int getLayoutIdForOverlappedListView() {
        return 0;
    }

    @Override
    public int getListItemLayoutIdForOverlappedListView() {
        return 0;
    }

    @Override
    public int getListItemTextViewId() {
        return 0;
    }

    @Override
    public int getListItemTailTextViewId() {
        return 0;
    }

    @Override
    public int getListItemImageViewId() {
        return 0;
    }

    @Override
    public int getListItemDividerId() {
        return 0;
    }

    @Override
    public void setOverlappedListViewLayout(ListView listView, int itemCount, int width, int height) {

    }

    @Override
    public void setOverlappedItemResource(NMapPOIitem poiItem, ImageView imageView) {

    }

    private int getResourceIdOnMapView(int markerId, boolean focused, ResourceIdsOnMap resourceIdsArray[]) {
        int resourceId = 0;
        for (ResourceIdsOnMap resourceIds : resourceIdsArray) {
            if (resourceIds.markerId == markerId) {
                resourceId = (focused) ? resourceIds.resourceIdFocused : resourceIds.resourceId;
                break;
            }
        }
        return resourceId;
    }
}
