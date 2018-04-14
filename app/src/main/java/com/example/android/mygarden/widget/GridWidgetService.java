package com.example.android.mygarden.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by figengungor on 4/14/2018.
 */

public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}
