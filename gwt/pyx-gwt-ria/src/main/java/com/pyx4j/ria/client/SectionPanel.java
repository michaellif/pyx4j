/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.ResizableWidget;
import com.google.gwt.widgetideas.client.ResizableWidgetCollection;

import com.pyx4j.widgets.client.DecoratorPanel;
import com.pyx4j.widgets.client.style.CSSClass;

public class SectionPanel extends DecoratorPanel implements ResizableWidget {

    static private ResizableWidgetCollection resizableWidgetCollection = new ResizableWidgetCollection(50);

    private final SimplePanel scrollPanel;

    private final Panel scrollPanelHolder;

    private final SpaceHolderPanel header1Holder;

    private final SpaceHolderPanel header2Holder;

    private final SpaceHolderPanel footerHolder;

    private final SpaceHolderPanel contentPaneHolder;

    public SectionPanel() {

        super(true, true, true, true, 1, CSSClass.pyx4j_Section_Border.name());

        DockPanel rootPanel = new DockPanel();

        header1Holder = new SpaceHolderPanel();
        rootPanel.add(header1Holder, DockPanel.NORTH);
        header1Holder.setWidth("100%");
        rootPanel.setCellWidth(header1Holder, "100%");

        DecoratorPanel header2Decorator = new DecoratorPanel(true, true, false, true, 2, CSSClass.pyx4j_Section_SelectionBorder.name());
        header2Holder = new SpaceHolderPanel();
        header2Decorator.setWidget(header2Holder);

        rootPanel.add(header2Decorator, DockPanel.NORTH);
        header2Decorator.setWidth("100%");
        rootPanel.setCellWidth(header2Decorator, "100%");

        contentPaneHolder = new SpaceHolderPanel();

        scrollPanel = new SimplePanel();
        DOM.setStyleAttribute(scrollPanel.getElement(), "position", "relative");
        scrollPanel.setSize("100%", "100%");

        scrollPanelHolder = new SimplePanel();
        scrollPanelHolder.add(contentPaneHolder);

        scrollPanel.add(scrollPanelHolder);

        DOM.setStyleAttribute(scrollPanelHolder.getElement(), "overflow", "auto");
        DOM.setStyleAttribute(scrollPanelHolder.getElement(), "position", "absolute");
        DOM.setStyleAttribute(scrollPanelHolder.getElement(), "top", "0px");
        DOM.setStyleAttribute(scrollPanelHolder.getElement(), "left", "0px");

        DecoratorPanel contentDecorator = new DecoratorPanel(false, true, true, true, 2, CSSClass.pyx4j_Section_SelectionBorder.name());
        contentDecorator.setWidget(scrollPanel);

        rootPanel.add(contentDecorator, DockPanel.CENTER);
        contentDecorator.setSize("100%", "100%");
        rootPanel.setCellHeight(contentDecorator, "100%");
        rootPanel.setCellWidth(contentDecorator, "100%");

        scrollPanelHolder.setStyleName(CSSClass.pyx4j_Section_Content.name());

        footerHolder = new SpaceHolderPanel();
        rootPanel.add(footerHolder, DockPanel.SOUTH);

        rootPanel.setStyleName(CSSClass.pyx4j_Section_Background.name());

        setWidget(rootPanel);

        rootPanel.setSize("100%", "100%");
        setCellWidth(rootPanel, "100%");
        setCellHeight(rootPanel, "100%");

        setStyleName(CSSClass.pyx4j_Section.name());

        setSize("100%", "100%");

    }

    @Override
    protected void onAttach() {
        super.onAttach();
        resizableWidgetCollection.add(this);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        resizableWidgetCollection.remove(this);
    }

    @Override
    public void onResize(int width, int height) {
        onResize();
    }

    public void onResize() {
        scrollPanelHolder.setWidth(scrollPanel.getOffsetWidth() + "px");
        scrollPanelHolder.setHeight(scrollPanel.getOffsetHeight() + "px");
    }

    protected void setHeader1Pane(Widget headerPane) {
        header1Holder.clear();
        if (headerPane != null) {
            header1Holder.add(headerPane);
        }
    }

    protected void setHeader2Pane(Widget headerPane) {
        header2Holder.clear();
        if (headerPane != null) {
            header2Holder.add(headerPane);
        }
    }

    protected void setFooterPane(Widget footerPane) {
        footerHolder.clear();
        if (footerPane != null) {
            footerHolder.add(footerPane);
        }
    }

    protected void setContentPane(Widget contentPane) {
        contentPaneHolder.clear();
        if (contentPane != null) {
            contentPaneHolder.add(contentPane);
        }
    }

    public int getVerticalScrollPosition() {
        return DOM.getElementPropertyInt(scrollPanelHolder.getElement(), "scrollTop");
    }

    public void setVerticalScrollPosition(int position) {
        DOM.setElementPropertyInt(scrollPanelHolder.getElement(), "scrollTop", position);
    }

    public void scrollToBottom() {
        setVerticalScrollPosition(DOM.getElementPropertyInt(scrollPanelHolder.getElement(), "scrollHeight"));
    }

    public int getHorizontalScrollPosition() {
        return DOM.getElementPropertyInt(scrollPanelHolder.getElement(), "scrollLeft");
    }

    public void setHorizontalScrollPosition(int position) {
        DOM.setElementPropertyInt(scrollPanelHolder.getElement(), "scrollLeft", position);
    }

}
