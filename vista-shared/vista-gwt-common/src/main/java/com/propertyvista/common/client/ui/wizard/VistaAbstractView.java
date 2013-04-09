/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-05
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.wizard;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.visor.IVisor;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public abstract class VistaAbstractView extends VerticalPanel implements IPane {

    private final SimplePanel contentHolder;

    private final FlowPanel headerCaption;

    private final Label captionLabel;

    private final Toolbar headerToolbar;

    private final Toolbar footerToolbar;

    private final SimplePanel headerToolbarHolder;

    private final SimplePanel footerToolbarHolder;

    private String headerToolbarHeight = "50px";

    private String footerToolbarHeight = "50px";

    public VistaAbstractView() {
        super();

        setWidth("100%");

// TODO: style right (generic) way!!!

        captionLabel = new Label();
//      captionLabel.setStyleName(DefaultPaneTheme.StyleName.HeaderCaption.name());

        headerCaption = new FlowPanel();
        headerCaption.add(captionLabel);
//        headerCaption.setStyleName(DefaultPaneTheme.StyleName.Header.name());
        headerCaption.getElement().getStyle().setFontSize(1.2, Unit.EM);
        headerCaption.getElement().getStyle().setFontWeight(FontWeight.BOLD);

        headerCaption.setHeight("30px");
        add(headerCaption);

        headerToolbarHolder = new SimplePanel();
//        headerToolbarHolder.setStyleName(DefaultPaneTheme.StyleName.HeaderToolbar.name());

        headerToolbar = new Toolbar();
        headerToolbarHolder.setWidget(headerToolbar);
        add(headerToolbarHolder);

        add(contentHolder = new SimplePanel());

        footerToolbarHolder = new SimplePanel();
        footerToolbarHolder.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
//        footerToolbarHolder.setStyleName(DefaultPaneTheme.StyleName.FooterToolbar.name());

        footerToolbar = new Toolbar();
        footerToolbarHolder.setWidget(footerToolbar);
        add(footerToolbarHolder);
    }

    protected FlowPanel getHeaderCaption() {
        return headerCaption;
    }

    protected IsWidget getContent() {
        return contentHolder.getWidget();
    }

    protected void setContent(IsWidget widget) {
        contentHolder.clear();
        contentHolder.setWidget(widget);
    }

    public void setCaption(String caption) {
        captionLabel.setText(caption);
    }

    public String getCaption() {
        return captionLabel.getText();
    }

    public void addHeaderToolbarItem(Widget widget) {
        headerToolbarHolder.setHeight(headerToolbarHeight);
        headerToolbar.addItem(widget);
    }

    public void setHeaderToolbarHeight(String headerToolbarHeight) {
        this.headerToolbarHeight = headerToolbarHeight;
        if (headerToolbar.getWidgetCount() == 0) {
            headerToolbarHolder.setHeight(headerToolbarHeight);
        }
    }

    public void addFooterToolbarItem(Widget widget) {
        footerToolbarHolder.setHeight(footerToolbarHeight);
        footerToolbar.addItem(widget);
    }

    public void setFooterToolbarHeight(String footerToolbarHeight) {
        this.footerToolbarHeight = footerToolbarHeight;
        if (footerToolbar.getWidgetCount() == 0) {
            footerToolbarHolder.setHeight(footerToolbarHeight);
        }
    }

    // not supported functionality:

    @Override
    public void showVisor(IVisor visor, String caption) {
        // TODO Auto-generated method stub
    }

    @Override
    public void hideVisor() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isVisorShown() {
        // TODO Auto-generated method stub
        return false;
    }
}
