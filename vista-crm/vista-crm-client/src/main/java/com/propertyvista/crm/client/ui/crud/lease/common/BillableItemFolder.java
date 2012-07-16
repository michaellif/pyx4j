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
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.dto.LeaseDTO;

public class BillableItemFolder extends VistaBoxFolder<BillableItem> {

    private static final I18n i18n = I18n.get(BillableItemFolder.class);

    private final CEntityForm<? extends LeaseDTO> lease;

    private final IEditorView<? extends LeaseDTO> leaseEditorView;

    public BillableItemFolder(boolean modifyable, CEntityForm<? extends LeaseDTO> lease, IEditorView<? extends LeaseDTO> leaseEditorView) {
        super(BillableItem.class, modifyable);
        this.lease = lease;
        this.leaseEditorView = leaseEditorView;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof BillableItem) {
            return new BillableItemEditor(lease, leaseEditorView);
        }
        return super.create(member);
    }

    @Override
    public IFolderItemDecorator<BillableItem> createItemDecorator() {
        BoxFolderItemDecorator<BillableItem> decor = (BoxFolderItemDecorator<BillableItem>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected void addItem() {
        if (lease.getValue().version().leaseProducts().serviceItem().isNull()) {
            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("You Must Select A Service Item First"));
        } else {
            new EntitySelectorListDialog<ProductItem>(i18n.tr("Select Features"), true, lease.getValue().selectedFeatureItems()) {
                @Override
                public boolean onClickOk() {
                    for (ProductItem item : getSelectedItems()) {
                        ((LeaseEditorViewBase.Presenter) leaseEditorView.getPresenter()).createBillableItem(new DefaultAsyncCallback<BillableItem>() {
                            @Override
                            public void onSuccess(BillableItem result) {
                                addItem(result);
                            }
                        }, item);
                    }
                    return true;
                }
            }.show();
        }
    }
}