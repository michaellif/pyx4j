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

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.validators.ValidationResults;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.shared.rpc.LegalTermsTO;

public class LegalTermsContentViewer extends CViewer<LegalTermsTO> {

    public enum Styles implements IStyleName {
        LegalTermsContentViewerCaption, LegalTermsContentViewerHolder, LegalTermsContentViewerContent;
    }

    private final String contentHeight;

    public LegalTermsContentViewer(String contentHeight) {
        this.contentHeight = contentHeight;
    }

    @Override
    public IsWidget createContent(LegalTermsTO legalTermsContent) {
        FlowPanel contentPanel = new FlowPanel();
        Label caption = new Label();
        caption.addStyleName(Styles.LegalTermsContentViewerCaption.name());
        caption.setText(legalTermsContent.caption().getValue());

        contentPanel.add(caption);

        HTML content = new HTML();
        content.addStyleName(Styles.LegalTermsContentViewerContent.name());
        content.setHTML(legalTermsContent.content().getValue());

        ScrollPanel contentHolder = new ScrollPanel();
        contentHolder.addStyleName(Styles.LegalTermsContentViewerHolder.name());
        contentHolder.setHeight(contentHeight);
        contentHolder.setWidget(content);
        contentPanel.add(contentHolder);

        return contentPanel;
    }

    @Override
    public ValidationResults getValidationResults() {
        return new ValidationResults();
    }
}
