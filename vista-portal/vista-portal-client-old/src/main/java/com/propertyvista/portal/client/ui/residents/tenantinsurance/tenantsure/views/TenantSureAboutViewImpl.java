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
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.resources.TenantSureResources;

public class TenantSureAboutViewImpl extends Composite implements TenantSureAboutView {

    public enum Styles implements IStyleName {

        TenantSureAboutContactInfo

    }

    public TenantSureAboutViewImpl() {
        FlowPanel panel = new FlowPanel();

        Label tenantSureContactInfo = new Label();
        tenantSureContactInfo.addStyleName(TenantSureAboutViewImpl.Styles.TenantSureAboutContactInfo.name());
        tenantSureContactInfo.setWidth("100%");

        tenantSureContactInfo.setHTML(TenantSureResources.INSTANCE.contactInfo().getText());
        panel.add(tenantSureContactInfo);

        initWidget(panel);
    }
}
