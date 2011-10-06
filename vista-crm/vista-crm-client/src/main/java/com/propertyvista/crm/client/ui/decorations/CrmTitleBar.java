/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-21
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.decorations;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.widgets.client.style.IStyleName;

public class CrmTitleBar extends HorizontalPanel {

    public static String DEFAULT_STYLE_PREFIX = "vista_CrmTitleBar";

    public static enum StyleSuffix implements IStyleName {
        Caption, Breadcrumb
    }

    private final HTML captionHolder;

    private final HTML breadcrumbHolder;

    public CrmTitleBar(String caption) {
        captionHolder = new HTML(caption, false);
        setStyleName(getStylePrefix());
        captionHolder.setStyleName(getStylePrefix() + StyleSuffix.Caption.name());
        add(captionHolder);
        setCellVerticalAlignment(captionHolder, HorizontalPanel.ALIGN_MIDDLE);

        breadcrumbHolder = new HTML("breadcrumb1->breadcrumb2");
        setStyleName(getStylePrefix());
        breadcrumbHolder.setStyleName(getStylePrefix() + StyleSuffix.Breadcrumb.name());
        add(breadcrumbHolder);
        setCellVerticalAlignment(breadcrumbHolder, HorizontalPanel.ALIGN_MIDDLE);
        setCellWidth(breadcrumbHolder, "100%");

        getElement().getStyle().setProperty("clear", "both");
    }

    public void setCaption(String caption) {
        captionHolder.setText(caption);
    }

    protected String getStylePrefix() {
        return DEFAULT_STYLE_PREFIX;
    }
}