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
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CLabel;

import com.propertyvista.crm.client.ui.tools.autopayreview.MiniDecorator;
import com.propertyvista.crm.client.ui.tools.common.BulkEditableEntityForm;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;

public class LegalNoticeCandidateForm extends BulkEditableEntityForm<LegalNoticeCandidateDTO> {

    public enum Styles implements IStyleName {

        LegalNoticeCandidate, LegalNoticeCandidateDataColumn, LegalNoticeCandidateDataColumnLong, LegalNoticeCandidateDataNumberColumn

    }

    public LegalNoticeCandidateForm() {
        super(LegalNoticeCandidateDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();
        panel.setStyleName(Styles.LegalNoticeCandidate.name());

        panel.add(inject(proto().isSelected()));

        panel.add(new MiniDecorator(inject(proto().building()), Styles.LegalNoticeCandidateDataColumn.name()));
        get(proto().building()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().address()), Styles.LegalNoticeCandidateDataColumnLong.name()));
        get(proto().address()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().unit()), Styles.LegalNoticeCandidateDataColumn.name()));
        get(proto().unit()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().leaseIdString()), Styles.LegalNoticeCandidateDataColumn.name()));
        get(proto().leaseIdString()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().moveIn()), Styles.LegalNoticeCandidateDataColumn.name()));
        get(proto().moveIn()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().moveOut()), Styles.LegalNoticeCandidateDataColumn.name()));
        get(proto().moveOut()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().amountOwed()), Styles.LegalNoticeCandidateDataColumn.name(), Styles.LegalNoticeCandidateDataNumberColumn
                .name()));
        get(proto().amountOwed()).setViewable(true);

        panel.add(new MiniDecorator(inject(proto().n4Issued(), new CLabel<Integer>()), Styles.LegalNoticeCandidateDataColumn.name(),
                Styles.LegalNoticeCandidateDataNumberColumn.name()));
        get(proto().n4Issued()).setViewable(true);

        return panel;
    }
}
