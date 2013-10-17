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

import com.propertyvista.crm.client.ui.tools.n4generation.base.BulkEditableEntityForm;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;

public class LegalNoticeCandidateForm extends BulkEditableEntityForm<LegalNoticeCandidateDTO> {

    public enum Styles implements IStyleName {

        LegalNoticeCandidateForm

    }

    public LegalNoticeCandidateForm() {
        super(LegalNoticeCandidateDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();
        panel.setStyleName(Styles.LegalNoticeCandidateForm.name());

        panel.add(inject(proto().isSelected()));

        panel.add(inject(proto().building()));
        get(proto().building()).setViewable(true);

        panel.add(inject(proto().address()));
        get(proto().address()).setViewable(true);

        panel.add(inject(proto().unit()));
        get(proto().unit()).setViewable(true);

        panel.add(inject(proto().leaseIdString()));
        get(proto().leaseIdString()).setViewable(true);

        panel.add(inject(proto().moveIn()));
        get(proto().moveIn()).setViewable(true);

        panel.add(inject(proto().moveOut()));
        get(proto().moveOut()).setViewable(true);

        panel.add(inject(proto().amountOwed()));
        get(proto().amountOwed()).setViewable(true);

        panel.add(inject(proto().n4Issued(), new CLabel<Integer>()));
        get(proto().n4Issued()).setViewable(true);

        return panel;
    }

}
