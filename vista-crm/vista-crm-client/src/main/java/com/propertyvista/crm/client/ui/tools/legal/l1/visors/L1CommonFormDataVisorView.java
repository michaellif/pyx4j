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
package com.propertyvista.crm.client.ui.tools.legal.l1.visors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.visor.AbstractVisorEditor;
import com.pyx4j.site.client.ui.visor.IVisorEditor;

import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1LandlordsContactInfoFolder;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1ScheduleAndPaymentForm;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.L1SignatureDataForm;
import com.propertyvista.crm.client.ui.tools.legal.l1.forms.LtbAgentContactInfoForm;
import com.propertyvista.crm.rpc.dto.legal.l1.L1CommonFieldsDTO;

public class L1CommonFormDataVisorView extends AbstractVisorEditor<L1CommonFieldsDTO> {

    private static final I18n i18n = I18n.get(L1CommonFormDataVisorView.class);

    public L1CommonFormDataVisorView(IVisorEditor.Controller controller) {
        super(controller);
        setForm(new CForm<L1CommonFieldsDTO>(L1CommonFieldsDTO.class) {
            @Override
            protected IsWidget createContent() {
                FormPanel panel = new FormPanel(this);
                panel.h1(i18n.tr("Landlords"));
                panel.append(Location.Dual, proto().landlordsContactInfos(), new L1LandlordsContactInfoFolder());
                panel.h1(i18n.tr("Agent"));
                panel.append(Location.Dual, proto().agentContactInfo(), new LtbAgentContactInfoForm());
                panel.h1(i18n.tr("Signature"));
                panel.append(Location.Dual, proto().signatureData(), new L1SignatureDataForm());
                panel.h1(i18n.tr("Payment and Application Scheduling"));
                panel.append(Location.Dual, proto().paymentAndScheduling(), new L1ScheduleAndPaymentForm());
                return panel;
            }
        });
        asWidget().addStyleName(L1VisorStyles.L1GenerationVisor.name());
    }

}
