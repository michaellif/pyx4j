/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionForm;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.dto.LeaseDTO;

public class ConcessionFolder extends VistaBoxFolder<Concession> {

    private static final I18n i18n = I18n.get(ConcessionFolder.class);

    private final CEntityForm<? extends LeaseDTO> lease;

    public ConcessionFolder(boolean modifyable, CEntityForm<? extends LeaseDTO> lease) {
        super(Concession.class, modifyable);
        this.lease = lease;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Concession) {
            return new ConcessionForm(true);
        }
        return super.create(member);
    }

    @Override
    public IFolderItemDecorator<Concession> createItemDecorator() {
        BoxFolderItemDecorator<Concession> decor = (BoxFolderItemDecorator<Concession>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected void addItem() {
        if (lease.getValue().version().leaseProducts().serviceItem().isNull()) {
            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select A Service Item First"));
        } else {

            new EntitySelectorListDialog<Concession>(i18n.tr("Select Concessions"), true, lease.getValue().selectedConcessions()) {
                @Override
                public boolean onClickOk() {
                    for (Concession item : getSelectedItems()) {
                        addItem(item);
                    }
                    return true;
                }
            }.show();
        }

    }
}