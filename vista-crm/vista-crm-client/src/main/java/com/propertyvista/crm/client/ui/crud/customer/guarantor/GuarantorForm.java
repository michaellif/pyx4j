/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.guarantor;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.common.CLeaseTermVHyperlink;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorForm extends CrmEntityForm<GuarantorDTO> {

    private static final I18n i18n = I18n.get(GuarantorForm.class);

    public GuarantorForm() {
        this(false);
    }

    public GuarantorForm(boolean viewMode) {
        super(GuarantorDTO.class, viewMode);
    }

    @Override
    public void createTabs() {

        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.guarantor, get(proto().leaseCustomer().participantId()), getValue().getPrimaryKey());
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().leaseCustomer().customer().person().email()).setMandatory(!getValue().leaseCustomer().customer().user().isNull());

        if (!isEditable()) {
            get(proto().leaseCustomer().customer().personScreening()).setVisible(!getValue().leaseCustomer().customer().personScreening().isNull());
        }
    }

    private FormFlexPanel createDetailsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().participantId()), 7).build());
        main.setWidget(++row, 0, inject(proto().leaseCustomer().customer().person().name(), new NameEditor(i18n.tr("Guarantor"))));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().sex()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().birthDate()), 9).build());

        main.setBR(++row, 0, 1);

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().workPhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseCustomer().customer().person().email()), 25).build());

        if (!isEditable()) {
            main.setBR(++row, 0, 1);

            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseTermV(), new CLeaseTermVHyperlink()), 35).customLabel(i18n.tr("Lease Term"))
                    .build());
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().leaseCustomer().customer().personScreening(), new CEntityCrudHyperlink<PersonScreening>(
                            AppPlaceEntityMapper.resolvePlace(PersonScreening.class))), 15).build());
        }

        return main;
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().leaseCustomer().customer().person().birthDate()));
    }
}