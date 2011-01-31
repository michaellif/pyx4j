package com.pyx4j.client.demo.client.ui;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.pyx4j.client.demo.client.mvp.MainNavigActivityMapper;
import com.pyx4j.client.demo.client.mvp.ActionsActivityMapper;

@Singleton
public class MainView extends FlowPanel {

    private final SimplePanel mainDisplayPanel;

    private final SimplePanel verticalMasterDisplayPanel;

    @Inject
    public MainView(MainNavigActivityMapper mainActivityMapper, ActionsActivityMapper verticalMasterActivityMapper, EventBus eventBus) {

        mainDisplayPanel = new SimplePanel();
        mainDisplayPanel.setHeight("200px");
        mainDisplayPanel.getElement().getStyle().setBorderColor("red");
        mainDisplayPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        mainDisplayPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);

        add(mainDisplayPanel);

        verticalMasterDisplayPanel = new SimplePanel();
        verticalMasterDisplayPanel.setHeight("200px");
        verticalMasterDisplayPanel.getElement().getStyle().setBorderColor("green");
        verticalMasterDisplayPanel.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        verticalMasterDisplayPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
        add(verticalMasterDisplayPanel);

        ActivityManager mainActivityManager = new ActivityManager(mainActivityMapper, eventBus);
        mainActivityManager.setDisplay(mainDisplayPanel);

        ActivityManager verticalMasterActivityManager = new ActivityManager(verticalMasterActivityMapper, eventBus);
        verticalMasterActivityManager.setDisplay(verticalMasterDisplayPanel);

    }

}
