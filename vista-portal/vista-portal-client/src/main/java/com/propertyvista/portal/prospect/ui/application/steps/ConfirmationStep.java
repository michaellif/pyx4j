/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.ResidentPortalTerms;
import com.propertyvista.portal.shared.ui.TermsAnchor;

public class ConfirmationStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(ConfirmationStep.class);

    public ConfirmationStep() {
        super(OnlineApplicationWizardStepMeta.Confirmation);
    }

    @Override
    public BasicFlexFormPanel createStepContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(getStepTitle());
        int row = -1;

        // TODO : real content goes here...

        panel.setWidget(++row, 0, createLegalTermsPanel());

        return panel;
    }

    private Widget createLegalTermsPanel() {

        SafeHtmlBuilder legalTermsBuilder = new SafeHtmlBuilder();
        final String termsOfUseAnchorId = HTMLPanel.createUniqueId();
        final String billingPolicyAnchorId = HTMLPanel.createUniqueId();
        legalTermsBuilder.appendHtmlConstant(i18n.tr("Be informed that you are acknowledging our {0} and {1}.", "<span id=\"" + termsOfUseAnchorId
                + "\"></span>", "<span id=\"" + billingPolicyAnchorId + "\"></span>"));

        final HTMLPanel legalTermsLinkPanel = new HTMLPanel(legalTermsBuilder.toSafeHtml());

        Anchor termsOfUseAnchor = new TermsAnchor(i18n.tr("Terms Of Use"), PortalSiteMap.PortalTermsAndConditions.class);
        legalTermsLinkPanel.addAndReplaceElement(termsOfUseAnchor, termsOfUseAnchorId);

        Anchor billingPolicyAnchor = new TermsAnchor(i18n.tr("Billing And Refund Policy"), PortalSiteMap.BillingTerms.class);
        legalTermsLinkPanel.addAndReplaceElement(billingPolicyAnchor, billingPolicyAnchorId);

        return legalTermsLinkPanel;
    }
}
