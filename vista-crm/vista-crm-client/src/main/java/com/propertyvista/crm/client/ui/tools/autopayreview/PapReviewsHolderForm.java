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
package com.propertyvista.crm.client.ui.tools.autopayreview;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.tools.common.ItemsHolderForm;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;

public class PapReviewsHolderForm extends ItemsHolderForm<PapReviewDTO, PapReviewsHolder> {

    private final static I18n i18n = I18n.get(PapReviewsHolderForm.class);

    public enum Styles implements IStyleName {

        AutoPaySuperCaptionsPanel, AutoPayCaptionsPanel;

    }

    public PapReviewsHolderForm() {
        super(PapReviewsHolder.class);
    }

    @Override
    protected Widget createHeaderPanel() {
        FlowPanel tableHeaderPanel = new FlowPanel();

        FlowPanel superCaptionsPanel = new FlowPanel();
        superCaptionsPanel.addStyleName(Styles.AutoPaySuperCaptionsPanel.name());
        superCaptionsPanel.add(new HTML(i18n.tr("Previous")));
        superCaptionsPanel.add(new HTML(i18n.tr("Current")));
        tableHeaderPanel.add(superCaptionsPanel);

        FlowPanel captionsPanel = new FlowPanel();
        captionsPanel.setStylePrimaryName(Styles.AutoPayCaptionsPanel.name());
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Payment")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("% of Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Payment")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("% of Charge")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("% of Change")), PapReviewFolder.Styles.AutoPayChargeNumberColumn.name()));
        tableHeaderPanel.add(captionsPanel);

        return tableHeaderPanel;
    }

    @Override
    protected CEntityFolder<PapReviewDTO> createItemsFolder() {
        return new PapReviewFolder();
    }

}