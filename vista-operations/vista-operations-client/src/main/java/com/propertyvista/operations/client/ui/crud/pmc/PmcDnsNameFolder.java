/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2012
 * @author vlads
 */
package com.propertyvista.operations.client.ui.crud.pmc;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.pmc.PmcDnsName;

public class PmcDnsNameFolder extends VistaBoxFolder<PmcDnsName> {

    public PmcDnsNameFolder(boolean modifiable) {
        super(PmcDnsName.class, modifiable);
    }

    @Override
    protected CForm<PmcDnsName> createItemForm(IObject<?> member) {
        return new CForm<PmcDnsName>(PmcDnsName.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().enabled()).decorate();

                formPanel.append(Location.Left, proto().dnsName()).decorate();
                formPanel.append(Location.Left, proto().target()).decorate().componentWidth(100);

                formPanel.append(Location.Right, proto().httpsEnabled()).decorate();

                formPanel.append(Location.Right, proto().googleAPIKey()).decorate();
                formPanel.append(Location.Right, proto().googleAnalyticsId()).decorate();

                return formPanel;
            }
        };
    }
}
