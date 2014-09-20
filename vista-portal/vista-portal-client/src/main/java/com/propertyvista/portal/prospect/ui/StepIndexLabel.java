/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.HTML;

import com.propertyvista.portal.prospect.ui.WizardStepItem.StepStatus;
import com.propertyvista.portal.shared.themes.StepsTheme;

public class StepIndexLabel extends HTML {

    public StepIndexLabel(String label) {
        super(label);

        setStyleName(StepsTheme.StyleName.StepIndexLabel.name());
        getElement().getStyle().setFloat(Float.LEFT);
        getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        getElement().getStyle().setTextAlign(TextAlign.CENTER);

        setHeight("30px");
        getElement().getStyle().setProperty("minWidth", "30px");
        getElement().getStyle().setLineHeight(30, Unit.PX);
        getElement().getStyle().setFontSize(20, Unit.PX);
        getElement().getStyle().setProperty("borderRadius", "15px");

    }

    public void setStatus(StepStatus status) {
        getElement().getStyle().setColor("#fff");
        switch (status) {
        case notComplete:
            getElement().getStyle().setBackgroundColor("#999");
            break;
        case complete:
            getElement().getStyle().setBackgroundColor("#5EA11B");
            break;
        case current:
            getElement().getStyle().setBackgroundColor("#fff");
            getElement().getStyle().setColor("#999");
            break;
        case invalid:
            getElement().getStyle().setBackgroundColor("#FF4F3A");
            break;
        }
    }

}