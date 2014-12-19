/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-14
 * @author ArtyomB
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.portal.resident.themes.TenantSureTheme;

public class TenantSureAboutViewImpl extends FlowPanel implements TenantSureAboutView {

    public TenantSureAboutViewImpl() {

        setStyleName(TenantSureTheme.StyleName.TenantSureAboutContactInfo.name());

        TenanatSureAboutGadget tenantSureContactInfo = new TenanatSureAboutGadget(this, ThemeColor.contrast3, 1);
        tenantSureContactInfo.asWidget().setWidth("100%");
        add(tenantSureContactInfo);

    }
}
