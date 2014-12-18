/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-14
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.tools.common;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

public class SimpleProgressWidget extends Composite implements ProgressWidget {

    private static final I18n i18n = I18n.get(SimpleProgressWidget.class);

    private final Label progressLabel;

    public SimpleProgressWidget() {
        SimplePanel panel = new SimplePanel();
        panel.getElement().getStyle().setDisplay(Display.BLOCK);
        panel.getElement().getStyle().setMarginTop(50, Unit.PX);
        panel.setSize("100%", "100%");
        progressLabel = new Label();
        progressLabel.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        progressLabel.setWidth("100%");
        panel.setWidget(progressLabel);

        initWidget(panel);

    }

    @Override
    public void setProgress(int currentProgress, int maxiumProgress, String message) {
        progressLabel.setText(i18n.tr("Processing item {0} out {1}", currentProgress, maxiumProgress));
    }

}
