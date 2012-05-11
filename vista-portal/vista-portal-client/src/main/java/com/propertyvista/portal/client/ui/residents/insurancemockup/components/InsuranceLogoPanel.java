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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.propertyvista.portal.client.ui.residents.insurancemockup.resources.InsuranceMockupResources;

public class InsuranceLogoPanel extends VerticalPanel {

    public InsuranceLogoPanel() {
        add(new Image(InsuranceMockupResources.INSTANCE.logoTenantSure()));
        add(new Image(InsuranceMockupResources.INSTANCE.logoHighcourt()));
        add(new HTML("1-888-1234-444"));
        getElement().getStyle().setPadding(1, Unit.EM);
        getElement().getStyle().setMargin(1, Unit.EM);
    }

}
