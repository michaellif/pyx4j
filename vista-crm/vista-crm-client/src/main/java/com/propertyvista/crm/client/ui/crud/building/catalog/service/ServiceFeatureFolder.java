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

import java.util.List;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.OkBox.OkResult;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.crm.client.ui.components.CrmViewersComponentFactory;
import com.propertyvista.crm.client.ui.crud.building.catalog.feature.FeatureEditorForm;
import com.propertyvista.domain.financial.offering.Feature;

class ServiceFeatureFolder extends VistaBoxFolder<Feature> {

    private final IListerView<Feature> featureListerVeiw;

    public ServiceFeatureFolder(boolean modifyable, IListerView<Feature> featureListerVeiw) {
        super(Feature.class, modifyable);
        this.featureListerVeiw = featureListerVeiw;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Feature) {
            return new FeatureEditorForm(new CrmViewersComponentFactory());
        }
        return super.create(member);
    }

    @Override
    public IFolderItemDecorator<Feature> createItemDecorator() {
        BoxFolderItemDecorator<Feature> decor = (BoxFolderItemDecorator<Feature>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    protected void addItem() {
        final SelectFeatureBox box = new SelectFeatureBox(featureListerVeiw);
        box.run(new OkResult() {
            @Override
            public void onOk() {
                for (Feature item : box.getSelectedItems()) {
                    addItem(item);
                }
            }
        });
    }

    private class SelectFeatureBox extends OkCancelBox {

        private List<Feature> selectedFeatures;

        private final IListerView<Feature> featureListerVeiw;

        public SelectFeatureBox(IListerView<Feature> featureListerVeiw) {
            super(i18n.tr("Select Features"));
            this.featureListerVeiw = featureListerVeiw;
            featureListerVeiw.getLister().releaseSelection();
            setContent(createContent());
            setSize("700px", "200px");
        }

        protected Widget createContent() {
            getOkButton().setEnabled(false);
            featureListerVeiw.getLister().addItemSelectionHandler(new ItemSelectionHandler<Feature>() {
                @Override
                public void onSelect(Feature selectedItem) {
                    getOkButton().setEnabled(!featureListerVeiw.getLister().getSelectedItems().isEmpty());
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(featureListerVeiw.asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        public boolean onClickOk() {
            selectedFeatures = featureListerVeiw.getLister().getSelectedItems();
            return super.onClickOk();
        }

        protected List<Feature> getSelectedItems() {
            return selectedFeatures;
        }
    }
}