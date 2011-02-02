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
        Header, MainNavig, Center, Main, Left, Right, ContentWrapper, Content, Footer
    }

    @Inject
    public SiteView(MainNavigActivityMapper mainNavigActivityMapper, ActionsActivityMapper actionsActivityMapper, EventBus eventBus, Theme theme) {

        StyleManger.installTheme(theme);

        String prefix = SiteView.DEFAULT_STYLE_PREFIX;

        setStyleName(prefix);

        //============ Top Panel ============

        FlowPanel headerWrapper = new FlowPanel();
        headerWrapper.setStyleName(prefix + StyleSuffix.Header);
        add(headerWrapper);

        SimplePanel logoDisplayPanel = new DisplayPanel("Logo Logo Logo");
        logoDisplayPanel.getElement().getStyle().setFloat(Style.Float.LEFT);
        headerWrapper.add(logoDisplayPanel);

        SimplePanel actionsDisplayPanel = new DisplayPanel("Actions|Actions|Actions|Actions");
        actionsDisplayPanel.getElement().getStyle().setFloat(Style.Float.RIGHT);
        headerWrapper.add(actionsDisplayPanel);

        //============ Main Navig ============

        FlowPanel mainNavigWrapper = new FlowPanel();
        mainNavigWrapper.setStyleName(prefix + StyleSuffix.MainNavig);
        add(mainNavigWrapper);

        SimplePanel mainNavigDisplayPanel = new DisplayPanel("Main Navig");
        mainNavigWrapper.add(mainNavigDisplayPanel);

        //============ Main ============

        SimplePanel centerWrapper = new SimplePanel();
        centerWrapper.setStyleName(prefix + StyleSuffix.Center);
        add(centerWrapper);

        FlowPanel mainWrapper = new FlowPanel();
        mainWrapper.setStyleName(prefix + StyleSuffix.Main);
        centerWrapper.add(mainWrapper);

        SimplePanel centerDisplayPanel1 = new DisplayPanel(
                "Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 Center1 ");
        mainWrapper.add(centerDisplayPanel1);

        SimplePanel centerDisplayPanel2 = new DisplayPanel(
                "Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 Center2 ");
        mainWrapper.add(centerDisplayPanel2);

        FlowPanel leftWrapper = new FlowPanel();
        leftWrapper.setStyleName(prefix + StyleSuffix.Left);
        add(leftWrapper);

        SimplePanel leftDisplayPanel1 = new DisplayPanel("Left1");
        leftWrapper.add(leftDisplayPanel1);
        SimplePanel leftDisplayPanel2 = new DisplayPanel("Left2");
        leftWrapper.add(leftDisplayPanel2);

        FlowPanel rightWrapper = new FlowPanel();
        rightWrapper.setStyleName(prefix + StyleSuffix.Right);
        add(rightWrapper);

        SimplePanel rightDisplayPanel1 = new DisplayPanel("Right1");
        rightWrapper.add(rightDisplayPanel1);
        SimplePanel rightDisplayPanel2 = new DisplayPanel("Right2");
        rightWrapper.add(rightDisplayPanel2);

        //============ Footer ============

        FlowPanel footerWrapper = new FlowPanel();
        footerWrapper.setStyleName(prefix + StyleSuffix.Footer);
        footerWrapper.getElement().getStyle().setProperty("clear", "left");
        add(footerWrapper);

        SimplePanel bottomDisplayPanel = new DisplayPanel("Footer");
        footerWrapper.add(bottomDisplayPanel);

        ActivityManager mainActivityManager = new ActivityManager(mainNavigActivityMapper, eventBus);
        //mainActivityManager.setDisplay(mainNavigDisplayPanel);

        ActivityManager verticalMasterActivityManager = new ActivityManager(actionsActivityMapper, eventBus);
        //verticalMasterActivityManager.setDisplay(actionsDisplayPanel);

    }

    class DisplayPanel extends SimplePanel {
        DisplayPanel(String title) {

            HTML inner = new HTML(title);
            inner.setWidth("100%");
            setWidget(inner);

            getElement().getStyle().setBackgroundColor("#ddd");
            getElement().getStyle().setMarginBottom(10, Unit.PX);
            getElement().getStyle().setMarginLeft(10, Unit.PX);
            getElement().getStyle().setMarginRight(10, Unit.PX);

        }
    }

}
