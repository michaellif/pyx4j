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
 */
package com.propertyvista.crm.client.ui.crud.policies.leasebilling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyPercentCombo;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.dto.LeaseBillingPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.MaxTotalFeeType;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.policy.policies.domain.NsfFeeItem;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseBillingPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseBillingPolicyDTO> {

    private final static I18n i18n = I18n.get(LeaseBillingPolicyForm.class);

    private final CMoneyPercentCombo baseFee = new CMoneyPercentCombo();

    private final CMoneyPercentCombo maxTotalFee = new CMoneyPercentCombo();

    public LeaseBillingPolicyForm(IPrimeFormView<LeaseBillingPolicyDTO, ?> view) {
        super(LeaseBillingPolicyDTO.class, view);

        addTab(createBillingPanel(), i18n.tr("Billing"));
        if (!VistaFeatures.instance().yardiIntegration()) {
            addTab(createLateFeesPanel(), i18n.tr("Late Fee"));
            addTab(createNsfFeesPanel(), i18n.tr("NSF Fee"));
        }
    }

    private IsWidget createBillingPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().prorationMethod()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().confirmationMethod()).decorate().componentWidth(120);

        formPanel.h3(proto().availableBillingTypes().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().availableBillingTypes(), new LeaseBillingTypeFolder());

        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().prorationMethod()).setVisible(false);
            get(proto().confirmationMethod()).setVisible(false);
        }

        return formPanel;
    }

    private IsWidget createLateFeesPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().lateFee().baseFeeType()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().lateFee().baseFee(), baseFee).decorate().componentWidth(100);

        formPanel.append(Location.Left, proto().lateFee().maxTotalFeeType()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().lateFee().maxTotalFee(), maxTotalFee).decorate().componentWidth(100);

        // add fee type control dependencies
        get(proto().lateFee().baseFeeType()).addValueChangeHandler(new ValueChangeHandler<BaseFeeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<BaseFeeType> event) {
                syncBaseFeeType(event.getValue());
            }
        });
        get(proto().lateFee().maxTotalFeeType()).addValueChangeHandler(new ValueChangeHandler<MaxTotalFeeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<MaxTotalFeeType> event) {
                syncMaxTotalFeeType(event.getValue());
            }
        });

        return formPanel;
    }

    private void syncBaseFeeType(BaseFeeType type) {
        baseFee.setAmountType(BaseFeeType.FlatAmount.equals(type) ? ValueType.Monetary : ValueType.Percentage);
    }

    private void syncMaxTotalFeeType(MaxTotalFeeType type) {
        maxTotalFee.setAmountType(MaxTotalFeeType.PercentMonthlyRent.equals(type) ? ValueType.Percentage : ValueType.Monetary);
        maxTotalFee.setVisible(!MaxTotalFeeType.Unlimited.equals(type));

        if (MaxTotalFeeType.Unlimited.equals(type) && maxTotalFee.getValue() != null) {
            maxTotalFee.getValue().amount().setValue(null);
            maxTotalFee.getValue().percent().setValue(null);
        }
    }

    private IsWidget createNsfFeesPanel() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().nsfFees(), new NsfFeeItemFolder(isEditable()));

        return formPanel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        // sync value types
        if (getValue() != null) {
            syncBaseFeeType(getValue().lateFee().baseFeeType().getValue());
            syncMaxTotalFeeType(getValue().lateFee().maxTotalFeeType().getValue());
        }
    }

    class LeaseBillingTypeFolder extends VistaBoxFolder<LeaseBillingTypePolicyItem> {

        public LeaseBillingTypeFolder() {
            super(LeaseBillingTypePolicyItem.class, !VistaFeatures.instance().yardiIntegration());

            addComponentValidator(new AbstractComponentValidator<IList<LeaseBillingTypePolicyItem>>() {
                @Override
                public AbstractValidationError isValid() {
                    if (getValue() != null) {
                        return getValue().size() > 0 ? null : new BasicValidationError(LeaseBillingTypeFolder.this, i18n.tr("No Billing Types added."));
                    }
                    return null;
                }
            });
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (VistaFeatures.instance().yardiIntegration()) {
                // Add default Yardi Billing Type
                if (isEditable() && getValue().size() == 0) {
                    LeaseBillingTypePolicyItem item = EntityFactory.create(LeaseBillingTypePolicyItem.class);

                    item.billingPeriod().setValue(BillingPeriod.Monthly);
                    item.billingCycleStartDay().setValue(1);
                    item.paymentDueDayOffset().setValue(0);
                    item.finalDueDayOffset().setValue(15);
                    item.billExecutionDayOffset().setValue(-15);
                    item.autopayExecutionDayOffset().setValue(0);

                    LeaseBillingTypeFolder.super.addItem(item);
                }
            }
        }

        @Override
        protected void addItem() {
            EnumSet<BillingPeriod> options = EnumSet.allOf(BillingPeriod.class);
            for (LeaseBillingTypePolicyItem item : getValue()) {
                options.remove(item.billingPeriod().getValue());
            }
            new SelectEnumDialog<BillingPeriod>(i18n.tr("Select Payment Frequency"), options) {
                @Override
                public boolean onClickOk() {
                    LeaseBillingTypePolicyItem item = EntityFactory.create(LeaseBillingTypePolicyItem.class);
                    item.billingPeriod().setValue(getSelectedType());
                    addItem(item);
                    return true;
                }
            }.show();
        }

        @Override
        protected CForm<LeaseBillingTypePolicyItem> createItemForm(IObject<?> member) {
            return new LeaseBillingTypeEditor();
        }

        class LeaseBillingTypeEditor extends CForm<LeaseBillingTypePolicyItem> {

            private final CComboBox<Integer> startDay = new CComboBox<Integer>();

            private final CComboBox<Integer> dueDayOffset = new CComboBox<Integer>();

            private final CComboBox<Integer> finalDueDayOffset = new CComboBox<Integer>();

            private final CComboBox<Integer> billDayOffset = new CComboBox<Integer>();

            private final CComboBox<Integer> padExecDayOffset = new CComboBox<Integer>();

            public LeaseBillingTypeEditor() {
                super(LeaseBillingTypePolicyItem.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().billingPeriod(), new CLabel<BillingPeriod>()).decorate();
                formPanel.append(Location.Left, proto().billingCycleStartDay(), startDay).decorate().componentWidth(170);

                formPanel.append(Location.Right, proto().paymentDueDayOffset(), dueDayOffset).decorate().labelWidth(200).componentWidth(60);
                formPanel.append(Location.Right, proto().finalDueDayOffset(), finalDueDayOffset).decorate().labelWidth(200).componentWidth(60);
                formPanel.append(Location.Right, proto().billExecutionDayOffset(), billDayOffset).decorate().labelWidth(200).componentWidth(60);
                formPanel.append(Location.Right, proto().autopayExecutionDayOffset(), padExecDayOffset).decorate().labelWidth(200).componentWidth(60);

                // tweak:
                startDay.populate(0); // populate first time before set format!
                startDay.setFormat(new IFormatter<Integer, String>() {
                    @Override
                    public String format(Integer value) {
                        return value == null ? i18n.tr("Same as Lease Start Day") : value.toString();
                    }
                });

                if (!VistaFeatures.instance().yardiIntegration()) {
                    get(proto().finalDueDayOffset()).setVisible(false);
                }

                return formPanel;
            }

            @Override
            protected void onValueSet(boolean populate) {
                if (getValue() != null) {
                    int cycles = getValue().billingPeriod().getValue().getNumOfCycles();
                    startDay.setOptions(makeList(1, cycles));

                    int maxOffset = cycles - 1;
                    dueDayOffset.setOptions(makeList(0, maxOffset / 2));
                    finalDueDayOffset.setOptions(makeList(0, maxOffset));
                    billDayOffset.setOptions(makeList(-maxOffset / 2, -1));
                    padExecDayOffset.setOptions(makeList(-maxOffset / 2, maxOffset / 2));
                }
            }

            private List<Integer> makeList(int min, int max) {
                ArrayList<Integer> options = new ArrayList<Integer>();

                int step = max > min ? 1 : -1;
                for (int i = min; i != max; i += step) {
                    options.add(i);
                }
                options.add(max);

                return options;
            }
        }
    }

    private class NsfFeeItemFolder extends VistaTableFolder<NsfFeeItem> {

        NsfFeeItemFolder(boolean modifyable) {
            super(NsfFeeItem.class, modifyable);
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().paymentType(), "200px", true),
                new FolderColumnDescriptor(proto().fee(), "100px")
            );//@formatter:on
        }

        @Override
        protected void addItem() {
            EnumSet<PaymentType> options = PaymentType.availableForNsf();
            for (NsfFeeItem item : getValue()) {
                options.remove(item.paymentType().getValue());
            }
            new SelectEnumDialog<PaymentType>(i18n.tr("Select Payment Type"), options) {
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
