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

import java.util.Arrays;
import java.util.List;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.portal.rpc.portal.resident.communication.CommunicationMessageDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityWizard;

public class CommunicationMessageWizard extends CPortalEntityWizard<CommunicationMessageDTO> {

    private final static I18n i18n = I18n.get(CommunicationMessageWizard.class);

    List<PropertyContact> meta;

    public CommunicationMessageWizard(CommunicationMessageWizardView view) {
        super(CommunicationMessageDTO.class, view, i18n.tr("New Message"), i18n.tr("Submit"), ThemeColor.contrast5);

        addStep(createDetailsStep(), i18n.tr("General"));
    }

    public void setDestinationMeta(List<PropertyContact> meta) {
        this.meta = meta;
    }

    public TwoColumnFlexFormPanel createDetailsStep() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setH1(++row, 0, 1, "Details");
        content.setWidget(++row, 0, inject(proto().subject(), new FieldDecoratorBuilder(20).build()));
        CComboBoxBoolean cmbBoolean = new CComboBoxBoolean();
        cmbBoolean.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));

        content.setWidget(++row, 0, inject(proto().isHighImportance(), cmbBoolean, new FieldDecoratorBuilder(20).build()));
        content.setWidget(++row, 0, (inject(proto().text(), new FieldDecoratorBuilder(20).build())));
        content.setBR(++row, 0, 1);
        content.setH1(++row, 0, 1, "Attachments");
        content.setWidget(++row, 0, inject(proto().attachments(), new CommunicationMessageAttachmentFolder()));

        return content;
    }
}