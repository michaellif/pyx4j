/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.insurancemockup.components;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class InsuranceMessagePanel extends HorizontalPanel {

    public InsuranceMessagePanel(IsWidget message) {
        InsuranceLogoPanel logoPanel = new InsuranceLogoPanel();
        add(logoPanel);
        setCellWidth(logoPanel, "5em");
        setCellHorizontalAlignment(logoPanel, HasHorizontalAlignment.ALIGN_CENTER);
        setCellVerticalAlignment(logoPanel, HasVerticalAlignment.ALIGN_MIDDLE);
        add(message);
        setCellVerticalAlignment(message, HasVerticalAlignment.ALIGN_MIDDLE);
        getElement().getStyle().setMarginRight(1, Unit.EM);
    }
}
