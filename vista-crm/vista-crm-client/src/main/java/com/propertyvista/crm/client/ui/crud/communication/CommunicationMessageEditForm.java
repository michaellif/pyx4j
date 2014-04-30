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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.CommunicationMessageDTO;

public class CommunicationMessageEditForm extends CrmEntityForm<CommunicationMessageDTO> {

    private static final I18n i18n = I18n.get(CommunicationMessageEditForm.class);

    private final CommunicationEndpointFolder receiverSelector;

    public CommunicationMessageEditForm(IForm<CommunicationMessageDTO> view) {
        super(CommunicationMessageDTO.class, view);

        receiverSelector = new CommunicationEndpointFolder(this);
        selectTab(addTab(createGeneralForm(), i18n.tr("General")));
        inheritEditable(true);
        inheritViewable(false);
        inheritEnabled(true);
        setEnabled(true);
    }

    public IsWidget createGeneralForm() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().subject()).decorate();

        formPanel.h1("To");
        formPanel.append(Location.Left, proto().to(), receiverSelector);

        formPanel.h1("Message");
        CComboBoxBoolean cmbBoolean = new CComboBoxBoolean();
        cmbBoolean.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));
        formPanel.append(Location.Left, proto().isHighImportance(), cmbBoolean).decorate();
        formPanel.append(Location.Left, proto().text()).decorate();
        formPanel.append(Location.Left, proto().attachments(), new CommunicationMessageAttachmentFolder());
        formPanel.br();

        return formPanel;
    }
}
