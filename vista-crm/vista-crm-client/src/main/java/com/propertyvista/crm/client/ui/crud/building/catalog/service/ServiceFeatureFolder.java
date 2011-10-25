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

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceFeature;

class ServiceFeatureFolder extends VistaTableFolder<ServiceFeature> {

    private final IListerView<Feature> featureListerVeiw;

    public ServiceFeatureFolder(boolean modifyable, IListerView<Feature> featureListerVeiw) {
        super(ServiceFeature.class, modifyable);
        this.featureListerVeiw = featureListerVeiw;
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().feature(), "50em"));
        return columns;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ServiceFeature) {
            return new ServiceFeatureEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new ShowPopUpBox<SelectFeatureBox>(new SelectFeatureBox(featureListerVeiw)) {
            @Override
            protected void onClose(SelectFeatureBox box) {
                if (box.getSelectedFeatures() != null) {
                    for (Feature item : box.getSelectedFeatures()) {
                        ServiceFeature newItem = EntityFactory.create(ServiceFeature.class);
                        newItem.feature().set(item);
                        addItem(newItem);
                    }
                }
            }
        };
    }

    @Override
    protected IFolderDecorator<ServiceFeature> createDecorator() {
        TableFolderDecorator<ServiceFeature> decor = (TableFolderDecorator<ServiceFeature>) super.createDecorator();
        decor.setShowHeader(false);
        return decor;
    }

    class ServiceFeatureEditor extends CEntityFolderRowEditor<ServiceFeature> {

        public ServiceFeatureEditor() {
            super(ServiceFeature.class, columns());
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().feature()) {
                return inject(column.getObject(), new CEntityLabel());
            }
            return super.createCell(column);
        }
    }

    private class SelectFeatureBox extends OkCancelBox {

        private List<Feature> selectedFeatures;

        private final IListerView<Feature> featureListerVeiw;

        public SelectFeatureBox(IListerView<Feature> featureListerVeiw) {
            super(i18n.tr("Select Features"));
            this.featureListerVeiw = featureListerVeiw;
            featureListerVeiw.getLister().releaseSelection();
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);
            featureListerVeiw.getLister().addItemSelectionHandler(new ItemSelectionHandler<Feature>() {
                @Override
                public void onSelect(Feature selectedItem) {
                    okButton.setEnabled(!featureListerVeiw.getLister().getSelectedItems().isEmpty());
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(featureListerVeiw.asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("700px", "200px");
        }

        @Override
        protected boolean onOk() {
            selectedFeatures = featureListerVeiw.getLister().getSelectedItems();
            return true;
        }

        @Override
        protected void onCancel() {
            selectedFeatures = null;
        }

        protected List<Feature> getSelectedFeatures() {
            return selectedFeatures;
        }
    }
}