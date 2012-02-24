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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.building.catalog.feature.FeatureEditorForm;
import com.propertyvista.crm.rpc.services.building.catalog.FeatureCrudService;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;

class ServiceFeatureFolder extends VistaBoxFolder<Feature> {

    private static final I18n i18n = I18n.get(ServiceFeatureFolder.class);

    private final CEntityEditor<Service> parent;

    public ServiceFeatureFolder(boolean modifyable, CEntityEditor<Service> parent) {
        super(Feature.class, modifyable);
        this.parent = parent;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Feature) {
            return new FeatureEditorForm(true);
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
        new FeatureSelectorDialog().show();
    }

    private class FeatureSelectorDialog extends EntitySelectorDialog<Feature> {

        public FeatureSelectorDialog() {
            super(Feature.class, true, getValue(), i18n.tr("Select Feature"));
            addFilter(new DataTableFilterData(ServiceFeatureFolder.this.proto().catalog().getPath(), Operators.is, parent.getValue().catalog()));
            setWidth("700px");
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (Feature selected : getSelectedItems()) {
                    addItem(selected);
                }
                return true;
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().type()).build(),
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().isMandatory()).build(),
                    new MemberColumnDescriptor.Builder(proto().isRecurring()).build()
            );//@formatter:on
        }

        @Override
        protected AbstractListService<Feature> getSelectService() {
            return GWT.<AbstractListService<Feature>> create(FeatureCrudService.class);
        }

    }
}