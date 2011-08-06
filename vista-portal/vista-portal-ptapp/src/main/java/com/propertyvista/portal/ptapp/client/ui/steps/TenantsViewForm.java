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
package com.propertyvista.portal.ptapp.client.ui.steps;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderEditorDecorator;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.portal.domain.ptapp.dto.TenantListItemDTO;
import com.propertyvista.portal.domain.ptapp.dto.TenantListDTO;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;

public class TenantsViewForm extends CEntityForm<TenantListDTO> {

    static I18n i18n = I18nFactory.getI18n(TenantsViewForm.class);

    private int maxTenants;

    public TenantsViewForm() {
        super(TenantListDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        main.add(inject(proto().tenants(), createTenantsEditorColumns()));
        return main;
    }

    @Override
    public void addValidations() {
        super.addValueValidator(new EditableValueValidator<TenantListDTO>() {

            @Override
            public boolean isValid(CEditableComponent<TenantListDTO, ?> component, TenantListDTO value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().tenants());
            }

            @Override
            public String getValidationMessage(CEditableComponent<TenantListDTO, ?> component, TenantListDTO value) {
                return i18n.tr("Duplicate tenants specified");
            }
        });

        maxTenants = proto().tenants().getMeta().getLength();
        super.addValueValidator(new EditableValueValidator<TenantListDTO>() {

            @Override
            public boolean isValid(CEditableComponent<TenantListDTO, ?> component, TenantListDTO value) {
                int size = getValue().tenants().size();
                return (size <= maxTenants) && ((value.tenantsMaximum().isNull() || (size <= value.tenantsMaximum().getValue())));
            }

            @Override
            public String getValidationMessage(CEditableComponent<TenantListDTO, ?> component, TenantListDTO value) {
                return i18n.tr("Exceeded number of allowed tenants");
            }
        });
    }

    private CEntityFolderEditor<TenantListItemDTO> createTenantsEditorColumns() {

        return new CEntityFolderEditor<TenantListItemDTO>(TenantListItemDTO.class) {

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().person().name().firstName(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().person().name().middleName(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().person().name().lastName(), "12em"));
                columns.add(new EntityFolderColumnDescriptor(proto().person().birthDate(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().person().email(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().status(), "8.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().takeOwnership(), "5em"));
            }

            @Override
            protected IFolderEditorDecorator<TenantListItemDTO> createFolderDecorator() {
                return new TableFolderEditorDecorator<TenantListItemDTO>(columns, PortalImages.INSTANCE.addRow(), PortalImages.INSTANCE.addRowHover(),
                        i18n.tr("Add a person"));
            }

            @Override
            protected CEntityFolderItemEditor<TenantListItemDTO> createItem() {
                return new TenantsViewFolderRow(columns);
            }
        };
    }
}
