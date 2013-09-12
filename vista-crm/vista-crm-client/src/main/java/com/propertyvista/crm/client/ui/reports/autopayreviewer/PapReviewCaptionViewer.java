/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewCaptionDTO;

public class PapReviewCaptionViewer extends CViewer<PapReviewCaptionDTO> {

    private static final I18n i18n = I18n.get(PapReviewCaptionViewer.class);

    public enum Styles implements IStyleName {

        AutoPayReviewCaptionPanel, AutoPayReviewCaption, AutoPayReviewCaptionWarning

    }

    public PapReviewCaptionViewer() {
        setViewable(true);
        setEditable(false);
    }

    @Override
    public IsWidget createContent(PapReviewCaptionDTO value) {
        FlowPanel papCaptionPanel = new FlowPanel();
        papCaptionPanel.setStyleName(Styles.AutoPayReviewCaptionPanel.name());
        HTML caption = new HTML(//@formatter:off
                i18n.tr("{0} {1} {2} (Expected Move Out: {3,date,short}): {4} {5}. Due {6,date,short}",
                        value.building().getValue(),
                        value.unit().getValue(),
                        value.lease().getValue(),
                        value.expectedMoveOut().getValue(),
                        value.tenant().getValue(),
                        value.paymentMethod().getValue(),
                        value.paymentDue().getValue()
        ));//@formatter:on
        caption.setStyleName(Styles.AutoPayReviewCaption.name());
        papCaptionPanel.add(caption);

        if (value.hasLeaseWithOtherPaps().isBooleanTrue()) {
            HTML warning = new HTML(i18n.tr("This lease has more than one AutoPay"));
            warning.setStyleName(Styles.AutoPayReviewCaptionWarning.name());
            papCaptionPanel.add(warning);
        }
        return papCaptionPanel;
    }

}
