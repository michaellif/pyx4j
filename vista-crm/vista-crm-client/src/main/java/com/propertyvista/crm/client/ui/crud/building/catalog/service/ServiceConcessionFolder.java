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
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.rpc.services.selections.SelectConcessionListService;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Service;

class ServiceConcessionFolder extends VistaTableFolder<Concession> {

    private static final I18n i18n = I18n.get(ServiceConcessionFolder.class);

    private final CEntityForm<Service> parent;

    public ServiceConcessionFolder(boolean modifyable, CEntityForm<Service> parent) {
        super(Concession.class, modifyable);
        this.parent = parent;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().version().type(), "15em"),
                new EntityFolderColumnDescriptor(proto().version().value(), "7em"),
                new EntityFolderColumnDescriptor(proto().version().term(), "10em"),
                new EntityFolderColumnDescriptor(proto().version().condition(), "10em"),
                new EntityFolderColumnDescriptor(proto().version().mixable(), "5em")
                );//@formatter:on   
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof Concession) {
            return new ConcessionEditor();
        }
        return super.create(member);

    }

    private class ConcessionEditor extends CEntityFolderRowEditor<Concession> {

        public ConcessionEditor() {
            super(Concession.class, columns());
            setEditable(false);
            setViewable(true);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public CComponent<?> create(IObject<?> member) {
            CComponent<?> comp = null;
            if (member.equals(proto().version().type())) {
                comp = new CEnumLabel();
                if (!ServiceConcessionFolder.this.isEditable()) {
                    ((CField) comp).setNavigationCommand(new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Concession.class).formViewerPlace(getValue().getPrimaryKey()));
                        }
                    });
                }
            } else {
                comp = super.create(member);
            }
            return comp;
        }
    }

    @Override
    protected void addItem() {
        new ConcessionSelectorDialog().show();
    }

    private class ConcessionSelectorDialog extends EntitySelectorTableDialog<Concession> {

        public ConcessionSelectorDialog() {
            super(Concession.class, true, getValue(), i18n.tr("Select Concession"));
            setParentFiltering(parent.getValue().catalog().getPrimaryKey());
            setDialogPixelWidth(700);
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (Concession selected : getSelectedItems()) {
                    addItem(selected);
                }
                return true;
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().version().type()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().term()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().value()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().condition()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().effectiveDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().expirationDate()).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().version().type(), false), new Sort(proto().version().effectiveDate(), false));
        }

        @Override
        protected AbstractListService<Concession> getSelectService() {
            return GWT.<AbstractListService<Concession>> create(SelectConcessionListService.class);
        }
    }
}