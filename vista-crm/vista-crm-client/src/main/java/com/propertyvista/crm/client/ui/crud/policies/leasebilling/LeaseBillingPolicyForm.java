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
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.annotations.I18nContext;
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

    class MoneyFormat implements IFormat<BigDecimal> {

        private final NumberFormat nf;

        @I18nContext(javaFormatFlag = true)
        MoneyFormat() {
            nf = NumberFormat.getFormat(i18n.tr("#,##0.00"));
        }

        @Override
        public String format(BigDecimal value) {
            if (value == null) {
                return "";
            } else {
                return "$" + nf.format(value);
            }
        }

        @Override
        public BigDecimal parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            try {
                string = string.trim();
                if (string.startsWith("$")) {
                    string = string.substring(1);
                }
                if (string.endsWith("%")) {
                    throw new NumberFormatException();
                }
                return new BigDecimal(nf.parse(string));
            } catch (NumberFormatException e) {
                throw new ParseException(i18n.tr("Invalid money format. Enter valid number"), 0);
            }
        }

    }

    class PercentFormat implements IFormat<BigDecimal> {

        private final NumberFormat nf = NumberFormat.getFormat(i18n.tr("#,##0.00"));

        @Override
        public String format(BigDecimal value) {
            if (value == null) {
                return "";
            } else {
                return nf.format(value) + "%";
            }

        }

        @Override
        public BigDecimal parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null;
            } else {
                try {
                    string = string.trim();
                    if (string.endsWith("%")) {
                        string = string.substring(0, string.length() - 1);
                    }
                    return new BigDecimal(string);
                } catch (NumberFormatException e) {
                    throw new ParseException(i18n.tr("Invalid percent format"), 0);
                }

            }
        }

    }

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
                CComponent<BigDecimal, ?> comp = get(proto().lateFee().baseFee());
                if (comp instanceof CTextFieldBase) {
                    CTextFieldBase<BigDecimal, ?> castedComp = ((CTextFieldBase<BigDecimal, ?>) comp);
                    if (event.getValue() == BaseFeeType.FlatAmount) {
                        castedComp.setWatermark(i18n.tr("$0.00"));
                        castedComp.setFormat(new MoneyFormat());
                    } else {
                        castedComp.setWatermark(i18n.tr("0.00%"));
                        castedComp.setFormat(new PercentFormat());
                    }
                }
                comp.revalidate();
            }
        });
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lateFee().baseFee()), 6).build());

        row = -1;
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().lateFee().maxTotalFeeType()), 10).build());
        get(proto().lateFee().maxTotalFeeType()).addValueChangeHandler(new ValueChangeHandler<LateFeeItem.MaxTotalFeeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<MaxTotalFeeType> event) {
                CComponent<BigDecimal, ?> comp = get(proto().lateFee().maxTotalFee());
                comp.setVisible(event.getValue() != MaxTotalFeeType.Unlimited);
                if (comp instanceof CTextFieldBase) {
                    CTextFieldBase<BigDecimal, ?> castedComp = ((CTextFieldBase<BigDecimal, ?>) comp);
                    if (event.getValue() == MaxTotalFeeType.FlatAmount) {
                        castedComp.setWatermark(i18n.tr("$0.00"));
                        castedComp.setFormat(new MoneyFormat());
                    } else {
                        castedComp.setWatermark(i18n.tr("0.00%"));
                        castedComp.setFormat(new PercentFormat());
                    }
                }
                comp.revalidate();
            }
        });
        panel.setWidget(++row, 1, new DecoratorBuilder(inject(proto().lateFee().maxTotalFee()), 6).build());

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
