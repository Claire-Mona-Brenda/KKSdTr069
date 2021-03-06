package com.konka.kksdtr069.model;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.konka.kksdtr069.util.LogUtil;
import com.konka.kksdtr069.util.Utils;

public class Tr069Provider extends ContentProvider {

    private static final String AUTHORITY = "tr069";
    private static final String URI_AUTH = "content://tr069/datamodel";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_VALUE = "value";
    private static final String SELECTION = "name=?";
    private static final int SINGLE_ROW = 101;
    private static final int ALL_ROW = 102;
    private static final String TAG = "TR069_DB";

    private static UriMatcher sUriMatcher;
    private SQLiteDatabase db;

    private String[][] props = {
            {"ro.mac", "Device.LAN.MACAddress"},
            {"persist.sys.konka.stbinfo.hw", "Device.DeviceInfo.HardwareVersion"},
            {"ro.build.version.incremental", "Device.DeviceInfo.SoftwareVersion"},
            {"ro.product.model", "Device.DeviceInfo.ModelName"},
            {"ro.serialno", "Device.X_CMCC_OTV.STBInfo.STBID"},
            {"ro.product.model", "Device.DeviceInfo.ProductClass"},
            {"ro.serialno", "Device.DeviceInfo.SerialNumber"}};

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "datamodel", ALL_ROW);
        sUriMatcher.addURI(AUTHORITY, "datamodel/*", SINGLE_ROW);
    }

    public boolean onCreate() {
        LogUtil.i(TAG, "provider onCreate");
        db = new DBHelper(getContext()).getWritableDatabase();
        initData();
        return true;
    }

    private void initData() {
        LogUtil.d(TAG, "initData");
        int ret = -100;
        String prop, dname;

        for (String[] s : props) {
            prop = s[0];
            dname = s[1];

            String mac = Utils.getProperty(prop);
            ContentValues cv = new ContentValues();
            cv.put("value", mac);
            ret = update(Uri.parse(URI_AUTH), cv, "name=?", new String[]{dname});
            LogUtil.i(TAG, "update:{" + prop + "} ret=" + ret);
        }
        updateDb("Device.ManagementServer.ConnectionRequestUsername", "cpe");
        updateDb("Device.ManagementServer.ConnectionRequestPassword", "cpe");

        updateDb("Device.ManagementServer.PeriodicInformEnable", true);
        updateDb("Device.ManagementServer.PeriodicInformInterval", 2);
        //updateDb("Device.ManagementServer.PeriodicInformTime",2);

        updateDb("Device.ManagementServer.STUNMaximumKeepAlivePeriod", 50);
        updateDb("Device.ManagementServer.STUNMinimumKeepAlivePeriod", 50);
    }

    private void updateDb(String dname, String dvalue) {
        // TODO Auto-generated method stub
        LogUtil.i(TAG, "updateDb");
        int ret = -100;

        ContentValues cv = new ContentValues();
        cv.put("value", dvalue);
        ret = update(Uri.parse(URI_AUTH), cv, "name=?", new String[]{dname});
        LogUtil.i(TAG, "update:{" + dname + "} ret=" + ret);
    }

    private void updateDb(String dname, boolean dvalue) {
        // TODO Auto-generated method stub
        LogUtil.i(TAG, "updateDb");
        int ret = -100;

        ContentValues cv = new ContentValues();
        cv.put("value", dvalue);
        ret = update(Uri.parse(URI_AUTH), cv, "name=?", new String[]{dname});
        LogUtil.i(TAG, "update:{" + dname + "} ret=" + ret);
    }

    private void updateDb(String dname, int dvalue) {
        // TODO Auto-generated method stub
        LogUtil.i(TAG, "updateDb");
        int ret = -100;

        ContentValues cv = new ContentValues();
        cv.put("value", dvalue);
        ret = update(Uri.parse(URI_AUTH), cv, "name=?", new String[]{dname});
        LogUtil.i(TAG, "update:{" + dname + "} ret=" + ret);
    }


    public String getType(Uri uri) {
        LogUtil.i(TAG, "getType(" + uri.toString() + ")");
        return null;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        LogUtil.i(TAG, "query(" + uri.toString() + ")");
        String select = null;
        String[] selectArgs = null;
        String table = (String) uri.getPathSegments().get(0);
        switch (sUriMatcher.match(uri)) {
            case ALL_ROW:
                select = selection;
                selectArgs = selectionArgs;
                break;

            case SINGLE_ROW:
                select = SELECTION;
                selectArgs = new String[]{(String) uri.getPathSegments().get(1)};
                break;
        }
        return db.query(table, projection, select, selectArgs, null, null, sortOrder);
    }

    public Uri insert(Uri uri, ContentValues values) {
        LogUtil.i(TAG, "insert(" + uri.toString() + ")");
        long r = -1;
        String table = (String) uri.getPathSegments().get(0);
        r = db.insert(table, null, values);
        LogUtil.i(TAG, "insert " + r);
        return Uri.withAppendedPath(uri, r + "");
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LogUtil.i(TAG, "delete(" + uri.toString() + ")");
        String select = null;
        String[] selectArgs = null;
        String table = (String) uri.getPathSegments().get(0);
        switch (sUriMatcher.match(uri)) {
            case ALL_ROW:
                select = selection;
                selectArgs = selectionArgs;
                break;

            case SINGLE_ROW:
                select = SELECTION;
                selectArgs = new String[]{(String) uri.getPathSegments().get(1)};
                break;
        }
        return db.delete(table, select, selectArgs);
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String name = values.get(COLUMN_NAME) == null ? "" : values.get(COLUMN_NAME).toString();
        String value = values.get(COLUMN_VALUE) == null ? "" : values.get(COLUMN_VALUE).toString();
        if ((name == null) || (name.equals(""))) {
            if (selectionArgs != null) {
                name = selectionArgs[0];
                values.put(COLUMN_NAME, name);
            }
        }
        LogUtil.i(TAG, "update(" + uri.toString() + ")");
        LogUtil.i(TAG, "name=[" + name + "]");
        LogUtil.d(TAG, "value=[" + value + "]");
        String select = null;
        String selectArgs[] = null;
        String table = (String) uri.getPathSegments().get(0);
        switch (sUriMatcher.match(uri)) {
            case ALL_ROW:
                select = selection;
                selectArgs = selectionArgs;
                break;

            case SINGLE_ROW:
                select = SELECTION;
                selectArgs = new String[]{(String) uri.getPathSegments().get(1)};
                break;
        }
        return db.update(table, values, select, selectArgs);
    }

    public void onLowMemory() {
        super.onLowMemory();
        if (db != null) {
            db.close();
        }
    }

    private boolean checkData(Uri uri, Object name) {
        StringBuffer tmp = new StringBuffer();
        tmp.append(uri);
        tmp.append("/");
        tmp.append(name.toString());
        Uri uri_tmp = Uri.parse(tmp.toString());
        LogUtil.i(TAG, "uri_tmp=[" + uri_tmp + "]");
        Cursor mCursor = query(uri_tmp, null, null, null, null);
        if (mCursor != null) {
            if (!mCursor.moveToFirst()) {
                mCursor.close();
                return false;
            }
            mCursor.close();
            return true;
        }
        LogUtil.e(TAG, "query failed!! ");
        return false;
    }

}