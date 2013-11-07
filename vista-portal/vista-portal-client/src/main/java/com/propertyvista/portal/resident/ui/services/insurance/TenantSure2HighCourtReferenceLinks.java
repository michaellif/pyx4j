/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.resident.themes.TenantSureTheme;
import com.propertyvista.portal.shared.themes.NavigationAnchorTheme;

public class TenantSure2HighCourtReferenceLinks extends Composite {

    private static final I18n i18n = I18n.get(TenantSure2HighCourtReferenceLinks.class);

    private final Anchor compensationDisclosureStatementAnchor;

    private final Anchor privacyPolicyAnchor;

    public TenantSure2HighCourtReferenceLinks() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        panel.setWidth("100%");
        panel.setStyleName(TenantSureTheme.StyleName.TenantSure2HighCourtLinks.name());

        SimplePanel privacyPolicyAnchorHolder = new SimplePanel();
        privacyPolicyAnchorHolder.setStyleName(TenantSureTheme.StyleName.TenantSureTermsLink.name());
        privacyPolicyAnchorHolder.addStyleName(TenantSureTheme.StyleName.TenantSurePrivacyPolicyLink.name());

        privacyPolicyAnchor = new Anchor(i18n.tr("Privacy Policy"));
        privacyPolicyAnchor.setStyleName(NavigationAnchorTheme.StyleName.NavigationAnchor.name());
        privacyPolicyAnchor.setTarget("_blank");
        privacyPolicyAnchorHolder.setWidget(privacyPolicyAnchor);
        panel.setWidget(0, 0, privacyPolicyAnchorHolder);
        panel.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        panel.getFlexCellFormatter().setWidth(0, 0, "50%");

        SimplePanel compensationDisclosureStatementAnchorHolder = new SimplePanel();
        compensationDisclosureStatementAnchorHolder.setStyleName(TenantSureTheme.StyleName.TenantSureTermsLink.name());
        compensationDisclosureStatementAnchorHolder.addStyleName(TenantSureTheme.StyleName.TenantSureBillingAndCancellationsPolicyLink.name());

        compensationDisclosureStatementAnchor = new Anchor(i18n.tr("Compensation Disclosure Statement"));
        compensationDisclosureStatementAnchor.setStyleName(NavigationAnchorTheme.StyleName.NavigationAnchor.name());
        compensationDisclosureStatementAnchor.setTarget("_blank");
        compensationDisclosureStatementAnchorHolder.setWidget(compensationDisclosureStatementAnchor);
        panel.setWidget(0, 1, compensationDisclosureStatementAnchorHolder);
        panel.getFlexCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        panel.getFlexCellFormatter().setWidth(0, 1, "50%");

        initWidget(panel);

    }

    public final void setPrivacyPolcyHref(String href) {
        privacyPolicyAnchor.setHref(href);
    }

    public final void setCompensationDisclosureStatementHref(String href) {
        compensationDisclosureStatementAnchor.setHref(href);
    }

}
