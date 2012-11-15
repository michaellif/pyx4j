/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms.TenantSureStatusForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.resources.TenantSureResources;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.TenantSureTenantInsuranceStatusShortDTO;

public class TenantSureInsuranceStatusViewer extends CEntityViewer<TenantSureTenantInsuranceStatusShortDTO> {

    public static final String STYLE_PREFIX = "-vista-TenantSureStatusViewer";

    public enum StyleSuffix implements IStyleName {

        TenantSureLogo;

    }

    private static final I18n i18n = I18n.get(TenantSureInsuranceStatusViewer.class);

    @Override
    public IsWidget createContent(TenantSureTenantInsuranceStatusShortDTO tenantSureStatus) {
        FlowPanel contentPanel = new FlowPanel();
        Image tenantSureLogo = new Image(TenantSureResources.INSTANCE.logoTenantSure());
        tenantSureLogo.addStyleName(STYLE_PREFIX + StyleSuffix.TenantSureLogo.name());
        contentPanel.add(tenantSureLogo);

        contentPanel.add(new Label(i18n.tr("Personal Liablity: ${0}", tenantSureStatus.liabilityCoverage().getStringView())));
        contentPanel.add(new Label(i18n.tr("Monthly Premium: ${0}", tenantSureStatus.monthlyPremiumPayment().getStringView())));
        if (!tenantSureStatus.nextPaymentDate().isNull()) {
            contentPanel.add(new Label(i18n.tr("Next Payment Date: {0}", tenantSureStatus.nextPaymentDate().getStringView())));
        }

        if (!tenantSureStatus.messages().isEmpty()) {
            contentPanel.add(new TenantSureStatusForm.TenantSureMessagesViewer().createContent(tenantSureStatus.messages()));
            Anchor goToTenantSureScreen = new Anchor(i18n.tr("Go To TenantSure Management Page"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.TenantInsurance.TenantSure.Management());
                }
            });
            contentPanel.add(goToTenantSureScreen);

        }

        return contentPanel;
    }
}
