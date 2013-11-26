/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.l1generation.visors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.visor.AbstractVisorEditor;
import com.pyx4j.site.client.ui.visor.IVisorEditor;

import com.propertyvista.crm.client.ui.tools.l1generation.forms.L1LandlordsContactInfoFolder;
import com.propertyvista.crm.client.ui.tools.l1generation.forms.L1ScheduleAndPaymentForm;
import com.propertyvista.crm.client.ui.tools.l1generation.forms.L1SignatureDataForm;
import com.propertyvista.crm.client.ui.tools.l1generation.forms.LtbAgentContactInfoForm;
import com.propertyvista.crm.rpc.dto.legal.l1.L1CommonFieldsDTO;

public class L1CommonFormDataVisorView extends AbstractVisorEditor<L1CommonFieldsDTO> {

    private static final I18n i18n = I18n.get(L1CommonFormDataVisorView.class);

    public L1CommonFormDataVisorView(IVisorEditor.Controller controller) {
        super(controller);
        setForm(new CEntityForm<L1CommonFieldsDTO>(L1CommonFieldsDTO.class) {
            @Override
            public IsWidget createContent() {
                TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
                int row = -1;
                panel.setH1(++row, 0, 2, i18n.tr("Landlords"));
                panel.setWidget(++row, 0, 2, inject(proto().landlordsContactInfos(), new L1LandlordsContactInfoFolder()));
                panel.setH1(++row, 0, 2, i18n.tr("Agent"));
                panel.setWidget(++row, 0, 2, inject(proto().agentContactInfo(), new LtbAgentContactInfoForm()));
                panel.setH1(++row, 0, 2, i18n.tr("Signature"));
                panel.setWidget(++row, 0, 2, inject(proto().signatureData(), new L1SignatureDataForm()));
                panel.setH1(++row, 0, 2, i18n.tr("Payment and Application Scheduling"));
                panel.setWidget(++row, 0, 2, inject(proto().paymentAndScheduling(), new L1ScheduleAndPaymentForm()));
                return panel;
            }
        });
        asWidget().addStyleName(L1VisorStyles.L1GenerationVisor.name());
    }

}
