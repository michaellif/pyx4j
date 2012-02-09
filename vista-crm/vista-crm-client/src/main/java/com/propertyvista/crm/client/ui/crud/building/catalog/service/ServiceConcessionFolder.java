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
import com.propertyvista.crm.client.ui.crud.building.catalog.concession.ConcessionEditorForm;
import com.propertyvista.crm.rpc.services.building.catalog.ConcessionCrudService;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Service;

class ServiceConcessionFolder extends VistaBoxFolder<Concession> {

    private static final I18n i18n = I18n.get(ServiceConcessionFolder.class);

    private final CEntityEditor<Service> parent;

    public ServiceConcessionFolder(boolean modifyable, CEntityEditor<Service> parent) {
        super(Concession.class, modifyable);
        this.parent = parent;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Concession) {
            return new ConcessionEditorForm(true);
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
        new ConcessionSelectorDialog().show();
    }

    private class ConcessionSelectorDialog extends EntitySelectorDialog<Concession> {

        public ConcessionSelectorDialog() {
            super(Concession.class, true, getValue(), i18n.tr("Select Concession"));
            addFilter(new DataTableFilterData(ServiceConcessionFolder.this.proto().catalog().getPath(), Operators.is, parent.getValue().catalog()));
            setWidth("700px");
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (Concession item : getSelectedItems()) {
                    addItem(item);
                }
                return true;
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().type()).build(),
                    new MemberColumnDescriptor.Builder(proto().term()).build(),
                    new MemberColumnDescriptor.Builder(proto().value()).build(),
                    new MemberColumnDescriptor.Builder(proto().condition()).build(),
                    new MemberColumnDescriptor.Builder(proto().status()).build(),
                    new MemberColumnDescriptor.Builder(proto().effectiveDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().expirationDate()).build()
            );//@formatter:on
        }

        @Override
        protected AbstractListService<Concession> getSelectService() {
            return GWT.<AbstractListService<Concession>> create(ConcessionCrudService.class);
        }
    }
}