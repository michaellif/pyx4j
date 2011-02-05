package com.pyx4j.site.client.ui;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;

public class AppSiteView extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleSuffix {
        Header, MainNavig, Center, Main, Left, Right, Footer
    }

    public AppSiteView(ActivityMapper logoActivityMapper,

    ActivityMapper actionsActivityMapper,

    ActivityMapper mainNavigActivityMapper,

    ActivityMapper center1ActivityMapper,

    ActivityMapper center2ActivityMapper,

    ActivityMapper center3ActivityMapper,

    ActivityMapper left1ActivityMapper,

    ActivityMapper left2ActivityMapper,

    ActivityMapper right1ActivityMapper,

    ActivityMapper right2ActivityMapper,

    ActivityMapper bottomActivityMapper,

    EventBus eventBus,

    Theme theme) {

        StyleManger.installTheme(theme);

        String prefix = AppSiteView.DEFAULT_STYLE_PREFIX;

        setStyleName(prefix);

        //============ Top Panel ============

        FlowPanel headerWrapper = new FlowPanel();
        headerWrapper.setStyleName(prefix + StyleSuffix.Header);
        add(headerWrapper);

        DisplayPanel logoDisplayPanel = new DisplayPanel();
        logoDisplayPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        headerWrapper.add(logoDisplayPanel);

        DisplayPanel actionsDisplayPanel = new DisplayPanel();
        actionsDisplayPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        headerWrapper.add(actionsDisplayPanel);

        //============ Main Navig ============

        FlowPanel mainNavigWrapper = new FlowPanel();
        mainNavigWrapper.setStyleName(prefix + StyleSuffix.MainNavig);
        add(mainNavigWrapper);

        DisplayPanel mainNavigDisplayPanel = new DisplayPanel();
        mainNavigWrapper.add(mainNavigDisplayPanel);

        //============ Main ============

        SimplePanel centerWrapper = new SimplePanel();
        centerWrapper.setStyleName(prefix + StyleSuffix.Center);
        add(centerWrapper);

        FlowPanel mainWrapper = new FlowPanel();
        mainWrapper.setStyleName(prefix + StyleSuffix.Main);
        centerWrapper.add(mainWrapper);

        DisplayPanel center1DisplayPanel = new DisplayPanel();
        mainWrapper.add(center1DisplayPanel);

        DisplayPanel center2DisplayPanel = new DisplayPanel();
        mainWrapper.add(center2DisplayPanel);

        DisplayPanel center3DisplayPanel = new DisplayPanel();
        mainWrapper.add(center3DisplayPanel);

        FlowPanel leftWrapper = new FlowPanel();
        leftWrapper.setStyleName(prefix + StyleSuffix.Left);
        add(leftWrapper);

        DisplayPanel left1DisplayPanel = new DisplayPanel();
        leftWrapper.add(left1DisplayPanel);
        DisplayPanel left2DisplayPanel = new DisplayPanel();
        leftWrapper.add(left2DisplayPanel);

        FlowPanel rightWrapper = new FlowPanel();
        rightWrapper.setStyleName(prefix + StyleSuffix.Right);
        add(rightWrapper);

        DisplayPanel right1DisplayPanel = new DisplayPanel();
        rightWrapper.add(right1DisplayPanel);
        DisplayPanel right2DisplayPanel = new DisplayPanel();
        rightWrapper.add(right2DisplayPanel);

        //============ Footer ============

        FlowPanel footerWrapper = new FlowPanel();
        footerWrapper.setStyleName(prefix + StyleSuffix.Footer);
        footerWrapper.getElement().getStyle().setProperty("clear", "left");
        add(footerWrapper);

        DisplayPanel bottomDisplayPanel = new DisplayPanel();
        footerWrapper.add(bottomDisplayPanel);

        bind(logoActivityMapper, logoDisplayPanel, eventBus);
        bind(actionsActivityMapper, actionsDisplayPanel, eventBus);
        bind(mainNavigActivityMapper, mainNavigDisplayPanel, eventBus);

        bind(center1ActivityMapper, center1DisplayPanel, eventBus);
        bind(center2ActivityMapper, center2DisplayPanel, eventBus);
        bind(center3ActivityMapper, center3DisplayPanel, eventBus);

        bind(left1ActivityMapper, left1DisplayPanel, eventBus);
        bind(left2ActivityMapper, left2DisplayPanel, eventBus);

        bind(right1ActivityMapper, right1DisplayPanel, eventBus);
        bind(right2ActivityMapper, right2DisplayPanel, eventBus);

        bind(bottomActivityMapper, bottomDisplayPanel, eventBus);

    }

    private static void bind(ActivityMapper mapper, AcceptsOneWidget widget, EventBus eventBus) {
        ActivityManager logoActivityManager = new ActivityManager(mapper, eventBus);
        logoActivityManager.setDisplay(widget);

    }

    class DisplayPanel extends SimplePanel {
        DisplayPanel() {
            getElement().getStyle().setBackgroundColor("#ddd");
            getElement().getStyle().setMarginBottom(10, Unit.PX);
            getElement().getStyle().setMarginLeft(10, Unit.PX);
            getElement().getStyle().setMarginRight(10, Unit.PX);

        }
    }

}
