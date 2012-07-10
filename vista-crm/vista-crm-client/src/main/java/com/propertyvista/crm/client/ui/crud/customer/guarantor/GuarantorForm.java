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

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.common.CLeaseVHyperlink;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
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

        Tab tab = addTab(createDetailsTab(i18n.tr("Details")));
        selectTab(tab);

        tab = addTab(isEditable() ? new HTML() : ((GuarantorViewerView) getParentView()).getScreeningListerView().asWidget(), i18n.tr("Screening"));
        setTabEnabled(tab, !isEditable());

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.guarantor, get(proto().participantId()), getValue().getPrimaryKey());
        }
    }

    @Override
    protected void onSetValue(boolean populate) {
        super.onSetValue(populate);
        if (isValueEmpty()) {
            return;
        }

        get(proto().customer().person().email()).setMandatory(!getValue().customer().user().isNull());
    }

    private FormFlexPanel createDetailsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);
        int row = -1;

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().participantId()), 7).build());
        main.setWidget(++row, 0, inject(proto().customer().person().name(), new NameEditor(i18n.tr("Guarantor"))));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().sex()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().birthDate()), 9).build());

        main.setBR(++row, 0, 1);

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().workPhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customer().person().email()), 25).build());

        if (!isEditable()) {
            main.setBR(++row, 0, 1);

            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseV(), new CLeaseVHyperlink()), 35).customLabel(i18n.tr("Lease")).build());
        }

        return main;
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().customer().person().birthDate()));
    }
}