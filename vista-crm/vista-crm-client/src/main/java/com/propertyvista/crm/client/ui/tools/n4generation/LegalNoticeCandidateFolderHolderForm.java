/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.n4generation;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.reports.autopayreviewer.MiniDecorator;
import com.propertyvista.crm.client.ui.tools.n4generation.base.ItemsHolderForm;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;

public class LegalNoticeCandidateFolderHolderForm extends ItemsHolderForm<LegalNoticeCandidateDTO, LegalNoticeCandidateHolder> {

    private static final I18n i18n = I18n.get(LegalNoticeCandidateHolder.class);

    public enum Styles implements IStyleName {

        LegalNoticeCandidateCaptions
    }

    public LegalNoticeCandidateFolderHolderForm() {
        super(LegalNoticeCandidateHolder.class);
    }

    @Override
    protected Widget createHeaderPanel() {
        FlowPanel captionsPanel = new FlowPanel();
        captionsPanel.setStylePrimaryName(Styles.LegalNoticeCandidateCaptions.name());
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Building")), LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Address")), LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataColumnLong.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Unit")), LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Lease ID")), LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Move In")), LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Move Out")), LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("Amount Owed")), LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataColumn.name(),
                LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataNumberColumn.name()));
        captionsPanel.add(new MiniDecorator(new HTML(i18n.tr("N4 Issued")), LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataColumn.name(),
                LegalNoticeCandidateForm.Styles.LegalNoticeCandidateDataNumberColumn.name()));
        return captionsPanel;
    }

    @Override
    protected CEntityFolder<LegalNoticeCandidateDTO> createItemsFolder() {
        return new LegalNoticeCandidateFolder();
    }

}
