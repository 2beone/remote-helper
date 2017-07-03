package net.twobeone.remotehelper.nmap;

public class NMapPOIflagType {

    // Single POI icons
    private static final int SINGLE_POI_BASE = 0x0100;

    // Spot, Pin icons
    public static final int SPOT = SINGLE_POI_BASE + 1;
    public static final int PIN = SPOT + 1;

    // Direction POI icons: From, To
    private static final int DIRECTION_POI_BASE = 0x0200;
    public static final int FROM = DIRECTION_POI_BASE + 1;
    public static final int TO = FROM + 1;

    // end of single marker icon
    public static final int SINGLE_MARKER_END = 0x04FF;

    // Direction Number icons
    private static final int MAX_NUMBER_COUNT = 1000;
    public static final int NUMBER_BASE = 0x1000; // set NUMBER_BASE + 1 for '1' number
    public static final int NUMBER_END = NUMBER_BASE + MAX_NUMBER_COUNT;

    // Custom POI icons
    private static final int MAX_CUSTOM_COUNT = 1000;
    public static final int CUSTOM_BASE = NUMBER_END;
    public static final int CUSTOM_END = CUSTOM_BASE + MAX_CUSTOM_COUNT;

    // Clickable callout에 보여지는 화살표
    public static final int CLICKABLE_ARROW = CUSTOM_END + 1;

    public static boolean isBoundsCentered(int markerId) {
        boolean boundsCentered = false;

        switch (markerId) {
            default:
                if (markerId >= NMapPOIflagType.NUMBER_BASE && markerId < NMapPOIflagType.NUMBER_END) {
                    boundsCentered = true;
                }
                break;
        }

        return boundsCentered;
    }
}
