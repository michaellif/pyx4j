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
import java.text.ParseException;
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
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IFormat;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CPercentageField;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.dto.LeaseBillingPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.MaxTotalFeeType;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.policy.policies.domain.NsfFeeItem;
import com.propertyvista.shared.config.VistaFeatures;

public class LeaseBillingPolicyForm extends PolicyDTOTabPanelBasedForm<LeaseBillingPolicyDTO> {

    private final static I18n i18n = I18n.get(LeaseBillingPolicyForm.class);

    private SimplePanel baseFeeHolder;

    private SimplePanel maxFeeHolder;

    public LeaseBillingPolicyForm(IForm<LeaseBillingPolicyDTO> view) {
        super(LeaseBillingPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        baseFeeHolder = new SimplePanel();
        maxFeeHolder = new SimplePanel();
        if (VistaFeatures.instance().yardiIntegration()) {
            return Arrays.asList(//@formatter:off
                    createBillingPanel()
            );//@formatter:on
        } else {
            return Arrays.asList(//@formatter:off
                    createBillingPanel(),
                    createLateFeesPanel(),
                    createNsfFeesPanel()
            );//@formatter:on
        }
    }

    private TwoColumnFlexFormPanel createBillingPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Billing"));

        int row = -1;

        panel.setWidget(++row, 0, 2, inject(proto().prorationMethod(), new FormDecoratorBuilder(10, true).build()));
        panel.setWidget(++row, 0, 2, inject(proto().confirmationMethod(), new FormDecoratorBuilder(10, true).build()));

        panel.setH3(++row, 0, 2, proto().availableBillingTypes().getMeta().getCaption());
        panel.setWidget(++row, 0, 2, inject(proto().availableBillingTypes(), new LeaseBillingTypeFolder()));

        if (VistaFeatures.instance().yardiIntegration()) {
            get(proto().prorationMethod()).setVisible(false);
            get(proto().confirmationMethod()).setVisible(false);
        }

        return panel;
    }

    private TwoColumnFlexFormPanel createLateFeesPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Late Fee"));

        int row = -1;
        panel.setWidget(++row, 0, inject(proto().lateFee().baseFeeType(), new FormDecoratorBuilder(10).build()));
        get(proto().lateFee().baseFeeType()).addValueChangeHandler(new ValueChangeHandler<LateFeeItem.BaseFeeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<BaseFeeType> event) {
                bindBaseFeeEditor(event.getValue(), false);
            }
        });
        panel.setWidget(++row, 0, baseFeeHolder);

        row = -1;
        panel.setWidget(++row, 1, inject(proto().lateFee().maxTotalFeeType(), new FormDecoratorBuilder(10).build()));
        get(proto().lateFee().maxTotalFeeType()).addValueChangeHandler(new ValueChangeHandler<LateFeeItem.MaxTotalFeeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<MaxTotalFeeType> event) {
                bindMaxFeeEditor(event.getValue(), false);
            }
        });
        panel.setWidget(++row, 1, maxFeeHolder);

        return panel;
    }

    private TwoColumnFlexFormPanel createNsfFeesPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("NSF Fee"));

        panel.setWidget(0, 0, 2, inject(proto().nsfFees(), new NsfFeeItemFolder(isEditable())));

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        bindBaseFeeEditor(getValue().lateFee().baseFeeType().getValue(), true);
        bindMaxFeeEditor(getValue().lateFee().maxTotalFeeType().getValue(), true);
    }

    private void bindBaseFeeEditor(BaseFeeType baseFeeType, boolean repopulatevalue) {
        if (baseFeeType == null) {
            return; // New item
        }

        CComponent<?> comp = null;
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
            baseFeeHolder.setWidget(inject(proto().lateFee().baseFee(), comp, new FormDecoratorBuilder(6).build()));

            if (repopulatevalue) {
                get(proto().lateFee().baseFee()).populate(getValue().lateFee().baseFee().getValue(BigDecimal.ZERO));
            }
        }
    }

    private void bindMaxFeeEditor(MaxTotalFeeType maxFeeType, boolean repopulatevalue) {
        if (maxFeeType == null) {
            return; // New item
        }

        CComponent<?> comp = null;
        switch (maxFeeType) {
        case Unlimited:
        case FlatAmount:
            comp = new CMoneyField();
            break;

        case PercentMonthlyRent:
            comp = new CPercentageField();
            break;
        }

        unbind(proto().lateFee().maxTotalFee());

        if (comp != null) {
            maxFeeHolder.setWidget(inject(proto().lateFee().maxTotalFee(), comp, new FormDecoratorBuilder(6).build()));

            if (repopulatevalue) {
                get(proto().lateFee().maxTotalFee()).populate(getValue().lateFee().maxTotalFee().getValue(BigDecimal.ZERO));
            }

            comp.setVisible(maxFeeType != MaxTotalFeeType.Unlimited);
        }

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
                        return getValue().size() > 0 ? null : new FieldValidationError(LeaseBillingTypeFolder.this, i18n.tr("No Billing Types added."));
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
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof LeaseBillingTypePolicyItem) {
                return new LeaseBillingTypeEditor();
            } else {
                return super.create(member);
            }
        }

        class LeaseBillingTypeEditor extends CEntityForm<LeaseBillingTypePolicyItem> {

            private final CComboBox<Integer> startDay;

            private final CComboBox<Integer> dueDayOffset;

            private final CComboBox<Integer> finalDueDayOffset;

            private final CComboBox<Integer> billDayOffset;

            private final CComboBox<Integer> padExecDayOffset;

            public LeaseBillingTypeEditor() {
                super(LeaseBillingTypePolicyItem.class);

                startDay = new CComboBox<Integer>();
                startDay.setFormat(new IFormat<Integer>() {

                    @Override
                    public Integer parse(String string) throws ParseException {
                        return null;
                    }

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
                TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
                int row = -1;
                content.setWidget(++row, 0, inject(proto().billingPeriod(), new CLabel<BillingPeriod>(), new FormDecoratorBuilder(15).labelWidth(20).build()));

                content.setWidget(++row, 0, inject(proto().billingCycleStartDay(), startDay, new FormDecoratorBuilder(15).labelWidth(20).build()));
                content.setWidget(++row, 0, inject(proto().paymentDueDayOffset(), dueDayOffset, new FormDecoratorBuilder(15).labelWidth(20).build()));
                content.setWidget(++row, 0, inject(proto().finalDueDayOffset(), finalDueDayOffset, new FormDecoratorBuilder(15).labelWidth(20).build()));
                content.setWidget(++row, 0, inject(proto().billExecutionDayOffset(), billDayOffset, new FormDecoratorBuilder(15).labelWidth(20).build()));
                content.setWidget(++row, 0, inject(proto().autopayExecutionDayOffset(), padExecDayOffset, new FormDecoratorBuilder(15).labelWidth(20).build()));

                if (!VistaFeatures.instance().yardiIntegration()) {
                    get(proto().finalDueDayOffset()).setVisible(false);
                }
                return content;
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
                    selector.getWidget().getEditor().setVisibleItemCount(options.size());
                    selector.getWidget().getEditor().setHeight("100px");
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
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().paymentType(), "10em", true),
                new EntityFolderColumnDescriptor(proto().fee(), "6em")
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
