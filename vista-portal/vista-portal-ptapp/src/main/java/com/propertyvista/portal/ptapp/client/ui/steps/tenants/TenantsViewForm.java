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
package com.propertyvista.portal.ptapp.client.ui.steps.tenants;


import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInApplicationListDTO;

public class TenantsViewForm extends CEntityEditor<TenantInApplicationListDTO> {

    static I18n i18n = I18n.get(TenantsViewForm.class);

    private int maxTenants;

    public TenantsViewForm() {
        super(TenantInApplicationListDTO.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        main.add(inject(proto().tenants(), new TenantFolder(isEditable())));
        return main;
    }

    @Override
    public void addValidations() {
        super.addValueValidator(new EditableValueValidator<TenantInApplicationListDTO>() {

            @Override
            public boolean isValid(CEditableComponent<TenantInApplicationListDTO, ?> component, TenantInApplicationListDTO value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().tenants());
            }

            @Override
            public String getValidationMessage(CEditableComponent<TenantInApplicationListDTO, ?> component, TenantInApplicationListDTO value) {
                return i18n.tr("Duplicate tenants specified");
            }
        });

        maxTenants = proto().tenants().getMeta().getLength();
        super.addValueValidator(new EditableValueValidator<TenantInApplicationListDTO>() {

            @Override
            public boolean isValid(CEditableComponent<TenantInApplicationListDTO, ?> component, TenantInApplicationListDTO value) {
                int size = getValue().tenants().size();
                return (size <= maxTenants) && ((value.tenantsMaximum().isNull() || (size <= value.tenantsMaximum().getValue())));
            }

            @Override
            public String getValidationMessage(CEditableComponent<TenantInApplicationListDTO, ?> component, TenantInApplicationListDTO value) {
                return i18n.tr("Exceeded number of allowed tenants");
            }
        });
    }
}
