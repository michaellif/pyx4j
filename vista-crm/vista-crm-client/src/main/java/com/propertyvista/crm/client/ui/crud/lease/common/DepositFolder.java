/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.form.IEditorView;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositStatus;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.dto.LeaseDTO;

class DepositFolder extends VistaTableFolder<Deposit> {

    static final I18n i18n = I18n.get(DepositFolder.class);

    private final CEntityForm<? extends LeaseDTO> lease;

    private final IEditorView<? extends LeaseDTO> leaseEditorView;

    public DepositFolder(CEntityForm<? extends LeaseDTO> lease, IEditorView<? extends LeaseDTO> leaseEditorView, boolean modifiable) {
        super(Deposit.class, BillableItemEditor.i18n.tr("Deposit"), modifiable);
        this.lease = lease;
        this.leaseEditorView = leaseEditorView;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "12em"));
        columns.add(new EntityFolderColumnDescriptor(proto().status(), "8em"));
        columns.add(new EntityFolderColumnDescriptor(proto().depositDate(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().refundDate(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().initialAmount(), "7em"));
        columns.add(new EntityFolderColumnDescriptor(proto().currentAmount(), "7em"));
        columns.add(new EntityFolderColumnDescriptor(proto().description(), "15em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Deposit) {
            return new BillableItemDepositEditor();
        }
        return super.create(member);
    }

    List<BillableItem> getLeaseBilableItems() {
        List<BillableItem> items = new LinkedList<BillableItem>();

        items.add((BillableItem) lease.getValue().version().leaseProducts().serviceItem().detach());
        for (BillableItem item : lease.getValue().version().leaseProducts().featureItems()) {
            items.add((BillableItem) item.detach());
        }

        return items;
    }

    @Override
    protected void addItem() {
        new EntitySelectorListDialog<BillableItem>(i18n.tr("Select Item"), false, getLeaseBilableItems()) {
            @Override
            public boolean onClickOk() {
                if (!getSelectedItems().isEmpty()) {
                    new SelectEnumDialog<DepositType>(BillableItemEditor.i18n.tr("Select Deposit Type"), EnumSet.allOf(DepositType.class)) {
                        @Override
                        public boolean onClickOk() {
                            createNewItem(new DefaultAsyncCallback<Deposit>() {
                                @Override
                                public void onSuccess(Deposit result) {
                                    addItem(result);
                                }
                            }, getSelectedType(), getSelectedItems().get(0));
                            return true;
                        }
                    }.show();
                    return true;
                }
                return false;
            }
        }.show();
    }

    private void createNewItem(final AsyncCallback<Deposit> callback, final DepositType type, BillableItem billableItem) {
        if (leaseEditorView != null) {
            ((LeaseEditorViewBase.Presenter) leaseEditorView.getPresenter()).createDeposit(new DefaultAsyncCallback<Deposit>() {
                @Override
                public void onSuccess(Deposit result) {
                    if (result == null) { // if there is no deposits of such type - create it 'on the fly':
                        result = EntityFactory.create(Deposit.class);
                        result.type().setValue(type);
                        result.status().setValue(DepositStatus.Created);
                        result.depositDate().setValue(new LogicalDate());
                    }
                    callback.onSuccess(result);
                }
            }, type, billableItem);
        }
    }

    private class BillableItemDepositEditor extends CEntityFolderRowEditor<Deposit> {

        public BillableItemDepositEditor() {
            super(Deposit.class, columns());
            setViewable(false);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (isEditable()) {
                if (column.getObject() == proto().type() || column.getObject() == proto().status() || column.getObject() == proto().depositDate()
                        || column.getObject() == proto().refundDate() || column.getObject() == proto().currentAmount()) {
                    CComponent<?, ?> comp = inject(column.getObject());
                    comp.inheritEditable(false); // always not editable!
                    comp.setEditable(false);
                    return comp;
                }
            }
            return super.createCell(column);
        }
    }
}