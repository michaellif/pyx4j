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
package com.propertyvista.crm.client.ui.crud.billing.payment;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.AbstractEntitySelectorDialog;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorListDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.RadioGroup;

import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.PaymentRecordDTO.PaymentSelect;

public class PaymentEditorForm extends CrmEntityForm<PaymentRecordDTO> {

    private static final I18n i18n = I18n.get(PaymentEditorForm.class);

    private final PaymentMethodEditor paymentMethodEditor = new PaymentMethodEditor();

    public PaymentEditorForm() {
        this(false);
    }

    public PaymentEditorForm(boolean viewMode) {
        super(PaymentRecordDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, createDetailsPanel());
        main.setHR(1, 0, 1);
        main.setWidget(2, 0, inject(proto().paymentMethod(), paymentMethodEditor));

        return new CrmScrollPanel(main);
    }

    private IsWidget createDetailsPanel() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().propertyCode()), 15).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().unitNumber()), 15).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseId()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseStatus()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().leaseParticipant(), new CEntitySelectorHyperlink<LeaseParticipant>() {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(getValue().getInstanceValueClass(), getValue().getPrimaryKey());
            }

            @Override
            protected AbstractEntitySelectorDialog<LeaseParticipant> getSelectorDialog() {
                return new EntitySelectorListDialog<LeaseParticipant>(i18n.tr("Select Tenant/Guarantor To Pay"), false, PaymentEditorForm.this.getValue()
                        .participants()) {

                    @Override
                    public boolean onClickOk() {
                        get(PaymentEditorForm.this.proto().leaseParticipant()).setValue(getSelectedItems().get(0));
                        return true;
                    }

                    @Override
                    public String defineWidth() {
                        return "350px";
                    }

                    @Override
                    public String defineHeight() {
                        return "100px";
                    }
                };
            }
        }), 25).build());

        panel.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().paymentSelect(), new CRadioGroupEnum<PaymentSelect>(PaymentSelect.class, RadioGroup.Layout.HORISONTAL)), 10)
                        .build());

        row = -1;
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().amount()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().receivedDate()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().targetDate()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().depositDate()), 10).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().paymentStatus()), 10).build());
//        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().transactionErrorMessage()), 20).build());
//        main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().transactionAuthorizationNumber()), 20).build());
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().notes()), 25).build());

        // tweak UI:
        get(proto().id()).setViewable(true);
        get(proto().propertyCode()).setViewable(true);
        get(proto().unitNumber()).setViewable(true);
        get(proto().leaseId()).setViewable(true);
        get(proto().leaseStatus()).setViewable(true);

        get(proto().paymentStatus()).setViewable(true);
//        get(proto().transactionErrorMessage()).setViewable(true);
//        get(proto().transactionAuthorizationNumber()).setViewable(true);

        get(proto().paymentSelect()).addValueChangeHandler(new ValueChangeHandler<PaymentSelect>() {
            @Override
            public void onValueChange(ValueChangeEvent<PaymentSelect> event) {
                paymentMethodEditor.setTypeSelectionVisible(event.getValue() == PaymentSelect.New);
            }
        });

        panel.getColumnFormatter().setWidth(0, "50%");
        panel.getColumnFormatter().setWidth(1, "50%");
        return panel;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().id()).setVisible(!getValue().id().isNull());
//        get(proto().transactionAuthorizationNumber()).setVisible(!getValue().transactionAuthorizationNumber().isNull());
//        get(proto().transactionErrorMessage()).setVisible(!getValue().transactionErrorMessage().isNull());
    }

}