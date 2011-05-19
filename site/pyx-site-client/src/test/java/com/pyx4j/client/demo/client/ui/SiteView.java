package com.pyx4j.client.demo.client.ui;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.pyx4j.client.demo.client.mvp.ActionsActivityMapper;
import com.pyx4j.client.demo.client.mvp.BottomActivityMapper;
import com.pyx4j.client.demo.client.mvp.CaptionActivityMapper;
import com.pyx4j.client.demo.client.mvp.MessageActivityMapper;
import com.pyx4j.client.demo.client.mvp.ContentActivityMapper;
import com.pyx4j.client.demo.client.mvp.Left1ActivityMapper;
import com.pyx4j.client.demo.client.mvp.Left2ActivityMapper;
import com.pyx4j.client.demo.client.mvp.LogoActivityMapper;
import com.pyx4j.client.demo.client.mvp.MainNavigActivityMapper;
import com.pyx4j.client.demo.client.mvp.Right1ActivityMapper;
import com.pyx4j.client.demo.client.mvp.Right2ActivityMapper;
import com.pyx4j.widgets.client.style.IStyleSuffix;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;

@Singleton
public class SiteView extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public static enum StyleSuffix implements IStyleSuffix {
        Header, MainNavig, Center, Main, Left, Right, Footer
    }

    @Inject
    public SiteView(LogoActivityMapper logoActivityMapper,

    ActionsActivityMapper actionsActivityMapper,

    MainNavigActivityMapper mainNavigActivityMapper,

    CaptionActivityMapper center1ActivityMapper,

    MessageActivityMapper center2ActivityMapper,

    ContentActivityMapper center3ActivityMapper,

    Left1ActivityMapper left1ActivityMapper,

    Left2ActivityMapper left2ActivityMapper,

    Right1ActivityMapper right1ActivityMapper,

    Right2ActivityMapper right2ActivityMapper,

    BottomActivityMapper bottomActivityMapper,

    EventBus eventBus,

    Theme theme) {

        StyleManger.installTheme(theme);

        String prefix = SiteView.DEFAULT_STYLE_PREFIX;

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
        //        bind(center2ActivityMapper, center2DisplayPanel, eventBus);
        //        bind(center3ActivityMapper, center3DisplayPanel, eventBus);
        //
        //        bind(left1ActivityMapper, left1DisplayPanel, eventBus);
        //        bind(left2ActivityMapper, left2DisplayPanel, eventBus);
        //
        //        bind(right1ActivityMapper, right1DisplayPanel, eventBus);
        //        bind(right2ActivityMapper, right2DisplayPanel, eventBus);

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
