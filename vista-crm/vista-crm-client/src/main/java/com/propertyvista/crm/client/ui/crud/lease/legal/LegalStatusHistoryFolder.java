/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-21
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.crud.lease.legal;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.legal.LegalStatus;
import com.propertyvista.dto.LegalStatusDTO;

public abstract class LegalStatusHistoryFolder extends VistaBoxFolder<LegalStatusDTO> {

    private static final I18n i18n = I18n.get(LegalStatusHistoryFolder.class);

    public LegalStatusHistoryFolder() {
        super(LegalStatusDTO.class);
        inheritEditable(false);
        inheritViewable(false);

        setEditable(true);
        setViewable(false);

        setRemovable(true);
        setAddable(false);
        setOrderable(false);
    }

    @Override
    protected CForm<LegalStatusDTO> createItemForm(IObject<?> member) {
        LegalStatusForm form = new LegalStatusForm(false);
        form.inheritViewable(false);
        form.setViewable(true);
        return form;
    }

    @Override
    protected void removeItem(final CFolderItem<LegalStatusDTO> item) {
        MessageDialog.confirm(i18n.tr("Remove Status"), i18n.tr("Are you sure"), new Command() {
            @Override
            public void execute() {
                LegalStatusHistoryFolder.super.removeItem(item);
                LegalStatusHistoryFolder.this.onRemoved(item.getValue());
            }
        });
    }

    @Override
    public VistaBoxFolderItemDecorator<LegalStatusDTO> createItemDecorator() {
        VistaBoxFolderItemDecorator<LegalStatusDTO> d = super.createItemDecorator();
        d.setExpended(false);
        return d;
    }

    protected abstract void onRemoved(LegalStatus item);

}
