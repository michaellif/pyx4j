/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.policy.policies.domain.LegalTermsContent;

public class LegalTermsContentViewer extends CEntityViewer<LegalTermsContent> {

    private final String contentHeight;

    public LegalTermsContentViewer(String contentHeight) {
        this.contentHeight = contentHeight;
    }

    @Override
    public IsWidget createContent(LegalTermsContent legalTermsContent) {
        FlowPanel panel = new FlowPanel();
        Label label = new Label();
        label.setText(legalTermsContent.localizedCaption().getValue());

        panel.add(label);

        HTML content = new HTML();
        content.setHTML(legalTermsContent.content().getValue());

        ScrollPanel contentHolder = new ScrollPanel();
        contentHolder.setHeight(contentHeight);
        contentHolder.setWidget(content);
        panel.add(contentHolder);

        return panel;
    }

    @Override
    public ValidationResults getValidationResults() {
        return new ValidationResults();
    }
}
