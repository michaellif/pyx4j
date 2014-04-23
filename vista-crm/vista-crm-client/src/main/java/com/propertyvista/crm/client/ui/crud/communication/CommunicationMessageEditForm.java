/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.Arrays;

import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.CommunicationMessageDTO;

public class CommunicationMessageEditForm extends CrmEntityForm<CommunicationMessageDTO> {

    private static final I18n i18n = I18n.get(CommunicationMessageEditForm.class);

    private final CommunicationEndpointFolder receiverSelector;

    public CommunicationMessageEditForm(IForm<CommunicationMessageDTO> view) {
        super(CommunicationMessageDTO.class, view);

        receiverSelector = new CommunicationEndpointFolder(this);
        selectTab(addTab(createGeneralForm()));
        inheritEditable(true);
        inheritViewable(false);
        inheritEnabled(true);
        setEnabled(true);
    }

    public BasicFlexFormPanel createGeneralForm() {
        BasicFlexFormPanel mainPanel = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;
        mainPanel.setWidget(++row, 0, inject(proto().subject(), new FieldDecoratorBuilder(20).build()));
        mainPanel.setH1(++row, 0, 1, "To");
        mainPanel.setWidget(++row, 0, inject(proto().to(), receiverSelector));
        mainPanel.setH1(++row, 0, 1, "Message");
        CComboBoxBoolean cmbBoolean = new CComboBoxBoolean();
        cmbBoolean.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));
        mainPanel.setWidget(++row, 0, inject(proto().isHighImportance(), cmbBoolean, new FieldDecoratorBuilder(20).build()));
        mainPanel.setWidget(++row, 0, inject(proto().text(), new FieldDecoratorBuilder(20).build()));
        mainPanel.setWidget(++row, 0, inject(proto().attachments(), new CommunicationMessageAttachmentFolder()));
        mainPanel.setBR(++row, 0, 1);

        return mainPanel;
    }

}
