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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.dialogs.SelectDialog;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.dto.LeaseDTO;

class BillableItemFolder extends VistaBoxFolder<BillableItem> {

    private static final I18n i18n = I18n.get(BillableItemFolder.class);

    final CrmEntityForm<LeaseDTO> parent;

    public BillableItemFolder(boolean modifyable, CrmEntityForm<LeaseDTO> parent) {
        super(BillableItem.class, modifyable);
        this.parent = parent;
    }

    @Override
    protected void addItem() {
        if (parent.getValue().serviceAgreement().serviceItem().isNull()) {
            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select A Service Item First"));
        } else {
            new SelectDialog<ProductItem>(i18n.tr("Select Features"), true, parent.getValue().selectedFeatureItems()) {
                @Override
                public boolean onClickOk() {
                    for (ProductItem item : getSelectedItems()) {
                        BillableItem newItem = EntityFactory.create(BillableItem.class);
                        newItem.item().set(item);
                        newItem._currentPrice().setValue(item.price().getValue());
                        newItem.effectiveDate().setValue(new LogicalDate());
                        newItem.expirationDate().setValue(new LogicalDate());
                        addItem(newItem);
                    }
                    return true;
                }

                @Override
                public String defineWidth() {
                    return "400px";
                }

                @Override
                public String defineHeight() {
                    return "200px";
                }
            }.show();
        }
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof BillableItem) {
            return new BillableItemEditor();
        }
        return super.create(member);
    }

}