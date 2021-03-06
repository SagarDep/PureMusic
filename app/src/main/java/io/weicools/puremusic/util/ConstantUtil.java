package io.weicools.puremusic.util;

/**
 * Author: weicools
 * Time: 2017/10/30 下午12:21
 */

public class ConstantUtil {
    public static final int REQUEST_WRITE_SETTINGS = 0;
    public static final int REQUEST_ALBUM = 1;
    public static final int REQUEST_CORP = 2;

    /* action */
    public static final String ACTION_MEDIA_PLAY_PAUSE = "io.weicools.puremusic.ACTION_MEDIA_PLAY_PAUSE";
    public static final String ACTION_MEDIA_NEXT = "io.weicools.puremusic.ACTION_MEDIA_NEXT";
    public static final String ACTION_MEDIA_PREVIOUS = "io.weicools.puremusic.ACTION_MEDIA_PREVIOUS";
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";

    /* extra */
    public static final String EXTRA_NOTIFICATION = "io.weicools.puremusic.notification";
    public static final String MUSIC_LIST_TYPE = "music_list_type";
    public static final String TING_UID = "ting_uid";
    public static final String MUSIC = "music";

    /* key */
    public static final String VIEW_PAGER_INDEX = "view_pager_index";
    public static final String LOCAL_MUSIC_POSITION = "local_music_position";
    public static final String LOCAL_MUSIC_OFFSET = "local_music_offset";
    public static final String PLAYLIST_POSITION = "playlist_position";
    public static final String PLAYLIST_OFFSET = "playlist_offset";

    public static final String ACTION_STOP = "io.weicools.puremusic.ACTION_STOP";
    public static final String SCAN_MUSIC = "scan_music";

    public static String APP_URL = "https://github.com/lecymeng/PureMusic/releases";
    public static String DESIGNED_BY = "Designed by Weicools in China";
    public static String SHARE_CONTENT = "A beautiful app designed with Material Design:\n" + APP_URL + "\n- " + DESIGNED_BY;
    public static String EMAIL = "zhang1570682285@gmail.com";
    public static String GIT_HUB = "https://github.com/lecymeng/PureMusic";
}
