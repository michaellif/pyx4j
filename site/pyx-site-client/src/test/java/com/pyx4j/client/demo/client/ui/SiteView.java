package com.pyx4j.client.demo.client.ui;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.pyx4j.client.demo.client.mvp.ActionsActivityMapper;
import com.pyx4j.client.demo.client.mvp.MainNavigActivityMapper;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;

@Singleton
public class SiteView extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleSuffix {
        LeftSection, RightSection, ContentWrapper, ContentSection, FooterSection
    }

    @Inject
    public SiteView(MainNavigActivityMapper mainNavigActivityMapper, ActionsActivityMapper actionsActivityMapper, EventBus eventBus, Theme theme) {

        StyleManger.installTheme(theme);

        getElement().getStyle().setBackgroundColor("#fff");
        getElement().getStyle().setWidth(90, Unit.PCT);
        getElement().getStyle().setProperty("minWidth", "760px");
        getElement().getStyle().setProperty("margin", "0 auto");

        //============ Top Panel ============

        FlowPanel topWrapper = new FlowPanel();
        topWrapper.setHeight("100%");
        add(topWrapper);

        SimplePanel logoDisplayPanel = new DisplayPanel("Logo");
        logoDisplayPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        topWrapper.add(logoDisplayPanel);

        SimplePanel actionsDisplayPanel = new DisplayPanel("Actions|Actions|Actions|Actions");
        actionsDisplayPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        topWrapper.add(actionsDisplayPanel);

        //============ Main Navig ============

        FlowPanel mainNavigWrapper = new FlowPanel();
        mainNavigWrapper.setWidth("100%");
        mainNavigWrapper.getElement().getStyle().setFloat(Style.Float.LEFT);
        add(mainNavigWrapper);

        SimplePanel mainNavigDisplayPanel = new DisplayPanel("Main Navig");
        mainNavigWrapper.add(mainNavigDisplayPanel);

        //============ Main ============

        SimplePanel centerWrapper = new SimplePanel();
        centerWrapper.getElement().getStyle().setProperty("width", "100%");
        centerWrapper.getElement().getStyle().setFloat(Style.Float.LEFT);
        add(centerWrapper);

        FlowPanel mainWrapper = new FlowPanel();
        mainWrapper.setHeight("100%");
        mainWrapper.getElement().getStyle().setProperty("margin", "0 200px 0 230px");
        centerWrapper.add(mainWrapper);

        SimplePanel centerDisplayPanel1 = new DisplayPanel("Center1");
        mainWrapper.add(centerDisplayPanel1);

        SimplePanel centerDisplayPanel2 = new DisplayPanel("Center2");
        mainWrapper.add(centerDisplayPanel2);

        FlowPanel leftWrapper = new FlowPanel();
        leftWrapper.getElement().getStyle().setFloat(Style.Float.LEFT);
        leftWrapper.getElement().getStyle().setProperty("width", "230px");
        leftWrapper.getElement().getStyle().setProperty("marginLeft", "-100%");
        leftWrapper.setWidth("230px");
        add(leftWrapper);

        SimplePanel leftDisplayPanel1 = new DisplayPanel("Left1");
        leftWrapper.add(leftDisplayPanel1);
        SimplePanel leftDisplayPanel2 = new DisplayPanel("Left2");
        leftWrapper.add(leftDisplayPanel2);

        FlowPanel rightWrapper = new FlowPanel();
        rightWrapper.getElement().getStyle().setFloat(Style.Float.LEFT);
        rightWrapper.getElement().getStyle().setProperty("width", "200px");
        rightWrapper.getElement().getStyle().setProperty("marginLeft", "-200px");
        rightWrapper.setWidth("200px");
        add(rightWrapper);

        SimplePanel rightDisplayPanel1 = new DisplayPanel("Right1");
        rightWrapper.add(rightDisplayPanel1);
        SimplePanel rightDisplayPanel2 = new DisplayPanel("Right2");
        rightWrapper.add(rightDisplayPanel2);

        //============ Footer ============

        SimplePanel bottomDisplayPanel = new DisplayPanel("Footer");
        bottomDisplayPanel.getElement().getStyle().setProperty("clear", "left");
        add(bottomDisplayPanel);

        ActivityManager mainActivityManager = new ActivityManager(mainNavigActivityMapper, eventBus);
        //mainActivityManager.setDisplay(mainNavigDisplayPanel);

        ActivityManager verticalMasterActivityManager = new ActivityManager(actionsActivityMapper, eventBus);
        //verticalMasterActivityManager.setDisplay(actionsDisplayPanel);

    }

    class DisplayPanel extends SimplePanel {
        DisplayPanel(String title) {

            HTML inner = new HTML(title);
            inner.setSize("100%", "100px");
            setWidget(inner);

            getElement().getStyle().setBackgroundColor("#ddd");
            getElement().getStyle().setMarginBottom(10, Unit.PX);
            getElement().getStyle().setMarginLeft(10, Unit.PX);
            getElement().getStyle().setMarginRight(10, Unit.PX);

        }
    }

}
