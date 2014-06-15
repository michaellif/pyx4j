/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.communication;

import java.util.List;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;

public class MessageWizard extends CPortalEntityWizard<MessageDTO> {

    private final static I18n i18n = I18n.get(MessageWizard.class);

    List<PropertyContact> meta;

    public MessageWizard(MessageWizardView view) {
        super(MessageDTO.class, view, i18n.tr("New Message"), i18n.tr("Submit"), ThemeColor.contrast5);

        addStep(createDetailsStep(), i18n.tr("General"));
    }

    public void setDestinationMeta(List<PropertyContact> meta) {
        this.meta = meta;
    }

    public FormPanel createDetailsStep() {
        FormPanel content = new FormPanel(this);

        content.h1(i18n.tr("Details"));
        content.append(Location.Left, proto().subject()).decorate();
        content.append(Location.Left, proto().highImportance(), new CCheckBox()).decorate();
        content.append(Location.Left, proto().text()).decorate();
        content.br();
        content.h1(i18n.tr("Attachments"));
        content.append(Location.Left, proto().attachments(), new MessageAttachmentFolder());

        return content;
    }
}