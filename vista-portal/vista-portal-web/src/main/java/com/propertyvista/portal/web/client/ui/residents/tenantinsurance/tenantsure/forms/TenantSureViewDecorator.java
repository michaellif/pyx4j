/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.forms;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.web.client.themes.TenantSureTheme;

public class TenantSureViewDecorator extends Composite {

    private final static I18n i18n = I18n.get(TenantSureViewDecorator.class);

    private final SimplePanel content;

    private final Anchor privacyPolicyAnchor;

    private final Anchor compensationDisclosureStatementAnchor;

    public TenantSureViewDecorator() {
        FlowPanel tenantSureDecoratorPanel = new FlowPanel();
        tenantSureDecoratorPanel.setStyleName(TenantSureTheme.StyleName.TenantSureDecoratorPanel.name());

        SimplePanel header = new SimplePanel();
        header.setStyleName(TenantSureTheme.StyleName.TenantSureDecoratorHeader.name());
        header.add(new TenantSureLogo());
        tenantSureDecoratorPanel.add(header);

        content = new SimplePanel();
        content.setStyleName(TenantSureTheme.StyleName.TenantSureContentPanel.name());
        tenantSureDecoratorPanel.add(content);

        FlowPanel footer = new FlowPanel();
        footer.setStyleName(TenantSureTheme.StyleName.TenantSureDecoratorFooter.name());

        SimplePanel privacyPolicyAnchorHolder = new SimplePanel();
        privacyPolicyAnchorHolder.setStyleName(TenantSureTheme.StyleName.TenantSureTermsLink.name());
        privacyPolicyAnchorHolder.addStyleName(TenantSureTheme.StyleName.TenantSurePrivacyPolicyLink.name());

        privacyPolicyAnchor = new Anchor(i18n.tr("Privacy Policy"));
        privacyPolicyAnchor.setTarget("_blank");
        privacyPolicyAnchorHolder.setWidget(privacyPolicyAnchor);
        footer.add(privacyPolicyAnchorHolder);

        SimplePanel billingAndCancellationsPolicyAnchorHolder = new SimplePanel();
        billingAndCancellationsPolicyAnchorHolder.setStyleName(TenantSureTheme.StyleName.TenantSureTermsLink.name());
        billingAndCancellationsPolicyAnchorHolder.addStyleName(TenantSureTheme.StyleName.TenantSureBillingAndCancellationsPolicyLink.name());

        compensationDisclosureStatementAnchor = new Anchor(i18n.tr("Compensation Disclosure Statement"));
        compensationDisclosureStatementAnchor.setTarget("_blank");
        billingAndCancellationsPolicyAnchorHolder.setWidget(compensationDisclosureStatementAnchor);
        footer.add(billingAndCancellationsPolicyAnchorHolder);

        tenantSureDecoratorPanel.add(footer);

        initWidget(tenantSureDecoratorPanel);
    }

    public final void setPrivacyPolcyAddress(String href) {
        privacyPolicyAnchor.setHref(href);
    }

    public final void setCompensationDisclosureStatement(String href) {
        compensationDisclosureStatementAnchor.setHref(href);
    }

    public final void setContent(Widget contentWidget) {
        content.setWidget(contentWidget);
    }

    public final void setContent(IsWidget contentWidget) {
        setContent(contentWidget.asWidget());
    }
}
