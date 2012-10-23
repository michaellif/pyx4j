/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leasebilling;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.dto.LeaseBillingPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.MaxTotalFeeType;
import com.propertyvista.domain.policy.policies.domain.NsfFeeItem;

public class LeaseBillingPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseBillingPolicyDTO> {

    private final static I18n i18n = I18n.get(LeaseBillingPolicyForm.class);

    private SimplePanel baseFeeHolder;

    private SimplePanel maxFeeHolder;

    public LeaseBillingPolicyForm() {
        this(false);
    }

    public LeaseBillingPolicyForm(boolean viewMode) {
        super(LeaseBillingPolicyDTO.class, viewMode);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        return Arrays.asList(//@formatter:off
                createBillingPanel(),
                createLateFeesPanel(),
               createNsfFeesPanel()
        );//@formatter:on
    }

    private FormFlexPanel createBillingPanel() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Billing"));

        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().prorationMethod()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().confirmationMethod()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().useDefaultBillingCycleSartDay())).build());
        get(proto().useDefaultBillingCycleSartDay()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().defaultBillingCycleSartDay()).setEnabled(event.getValue());

            }
        });

        ArrayList<Integer> options = new ArrayList<Integer>();
        for (int i = 1; i < 29; i++) {
            options.add(i);
        }
        CComboBox<Integer> comboBox = new CComboBox<Integer>();
        comboBox.setOptions(options);
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().defaultBillingCycleSartDay(), comboBox), 5).build());

        return panel;
    }

    private FormFlexPanel createLateFeesPanel() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Late Fee"));

        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lateFee().baseFeeType()), 10).build());
        get(proto().lateFee().baseFeeType()).addValueChangeHandler(new ValueChangeHandler<LateFeeItem.BaseFeeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<BaseFeeType> event) {
                bindBaseFeeEditor(event.getValue(), false);
            }
        });
        panel.setWidget(++row, 0, baseFeeHolder = new SimplePanel());

        row = -1;
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().lateFee().maxTotalFeeType()), 10).build());
        get(proto().lateFee().maxTotalFeeType()).addValueChangeHandler(new ValueChangeHandler<LateFeeItem.MaxTotalFeeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<MaxTotalFeeType> event) {
                bindMaxFeeEditor(event.getValue(), false);
            }
        });
        panel.setWidget(++row, 1, maxFeeHolder = new SimplePanel());

        panel.getColumnFormatter().setWidth(0, "50%");
        panel.getColumnFormatter().setWidth(1, "50%");

        return panel;
    }

    private FormFlexPanel createNsfFeesPanel() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("NSF Fee"));

        panel.setWidget(0, 0, inject(proto().nsfFees(), new NsfFeeItemFolder(isEditable())));

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().defaultBillingCycleSartDay()).setEnabled(getValue().useDefaultBillingCycleSartDay().getValue());
        bindBaseFeeEditor(getValue().lateFee().baseFeeType().getValue(), true);
        bindMaxFeeEditor(getValue().lateFee().maxTotalFeeType().getValue(), true);
    }

    private void bindBaseFeeEditor(BaseFeeType baseFeeType, boolean repopulatevalue) {
        if (baseFeeType == null) {
            return; // New item
        }

        CComponent<?, ?> comp = null;
        switch (baseFeeType) {
        case FlatAmount:
            comp = new CMoneyField();
            break;

        case PercentMonthlyRent:
        case PercentOwedTotal:
            comp = new CPercentageField();
            break;
        }

        unbind(proto().lateFee().baseFee());

        if (comp != null) {
            baseFeeHolder.setWidget(new DecoratorBuilder(inject(proto().lateFee().baseFee(), comp), 6).build());

            if (repopulatevalue) {
                get(proto().lateFee().baseFee()).populate(getValue().lateFee().baseFee().getValue(BigDecimal.ZERO));
            }
        }
    }

    private void bindMaxFeeEditor(MaxTotalFeeType maxFeeType, boolean repopulatevalue) {
        if (maxFeeType == null) {
            return; // New item
        }

        CComponent<?, ?> comp = null;
        switch (maxFeeType) {
        case Unlimited:
        case FlatAmount:
            comp = new CMoneyField();
            break;

        case PercentMonthlyRent:
            comp = new CPercentageField();
            break;
        }

        unbind(proto().lateFee().baseFee());

        if (comp != null) {
            maxFeeHolder.setWidget(new DecoratorBuilder(inject(proto().lateFee().baseFee(), comp), 6).build());

            if (repopulatevalue) {
                get(proto().lateFee().baseFee()).populate(getValue().lateFee().baseFee().getValue(BigDecimal.ZERO));
            }

            comp.setVisible(maxFeeType != MaxTotalFeeType.Unlimited);
        }

    }

    private class NsfFeeItemFolder extends VistaTableFolder<NsfFeeItem> {

        NsfFeeItemFolder(boolean modifyable) {
            super(NsfFeeItem.class, modifyable);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().paymentType(), "10em", true),
                new EntityFolderColumnDescriptor(proto().fee(), "6em")
            );//@formatter:on
        }

        @Override
        protected void addItem() {
            EnumSet<PaymentType> values = PaymentType.avalableForNsf();
            for (NsfFeeItem item : getValue()) {
                if (values.contains(item.paymentType().getValue())) {
                    values.remove(item.paymentType().getValue());
                }
            }
            new SelectEnumDialog<PaymentType>(i18n.tr("Select Payment Type"), values) {
                @Override
                public boolean onClickOk() {
                    NsfFeeItem item = EntityFactory.create(NsfFeeItem.class);
                    item.paymentType().setValue(getSelectedType());
                    addItem(item);
                    return true;
                }
            }.show();
        }
    }
}
