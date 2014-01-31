/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.landing;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.legal.TermsAndPoliciesType;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.shared.services.PortalTermsAndPoliciesService;
import com.propertyvista.portal.shared.ui.TermsAnchor;

public class TermsLinkPanel extends SimplePanel {

    static final I18n i18n = I18n.get(TermsLinkPanel.class);

    public TermsLinkPanel(final String buttonName) {

        Vector<TermsAndPoliciesType> termTypes = new Vector<TermsAndPoliciesType>();
        termTypes.add(TermsAndPoliciesType.PVProspectPortalTermsAndConditions);
        termTypes.add(TermsAndPoliciesType.PMCProspectPortalTermsAndConditions);

        GWT.<PortalTermsAndPoliciesService> create(PortalTermsAndPoliciesService.class).getTermCaptions(new DefaultAsyncCallback<Vector<String>>() {
            @Override
            public void onSuccess(Vector<String> result) {
                SafeHtmlBuilder loginTermsBuilder = new SafeHtmlBuilder();
                String portalTermsAndConditionsAnchorId = HTMLPanel.createUniqueId();
                String pmcTermsAndConditionsAnchorId = HTMLPanel.createUniqueId();

                if (result.get(1) == null) {
                    loginTermsBuilder.appendHtmlConstant(i18n.tr("By clicking {0}, you are acknowledging that you have read and agree to the {1}.", buttonName,
                            "<span id=\"" + portalTermsAndConditionsAnchorId + "\"></span>"));
                } else {
                    loginTermsBuilder.appendHtmlConstant(i18n.tr("By clicking {0}, you are acknowledging that you have read and agree to the {1} and {2}.",
                            buttonName, "<span id=\"" + portalTermsAndConditionsAnchorId + "\"></span>", "<span id=\"" + pmcTermsAndConditionsAnchorId
                                    + "\"></span>"));
                }

                HTMLPanel loginTermsLinkPanel = new HTMLPanel(loginTermsBuilder.toSafeHtml());
                setWidget(loginTermsLinkPanel);
                loginTermsLinkPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

                Anchor pvTermsAndConditionsAnchor = new TermsAnchor(result.get(0), PortalSiteMap.PortalTerms.VistaTermsAndConditions.class);
                loginTermsLinkPanel.addAndReplaceElement(pvTermsAndConditionsAnchor, portalTermsAndConditionsAnchorId);

                if (result.get(1) != null) {
                    Anchor pmcTermsAndConditionsAnchor = new TermsAnchor(result.get(1), PortalSiteMap.PortalTerms.PmcTermsAndConditions.class);
                    loginTermsLinkPanel.addAndReplaceElement(pmcTermsAndConditionsAnchor, pmcTermsAndConditionsAnchorId);
                }

            }
        }, termTypes);
    }

}
