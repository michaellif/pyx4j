package com.pyx4j.client.demo.client.ui;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.pyx4j.client.demo.client.mvp.MainActivityMapper;
import com.pyx4j.client.demo.client.mvp.VerticalMasterActivityMapper;

@Singleton
public class MainView extends VerticalPanel {

    SimplePanel mainDisplayPanel;

    SimplePanel verticalMasterDisplayPanel;

    @Inject
    public MainView(MainActivityMapper mainActivityMapper, VerticalMasterActivityMapper verticalMasterActivityMapper, EventBus eventBus) {

        mainDisplayPanel = new SimplePanel();
        add(mainDisplayPanel);

        verticalMasterDisplayPanel = new SimplePanel();
        add(verticalMasterDisplayPanel);

        ActivityManager mainActivityManager = new ActivityManager(mainActivityMapper, eventBus);
        mainActivityManager.setDisplay(mainDisplayPanel);

        ActivityManager verticalMasterActivityManager = new ActivityManager(verticalMasterActivityMapper, eventBus);
        verticalMasterActivityManager.setDisplay(verticalMasterDisplayPanel);

    }

}
