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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInApplicationListDTO;

public class TenantsViewForm extends CEntityForm<TenantInApplicationListDTO> {

    static I18n i18n = I18n.get(TenantsViewForm.class);

    public TenantsViewForm() {
        super(TenantInApplicationListDTO.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        boolean modifiable = SecurityController.checkBehavior(VistaCustomerBehavior.ProspectiveApplicant);

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Tenants & Occupants"));
        main.setWidget(++row, 0, inject(proto().tenants(), new TenantFolder(modifiable)));
        get(proto().tenants()).setViewable(!modifiable);

        return main;
    }

    @Override
    public void addValidations() {
        super.addValueValidator(new EditableValueValidator<TenantInApplicationListDTO>() {

            @Override
            public ValidationError isValid(CComponent<TenantInApplicationListDTO> component, TenantInApplicationListDTO value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().tenants()) ? null : new ValidationError(component, i18n.tr("Duplicate Tenants Specified"));
            }

        });

        super.addValueValidator(new EditableValueValidator<TenantInApplicationListDTO>() {
            @Override
            public ValidationError isValid(CComponent<TenantInApplicationListDTO> component, TenantInApplicationListDTO value) {
                int size = getValue().tenants().size();
                return (size <= value.tenantsMaximum().getValue()) && ((value.tenantsMaximum().isNull() || (size <= value.tenantsMaximum().getValue()))) ? null
                        : new ValidationError(component, i18n.tr("Your Selection Exceeded The Number Of Allowed Tenants"));
            }
        });
    }
}
