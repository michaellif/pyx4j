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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.MessageDTO;

public class MessageEditForm extends CrmEntityForm<MessageDTO> {

    private static final I18n i18n = I18n.get(MessageEditForm.class);

    private final CommunicationEndpointFolder receiverSelector;

    public MessageEditForm(IForm<MessageDTO> view) {
        super(MessageDTO.class, view);
        setTabBarVisible(false);
        receiverSelector = new CommunicationEndpointFolder(this);
        selectTab(addTab(createGeneralForm(), i18n.tr("New message")));
        inheritEditable(true);
        inheritViewable(false);
        inheritEnabled(true);
        setEnabled(true);
    }

    public IsWidget createGeneralForm() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().subject()).decorate();
        formPanel.append(Location.Dual, proto().topic()).decorate();
        formPanel.append(Location.Left, proto().allowedReply()).decorate();
        formPanel.append(Location.Right, proto().highImportance()).decorate();
        formPanel.h3("To");
        formPanel.append(Location.Dual, proto().to(), receiverSelector);
        formPanel.br();
        formPanel.append(Location.Dual, proto().text()).decorate();
        formPanel.append(Location.Dual, proto().attachments(), new MessageAttachmentFolder());
        formPanel.br();

        return formPanel;
    }

    @Override
    public boolean isValid() {
        if (getValue() != null && getValue().to() != null && getValue().to().size() < 1) {
            return false;
        }
        return super.isValid();
    }
}
