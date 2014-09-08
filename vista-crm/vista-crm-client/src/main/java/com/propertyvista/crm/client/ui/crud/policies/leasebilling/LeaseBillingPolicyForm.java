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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
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
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;

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

    public LeaseBillingPolicyForm(IForm<LeaseBillingPolicyDTO> view) {
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
        final CMoneyPercentCombo baseFee = new CMoneyPercentCombo();
        formPanel.append(Location.Left, proto().lateFee().baseFee(), baseFee).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().lateFee().maxTotalFeeType()).decorate().componentWidth(120);
        final CMoneyPercentCombo maxTotalFee = new CMoneyPercentCombo();
        formPanel.append(Location.Left, proto().lateFee().maxTotalFee(), maxTotalFee).decorate().componentWidth(120);

        // add fee type control dependencies
        get(proto().lateFee().baseFeeType()).addValueChangeHandler(new ValueChangeHandler<BaseFeeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<BaseFeeType> event) {
                baseFee.setAmountType(BaseFeeType.FlatAmount.equals(event.getValue()) ? ValueType.Monetary : ValueType.Percentage);
            }
        });
        get(proto().lateFee().maxTotalFeeType()).addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                MaxTotalFeeType type = get(proto().lateFee().maxTotalFeeType()).getValue();
                maxTotalFee.setAmountType(MaxTotalFeeType.PercentMonthlyRent.equals(type) ? ValueType.Percentage : ValueType.Monetary);
                maxTotalFee.setVisible(!MaxTotalFeeType.Unlimited.equals(type));
                if (MaxTotalFeeType.Unlimited.equals(type) && maxTotalFee.getValue() != null) {
                    maxTotalFee.getValue().amount().setValue(null);
                    maxTotalFee.getValue().percent().setValue(null);
                }
            }
        });

        return formPanel;
    }

    private IsWidget createNsfFeesPanel() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().nsfFees(), new NsfFeeItemFolder(isEditable()));

        return formPanel;
    }

    class LeaseBillingTypeFolder extends VistaBoxFolder<LeaseBillingTypePolicyItem> {

        private final Set<BillingPeriod> usedFrequencies = new HashSet<BillingPeriod>();

        public LeaseBillingTypeFolder() {
            super(LeaseBillingTypePolicyItem.class);
            if (VistaFeatures.instance().yardiIntegration()) {
                // The only Yardi Billing type (default) will be added in onValueSet()
                setAddable(false);
                setRemovable(false);
            } else {
                addValueChangeHandler(new ValueChangeHandler<IList<LeaseBillingTypePolicyItem>>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<IList<LeaseBillingTypePolicyItem>> event) {
                        updateUsedFrequencies();
                    }
                });
                addComponentValidator(new AbstractComponentValidator<IList<LeaseBillingTypePolicyItem>>() {
                    @Override
                    public AbstractValidationError isValid() {
                        if (getValue() == null) {
                            return null;
                        } else {
                            return getValue().size() > 0 ? null : new BasicValidationError(LeaseBillingTypeFolder.this, i18n.tr("No Billing Types added."));
                        }
                    }
                });
            }
        }

        private void updateUsedFrequencies() {
            usedFrequencies.clear();
            for (LeaseBillingTypePolicyItem item : getValue()) {
                usedFrequencies.add(item.billingPeriod().getValue());
            }
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (VistaFeatures.instance().yardiIntegration()) {
                // Add default Yardi Billing Type
                if (getValue().size() == 0) {
                    LeaseBillingTypePolicyItem item = EntityFactory.create(LeaseBillingTypePolicyItem.class);
                    item.billingPeriod().setValue(BillingPeriod.Monthly);
                    item.billingCycleStartDay().setValue(1);
                    item.paymentDueDayOffset().setValue(0);
                    item.finalDueDayOffset().setValue(15);
                    item.billExecutionDayOffset().setValue(-15);
                    item.autopayExecutionDayOffset().setValue(0);
                    LeaseBillingTypeFolder.super.addItem(item);
                }
            } else {
                updateUsedFrequencies();
            }
        }

        @Override
        protected void addItem() {
            new PaymentFrequencySelectorDialog(usedFrequencies, new ValueChangeHandler<BillingPeriod>() {
                @Override
                public void onValueChange(ValueChangeEvent<BillingPeriod> event) {
                    LeaseBillingTypePolicyItem item = EntityFactory.create(LeaseBillingTypePolicyItem.class);
                    item.billingPeriod().setValue(event.getValue());
                    LeaseBillingTypeFolder.super.addItem(item);
                }
            }).show();
        }

        @Override
        protected CForm<LeaseBillingTypePolicyItem> createItemForm(IObject<?> member) {
            return new LeaseBillingTypeEditor();
        }

        class LeaseBillingTypeEditor extends CForm<LeaseBillingTypePolicyItem> {

            private final CComboBox<Integer> startDay;

            private final CComboBox<Integer> dueDayOffset;

            private final CComboBox<Integer> finalDueDayOffset;

            private final CComboBox<Integer> billDayOffset;

            private final CComboBox<Integer> padExecDayOffset;

            public LeaseBillingTypeEditor() {
                super(LeaseBillingTypePolicyItem.class);

                startDay = new CComboBox<Integer>();
                startDay.setFormat(new IFormatter<Integer, String>() {

                    @Override
                    public String format(Integer value) {
                        return value == null ? i18n.tr("Same as Lease Start Day") : value.toString();
                    }
                });
                dueDayOffset = new CComboBox<Integer>();
                finalDueDayOffset = new CComboBox<Integer>();
                billDayOffset = new CComboBox<Integer>();
                padExecDayOffset = new CComboBox<Integer>();
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.append(Location.Left, proto().billingPeriod(), new CLabel<BillingPeriod>()).decorate().componentWidth(200).labelWidth(250);
                formPanel.append(Location.Left, proto().billingCycleStartDay(), startDay).decorate().componentWidth(200).labelWidth(250);
                formPanel.append(Location.Left, proto().paymentDueDayOffset(), dueDayOffset).decorate().componentWidth(200).labelWidth(250);
                formPanel.append(Location.Left, proto().finalDueDayOffset(), finalDueDayOffset).decorate().componentWidth(200).labelWidth(250);
                formPanel.append(Location.Left, proto().billExecutionDayOffset(), billDayOffset).decorate().componentWidth(200).labelWidth(250);
                formPanel.append(Location.Left, proto().autopayExecutionDayOffset(), padExecDayOffset).decorate().componentWidth(200).labelWidth(250);

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
                int step = max > min ? 1 : -1;
                ArrayList<Integer> options = new ArrayList<Integer>();
                for (int i = min; i != max; i += step) {
                    options.add(i);
                }
                options.add(max);
                return options;
            }
        }

        class PaymentFrequencySelectorDialog extends Dialog implements CancelOption {
            public PaymentFrequencySelectorDialog(final Set<BillingPeriod> usedFrequencies, final ValueChangeHandler<BillingPeriod> selectHandler) {
                super(i18n.tr("Select Payment Frequency"));
                setDialogOptions(this);

                CComboBox<BillingPeriod> selector = new CComboBox<BillingPeriod>();
                selector.setMandatory(true);
                Set<BillingPeriod> options = EnumSet.allOf(BillingPeriod.class);
                options.removeAll(usedFrequencies);
                selector.setOptions(options);

                selector.addValueChangeHandler(new ValueChangeHandler<BillingPeriod>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<BillingPeriod> event) {
                        selectHandler.onValueChange(event);
                        hide(false);
                    }
                });

                if (options.size() == 0) {
                    setBody(new Label(i18n.tr("Sorry, no more items to choose from.")));
                } else {
                    setBody(selector);
                    selector.getNativeComponent().getEditor().setVisibleItemCount(options.size());
                    selector.getNativeComponent().getEditor().setHeight("100px");
                }
            }

            @Override
            public boolean onClickCancel() {
                return true;
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
                new FolderColumnDescriptor(proto().paymentType(), "10em", true),
                new FolderColumnDescriptor(proto().fee(), "6em")
            );//@formatter:on
        }

        @Override
        protected void addItem() {
            EnumSet<PaymentType> values = PaymentType.availableForNsf();
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
