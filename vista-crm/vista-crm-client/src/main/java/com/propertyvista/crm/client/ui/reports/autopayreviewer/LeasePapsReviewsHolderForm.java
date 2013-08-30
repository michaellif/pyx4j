/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapsReviewsHolderDTO;

public final class LeasePapsReviewsHolderForm extends CEntityDecoratableForm<LeasePapsReviewsHolderDTO> {

    private final static I18n i18n = I18n.get(LeasePapsReviewFolder.class);

    public enum Styles implements IStyleName {

        AutoPaySuperCaptionsPanel, AutoPayCaptionsPanel

    }

    public LeasePapsReviewsHolderForm() {
        super(LeasePapsReviewsHolderDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();
        FlowPanel superCaptionsPanel = new FlowPanel();
        superCaptionsPanel.addStyleName(Styles.AutoPaySuperCaptionsPanel.name());
        superCaptionsPanel.add(new HTML(i18n.tr("Suspended")));
        superCaptionsPanel.add(new HTML(i18n.tr("Suggested")));
        panel.add(superCaptionsPanel);

        FlowPanel captionsPanel = new FlowPanel();
        captionsPanel.addStyleName(Styles.AutoPayCaptionsPanel.name());
        captionsPanel.add(new HTML(i18n.tr("Price")));
        captionsPanel.add(new HTML(i18n.tr("Payment")));
        captionsPanel.add(new HTML(i18n.tr("%")));
        captionsPanel.add(new HTML(i18n.tr("Price")));
        captionsPanel.add(new HTML(i18n.tr("Payment")));
        captionsPanel.add(new HTML(i18n.tr("%")));

        panel.add(captionsPanel);
        panel.add(inject(proto().leasePapsReviews(), new LeasePapsReviewFolder()));
        return panel;
    }
}