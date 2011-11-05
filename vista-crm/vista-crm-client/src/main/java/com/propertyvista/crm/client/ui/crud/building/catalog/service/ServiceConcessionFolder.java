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
package com.propertyvista.crm.client.ui.crud.building.catalog.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.ServiceConcession;

class ServiceConcessionFolder extends VistaTableFolder<ServiceConcession> {

    private final IListerView<Concession> concessionListerVeiw;

    public ServiceConcessionFolder(boolean modifyable, IListerView<Concession> concessionListerVeiw) {
        super(ServiceConcession.class, modifyable);
        this.concessionListerVeiw = concessionListerVeiw;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().concession(), "50em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ServiceConcession) {
            return new ServiceConcessionEditor();
        }
        return super.create(member);

    }

    @Override
    protected void addItem() {
        new ShowPopUpBox<SelectConcessionBox>(new SelectConcessionBox()) {
            @Override
            protected void onClose(SelectConcessionBox box) {
                if (box.getSelectedConcessions() != null) {
                    for (Concession item : box.getSelectedConcessions()) {
                        ServiceConcession newItem = EntityFactory.create(ServiceConcession.class);
                        newItem.concession().set(item);
                        addItem(newItem);
                    }
                }
            }
        };
    }

    @Override
    protected IFolderDecorator<ServiceConcession> createDecorator() {
        TableFolderDecorator<ServiceConcession> decor = (TableFolderDecorator<ServiceConcession>) super.createDecorator();
        decor.setShowHeader(false);
        return decor;
    }

    private class ServiceConcessionEditor extends CEntityFolderRowEditor<ServiceConcession> {

        public ServiceConcessionEditor() {
            super(ServiceConcession.class, columns());
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().concession()) {
                return inject(column.getObject(), new CEntityLabel());
            }
            return super.createCell(column);
        }
    }

    private class SelectConcessionBox extends OkCancelBox {

        private List<Concession> selectedConcessions;

        public SelectConcessionBox() {
            super(i18n.tr("Select Concessions"));
            concessionListerVeiw.getLister().releaseSelection();
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);
            concessionListerVeiw.getLister().addItemSelectionHandler(new ItemSelectionHandler<Concession>() {
                @Override
                public void onSelect(Concession selectedItem) {
                    okButton.setEnabled(!concessionListerVeiw.getLister().getSelectedItems().isEmpty());
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(concessionListerVeiw.asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("700px", "200px");
        }

        @Override
        protected boolean onOk() {
            selectedConcessions = concessionListerVeiw.getLister().getSelectedItems();
            return true;
        }

        @Override
        protected void onCancel() {
            selectedConcessions = null;
        }

        protected List<Concession> getSelectedConcessions() {
            return selectedConcessions;
        }
    }
}