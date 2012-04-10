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
package com.propertyvista.crm.client.ui.crud.tenant.screening;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.rpc.services.selections.SelectGuarantorListService;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonGuarantor;

public class PersonGuarantorFolder extends VistaTableFolder<PersonGuarantor> {

    private static final I18n i18n = I18n.get(PersonGuarantorFolder.class);

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().guarantor(), "25em"),
                new EntityFolderColumnDescriptor(proto().relationship(), "10em"),
                new EntityFolderColumnDescriptor(proto().guarantor().customer().person().birthDate(), "9em"),
                new EntityFolderColumnDescriptor(proto().guarantor().customer().person().email(), "15em")
        );//@formatter:on
    }

    public PersonGuarantorFolder(boolean modifyable) {
        super(PersonGuarantor.class, modifyable);
        setViewable(true);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PersonGuarantor) {
            return new PersonGuarantorEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new TenantSelectorDialog().show();
    }

    private class PersonGuarantorEditor extends CEntityFolderRowEditor<PersonGuarantor> {

        public PersonGuarantorEditor() {
            super(PersonGuarantor.class, columns());
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof Guarantor) {
                if (isEditable()) {
                    return new CEntityLabel<Guarantor>();
                } else {
                    return new CEntityCrudHyperlink<Guarantor>(AppPlaceEntityMapper.resolvePlace(Guarantor.class));
                }
            }
            return super.create(member);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?, ?> comp = super.createCell(column);
            if (isEditable() && proto().relationship() == column.getObject()) {
                comp.inheritEditable(false);
            }
            return comp;
        }
    }

    List<Guarantor> getAlreadySelected() {
        List<Guarantor> selected = new Vector<Guarantor>();
        for (PersonGuarantor item : getValue()) {
            if (!item.guarantor().isNull()) {
                selected.add(item.guarantor());
            }
        }
        return selected;
    }

    private class TenantSelectorDialog extends EntitySelectorTableDialog<Guarantor> {

        public TenantSelectorDialog() {
            super(Guarantor.class, true, getAlreadySelected(), i18n.tr("Select Guarantor"));
            setWidth("700px");
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (Guarantor selected : getSelectedItems()) {
                    PersonGuarantor item = EntityFactory.create(PersonGuarantor.class);
                    item.guarantor().set(selected);
                    addItem(item);
                }
                return true;
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().customer().person().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().customer().person().birthDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().customer().person().email()).build()
            );//@formatter:on
        }

        @Override
        protected AbstractListService<Guarantor> getSelectService() {
            return GWT.<AbstractListService<Guarantor>> create(SelectGuarantorListService.class);
        }
    }
}
