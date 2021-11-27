package com.example.demodatn.constant;

import java.util.Arrays;
import java.util.List;

public class ConstantDefine {
    public static final String SORT_ASC = "ASC";
    public static final String SORT_DESC = "DESC";
    public static final String TIME_DEFAULT_ZERO = "00:00:00:000";
    public static final String APPEND_LINK = "/";
    public static final String REQUEST_BODY = "RequestBody";
    public static final String CHANNEL_ID = "channelId";
    public static final String CHANNEL_FIELD = "channelId";
    public static final String GET_CHANNEL_METHOD = "getChannelId";
    public static final String SEARCH_ROLE_CMS = "CMS_%";
    public static final String TRUE = "1";
    public static final String FALSE = "0";
    public static final String DEFAULT_STORAGE_SIZE = "200000000000";
    public static final String AVATAR_FOLDER = "_AVATAR/";
    public static final String HLS_PROTOCOL = "http";
    public static final String RTMP_PROTOCOL = "rtmp";
    public static final String COMMA = ",";
    public static final String DOT_REGREX = "\\.";
    public static final String FORMAT_FILE = "mp4";
    public static final String DOT = ".";
    public static final String EXTENSION_REG = "(\\.[^.]+)$";
    public static final String SEARCH_LIKE = "%";
    public static final String EMPTY_STRING = "";
    public static final String PROGRAM_ID = "p.id";
    public static Integer FIRST_INDEX = 0;
    public static Integer SECOND_INDEX = 1;
    public static Integer THIRD_INDEX =2;
    public static String CREATE_DATE = "createdDate";
    public static String INDEX_NUM = "INDEX_NUM";
    public static final Integer LINE = 25;
    public static final Integer MARGIN_X = 120;
    public static final String IMAGE_FULLSCREEN = "fullscreen.png";
    public static final String GRAPHIC_PATH = "graphic";
    public static final Integer TITLE_BANNER_POS_X = 120;
    public static final Integer TITLE_BANNER_POS_Y = 65;
    public static final Integer SUBTITLE_BANNER_POS_Y = 115;
    public static final Float TITLE_FONT_SIZE = 60f;
    public static final Float SUBTITLE_FONT_SIZE = 40f;
    public static final String FORMAT_IMAGE = "png";
    public static final String IMAGE_DEFAULT = "default.png";
    public static final String GRAPHIC = "graphic";
    public static final String FONT_AT_BANNER = "graphicFont.otf";
    public static final Integer NOTIFY_TITLE_LIMIT = 256;
    public static final Integer NOTIFY_CONTENT_LIMIT = 500;

    public static List<String> getTypeSortList(){
        return Arrays.asList(SORT_ASC, SORT_DESC);
    }

}
