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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.customer.common.LeaseParticipantEditorPresenter;
import com.propertyvista.crm.client.ui.crud.customer.common.PaymentMethodFolder;
import com.propertyvista.crm.client.ui.crud.lease.common.CLeaseTermVHyperlink;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorForm extends CrmEntityForm<GuarantorDTO> {

    private static final I18n i18n = I18n.get(GuarantorForm.class);

    public GuarantorForm(IFormView<GuarantorDTO> view) {
        super(GuarantorDTO.class, view);

        selectTab(addTab(createDetailsTab(i18n.tr("Details"))));
        addTab(createPaymentMethodsTab(i18n.tr("Payment Methods")));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().customer().person().email()).setMandatory(!getValue().customer().user().isNull());

        if (isEditable()) {
            ClientPolicyManager.setIdComponentEditabilityByPolicy(IdTarget.guarantor, get(proto().participantId()), getValue().getPrimaryKey());
        } else {
            get(proto().customer().personScreening()).setVisible(getValue().customer().personScreening().getPrimaryKey() != null);
        }
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

            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseTermV(), new CLeaseTermVHyperlink()), 35).customLabel(i18n.tr("Lease Term"))
                    .build());
            main.setWidget(
                    ++row,
                    0,
                    new DecoratorBuilder(inject(proto().customer().personScreening(),
                            new CEntityCrudHyperlink<CustomerScreening>(AppPlaceEntityMapper.resolvePlace(CustomerScreening.class))), 15).build());
        }

        return main;
    }

    private FormFlexPanel createPaymentMethodsTab(String title) {
        FormFlexPanel main = new FormFlexPanel(title);

        main.setWidget(0, 0, inject(proto().paymentMethods(), new PaymentMethodFolder(isEditable(), false) {
            @Override
            protected void onBillingAddressSameAsCurrentOne(boolean set, final CComponent<AddressStructured, ?> comp) {
                if (set) {
                    ((LeaseParticipantEditorPresenter) ((GuarantorEditorView) getParentView()).getPresenter())
                            .getCurrentAddress(new DefaultAsyncCallback<AddressStructured>() {
                                @Override
                                public void onSuccess(AddressStructured result) {
                                    comp.setValue(result, false);
                                }
                            });
                } else {
                    comp.setValue(EntityFactory.create(AddressStructured.class), false);
                }
            }

            @Override
            protected void addItem() {
                if (GuarantorForm.this.getValue().electronicPaymentsAllowed().isBooleanTrue()) {
                    super.addItem();
                } else {
                    MessageDialog.warn(i18n.tr("Warning"), i18n.tr("Merchant account is not setup to receive Electronic Payments"));
                }
            }
        }));

        return main;
    }

    @Override
    public void addValidations() {
        new PastDateValidation(get(proto().customer().person().birthDate()));
    }
}