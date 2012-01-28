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
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.rpc.services.SelectGuarantorCrudService;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonGuarantor;

public class PersonGuarantorFolder extends VistaTableFolder<PersonGuarantor> {

    private static final I18n i18n = I18n.get(PersonGuarantorFolder.class);

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().guarantor().person().name(), "25em"),
                new EntityFolderColumnDescriptor(proto().guarantor().person().birthDate(), "9em"),
                new EntityFolderColumnDescriptor(proto().guarantor().person().email(), "15em")
        );//@formatter:on
    }

    public PersonGuarantorFolder(boolean modifyable) {
        super(PersonGuarantor.class, modifyable);
        inheritContainerAccessRules(false);
        setEditable(modifyable);
        setViewable(true);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PersonGuarantor) {
            return new GuarantorEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new TenantSelectorDialog().show();
    }

    private class GuarantorEditor extends CEntityFolderRowEditor<PersonGuarantor> {

        public GuarantorEditor() {
            super(PersonGuarantor.class, columns());
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof Name) {
                if (isEditable()) {
                    return new CEntityLabel<Name>();
                } else {
                    return new CEntityCrudHyperlink<Guarantor>(MainActivityMapper.getCrudAppPlace(Guarantor.class));
                }
            }

            return super.create(member);
        }
    }

    List<Guarantor> alreadySelectedGuarantor() {
        List<Guarantor> guarantors = new Vector<Guarantor>();
        for (PersonGuarantor g : getValue()) {
            if (g.guarantor().id() != null) {
                guarantors.add(g.guarantor());
            }
        }
        return guarantors;
    }

    private class TenantSelectorDialog extends EntitySelectorDialog<Guarantor> {

        public TenantSelectorDialog() {
            super(Guarantor.class, true, alreadySelectedGuarantor(), i18n.tr("Select Guarantor"));
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (Guarantor guarantor : getSelectedItems()) {
                    PersonGuarantor personGuarantor = EntityFactory.create(PersonGuarantor.class);
                    personGuarantor.guarantor().set(guarantor);
                    addItem(personGuarantor);
                }

                return true;
            }
        }

        @Override
        protected String width() {
            return "700px";
        }

        @Override
        protected String height() {
            return "400px";
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().person().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().person().birthDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().person().email()).build()
            );//@formatter:on
        }

        @Override
        protected AbstractListService<Guarantor> getSelectService() {
            return GWT.<AbstractListService<Guarantor>> create(SelectGuarantorCrudService.class);
        }
    }
}
