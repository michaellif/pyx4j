/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

@Singleton
public class TenantsViewForm extends CEntityForm<PotentialTenantList> {

    static I18n i18n = I18nFactory.getI18n(TenantsViewForm.class);

    private int maxTenants;

    public TenantsViewForm() {
        super(PotentialTenantList.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        main.add(inject(proto().tenants(), createTenantsEditorColumns()));
        return main;
    }

    @Override
    public void addValidations() {
        super.addValueValidator(new EditableValueValidator<PotentialTenantList>() {

            @Override
            public boolean isValid(CEditableComponent<PotentialTenantList, ?> component, PotentialTenantList value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().tenants());
            }

            @Override
            public String getValidationMessage(CEditableComponent<PotentialTenantList, ?> component, PotentialTenantList value) {
                return i18n.tr("Duplicate tenants specified");
            }
        });

        maxTenants = proto().tenants().getMeta().getLength();
        super.addValueValidator(new EditableValueValidator<PotentialTenantList>() {

            @Override
            public boolean isValid(CEditableComponent<PotentialTenantList, ?> component, PotentialTenantList value) {
                int size = getValue().tenants().size();
                return (size <= maxTenants) && ((value.tenantsMaximum().isNull() || (size <= value.tenantsMaximum().getValue())));
            }

            @Override
            public String getValidationMessage(CEditableComponent<PotentialTenantList, ?> component, PotentialTenantList value) {
                return i18n.tr("Exceeded number of allowed tenants");
            }
        });
    }

    private CEntityFolder<PotentialTenantInfo> createTenantsEditorColumns() {

        return new CEntityFolder<PotentialTenantInfo>(PotentialTenantInfo.class) {

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().firstName(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().middleName(), "6em"));
                columns.add(new EntityFolderColumnDescriptor(proto().lastName(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().birthDate(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().email(), "11em"));
                columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().status(), "8.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().takeOwnership(), "7em"));
            }

            @Override
            protected FolderDecorator<PotentialTenantInfo> createFolderDecorator() {
                return new TableFolderDecorator<PotentialTenantInfo>(columns, SiteImages.INSTANCE.addRow(), SiteImages.INSTANCE.addRowHover(),
                        i18n.tr("Add a person"));
            }

            @Override
            protected CEntityFolderItem<PotentialTenantInfo> createItem() {
                return new TenantsViewFolderRow(columns);
            }
        };
    }
}
