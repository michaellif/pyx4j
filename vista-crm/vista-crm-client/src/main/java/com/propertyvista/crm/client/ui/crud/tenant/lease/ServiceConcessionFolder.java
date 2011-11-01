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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.dto.LeaseDTO;

class ServiceConcessionFolder extends VistaTableFolder<ServiceConcession> {

    private final CEntityEditor<LeaseDTO> parent;

    public ServiceConcessionFolder(boolean modifyable, CEntityEditor<LeaseDTO> parent) {
        super(ServiceConcession.class, modifyable);
        this.parent = parent;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().concession(), "50em"));
        return columns;
    }

    @Override
    protected void addItem() {
        if (parent.getValue().serviceAgreement().serviceItem().isNull()) {
            MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Select Service Item first!"));
        } else {
            new ShowPopUpBox<SelectConcessionBox>(new SelectConcessionBox()) {
                @Override
                protected void onClose(SelectConcessionBox box) {
                    if (box.getSelectedItems() != null) {
                        for (Concession item : box.getSelectedItems()) {
                            ServiceConcession newItem = EntityFactory.create(ServiceConcession.class);
                            newItem.concession().set(item);
                            addItem(newItem);
                        }
                    }
                }
            };
        }
    }

    @Override
    protected IFolderDecorator<ServiceConcession> createDecorator() {
        TableFolderDecorator<ServiceConcession> decor = (TableFolderDecorator<ServiceConcession>) super.createDecorator();
        decor.setShowHeader(false);
        return decor;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ServiceConcession) {
            return new ServiceConcessionEditor();
        }
        return super.create(member);
    }

    private class ServiceConcessionEditor extends CEntityFolderRowEditor<ServiceConcession> {

        public ServiceConcessionEditor() {
            super(ServiceConcession.class, columns());
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().concession()) {
                return inject(column.getObject(), new CEntityLabel());
            }
            return super.createCell(column);
        }
    }

    private class SelectConcessionBox extends OkCancelBox {

        private ListBox list;

        private List<Concession> selectedItems;

        public SelectConcessionBox() {
            super(i18n.tr("Select Concessions"));
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);

            if (!parent.getValue().selectedConcessions().isEmpty()) {
                list = new ListBox(true);
                list.addChangeHandler(new ChangeHandler() {
                    @Override
                    public void onChange(ChangeEvent event) {
                        okButton.setEnabled(list.getSelectedIndex() >= 0);
                    }
                });

                List<Concession> alreadySelected = new ArrayList<Concession>();
                for (ServiceConcession item : parent.getValue().serviceAgreement().concessions()) {
                    alreadySelected.add(item.concession());
                }

                for (Concession item : parent.getValue().selectedConcessions()) {
                    if (!alreadySelected.contains(item)) {
                        list.addItem(item.getStringView(), item.id().toString());
                    }
                }

                if (list.getItemCount() > 0) {
                    list.setVisibleItemCount(8);
                    list.setWidth("100%");
                    return list.asWidget();
                } else {
                    return new HTML(i18n.tr("All Concessions have already been selected!"));
                }
            } else {
                return new HTML(i18n.tr("There are no concessions for this service!"));
            }
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        @Override
        protected boolean onOk() {
            selectedItems = new ArrayList<Concession>(4);
            for (int i = 0; i < list.getItemCount(); ++i) {
                if (list.isItemSelected(i)) {
                    for (Concession item : parent.getValue().selectedConcessions()) {
                        if (list.getValue(i).contentEquals(item.id().toString())) {
                            selectedItems.add(item);
                        }
                    }
                }
            }
            return super.onOk();
        }

        @Override
        protected void onCancel() {
            selectedItems = null;
        }

        protected List<Concession> getSelectedItems() {
            return selectedItems;
        }
    }
}