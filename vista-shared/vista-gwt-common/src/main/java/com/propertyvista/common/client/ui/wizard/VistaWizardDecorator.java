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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.wizard.CEntityWizardTheme;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.site.client.ui.visor.IVisor;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public abstract class VistaWizardDecorator extends VerticalPanel implements IPane {

    private final FlowPanel header;

    private final Label captionLabel;

    private final SimplePanel contentHolder;

    private final Toolbar footerToolbar;

    private final SimplePanel footer;

    private String footerHeight = "auto";

    public VistaWizardDecorator() {
        captionLabel = new Label();
        captionLabel.setStyleName(CEntityWizardTheme.StyleName.HeaderCaption.name());

        header = new FlowPanel();
        header.add(captionLabel);
        header.setStyleName(CEntityWizardTheme.StyleName.Header.name());
        add(header);

        add(contentHolder = new SimplePanel());

        footerToolbar = new Toolbar();
        footer = new SimplePanel();
        footer.setStyleName(CEntityWizardTheme.StyleName.FooterToolbar.name());
        footer.setWidget(footerToolbar);
        add(footer);

        setWidth("100%");
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

    public void addFooterItem(Widget widget) {
        footer.setHeight(footerHeight);
        footerToolbar.add(widget);
    }

    public void setFooterHeight(String footerToolbarHeight) {
        this.footerHeight = footerToolbarHeight;
        if (footerToolbar.getWidgetCount() == 0) {
            footer.setHeight(footerToolbarHeight);
        }
    }

    // not supported functionality:

    @Override
    public void showVisor(IVisor visor) {
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
