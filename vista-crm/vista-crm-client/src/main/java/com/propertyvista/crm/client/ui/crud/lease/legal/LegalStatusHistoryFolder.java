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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.legal;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.legal.LegalStatus;

public abstract class LegalStatusHistoryFolder extends VistaBoxFolder<LegalStatus> {

    private static final I18n i18n = I18n.get(LegalStatusHistoryFolder.class);

    public LegalStatusHistoryFolder() {
        super(LegalStatus.class);
        inheritEditable(false);
        inheritViewable(false);

        setEditable(true);
        setViewable(false);

        setRemovable(true);
        setAddable(false);
        setOrderable(false);
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof LegalStatus) {
            LegalStatusForm form = new LegalStatusForm(false);
            form.inheritViewable(false);
            form.setViewable(true);
            return (T) form;
        }
        return super.create(member);
    }

    @Override
    protected void removeItem(final CEntityFolderItem<LegalStatus> item) {
        MessageDialog.confirm(i18n.tr("Remove Status"), i18n.tr("Are you sure"), new Command() {
            @Override
            public void execute() {
                LegalStatusHistoryFolder.super.removeItem(item);
                LegalStatusHistoryFolder.this.onRemoved(item.getValue());
            }
        });
    }

    @Override
    public IFolderItemDecorator<LegalStatus> createItemDecorator() {
        VistaBoxFolderItemDecorator<LegalStatus> d = (VistaBoxFolderItemDecorator<LegalStatus>) super.createItemDecorator();
        d.setExpended(false);
        return d;
    }

    protected abstract void onRemoved(LegalStatus item);

}
