/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.NoInsuranceTenantInsuranceStatusDTO;

public class NoTenantInsuranceStatusViewer extends CEntityViewer<NoInsuranceTenantInsuranceStatusDTO> {

    public static final String STYLE_PREFIX = "-vista-NoTenantInsurance";

    public enum StyleSuffix implements IStyleName {

        Panel;

    }

    private static final I18n i18n = I18n.get(NoTenantInsuranceStatusViewer.class);

    @Override
    public IsWidget createContent(NoInsuranceTenantInsuranceStatusDTO value) {
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.addStyleName(STYLE_PREFIX + StyleSuffix.Panel.name());
        HTML explanationMessage = new HTML();
        explanationMessage.setHTML(value.noInsuranceStatusMessage().getValue());
        contentPanel.add(explanationMessage);

        Anchor goToInsuranceScreenAnchor = new Anchor(i18n.tr("Provide proof of insurance"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.TenantInsurance.ProvideTenantInsurance());
            }
        });
        goToInsuranceScreenAnchor.addStyleName(TenantInsuranceStatusViewer.Styles.TenantInsuranceAnchor.name());
        contentPanel.add(goToInsuranceScreenAnchor);

        return contentPanel;
    }
}
