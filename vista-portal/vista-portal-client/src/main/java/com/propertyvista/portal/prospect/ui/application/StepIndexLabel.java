/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.HTML;

import com.propertyvista.portal.prospect.ui.application.NavigStepItem.StepStatus;
import com.propertyvista.portal.shared.themes.StepsTheme;

public class StepIndexLabel extends HTML {

    public StepIndexLabel(String label) {
        super(label);

        setStyleName(StepsTheme.StyleName.WizardStepHandler.name());
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
            getElement().getStyle().setBackgroundColor("#93c948");
            break;
        case current:
            getElement().getStyle().setBackgroundColor("#fff");
            getElement().getStyle().setColor("#999");
            break;
        case invalid:
            getElement().getStyle().setBackgroundColor("#ef372f");
            break;
        }
    }

}