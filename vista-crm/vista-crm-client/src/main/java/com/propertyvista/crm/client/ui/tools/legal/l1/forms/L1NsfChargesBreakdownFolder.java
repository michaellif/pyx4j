/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.legal.l1.NsfChargeDetails;

public class L1NsfChargesBreakdownFolder extends VistaTableFolder<NsfChargeDetails> {

    public L1NsfChargesBreakdownFolder() {
        super(NsfChargeDetails.class);
        setOrderable(false);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().chequeAmount(), "150px"),
                new FolderColumnDescriptor(proto().dateOfCheque(), "150px"),
                new FolderColumnDescriptor(proto().dateOfNsfCharge(), "150px"),
                new FolderColumnDescriptor(proto().bankCharge(), "150px"),
                new FolderColumnDescriptor(proto().landlordsAdministrationCharge(), "150px"),
                new FolderColumnDescriptor(proto().totalCharge(), "150px")                
        );//@formatter:on
    }

    @Override
    protected CForm<? extends NsfChargeDetails> createItemForm(IObject<?> member) {
        CFolderRowEditor<NsfChargeDetails> rowEditor = new CFolderRowEditor<NsfChargeDetails>(NsfChargeDetails.class, columns(),
                new VistaViewersComponentFactory()) {
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (proto().chequeAmount() == column.getObject()) {
                    CMoneyField moneyField = new CMoneyField();
                    moneyField.setMandatory(true);
                    return inject(column.getObject(), moneyField);
                } else if (proto().dateOfCheque() == column.getObject()) {
                    CDatePicker datePicker = new CDatePicker();
                    datePicker.setMandatory(true);
                    return inject(column.getObject(), datePicker);
                } else if (proto().dateOfNsfCharge() == column.getObject()) {
                    CDatePicker datePicker = new CDatePicker();
                    datePicker.setMandatory(true);
                    return inject(column.getObject(), datePicker);
                } else if (proto().bankCharge() == column.getObject()) {
                    return inject(column.getObject(), new CMoneyField());
                } else if (proto().landlordsAdministrationCharge() == column.getObject()) {
                    return inject(column.getObject(), new CMoneyField());
                } else if (proto().totalCharge() == column.getObject()) {
                    CMoneyField field = new CMoneyField();
                    field.setViewable(true);
                    return inject(column.getObject(), field);
                } else {
                    return super.create(column.getObject());
                }
            }

            @Override
            protected IsWidget createContent() {
                IsWidget w = super.createContent();
                get(proto().bankCharge()).addValueChangeHandler(createTotalChargeUpdateRequiredHandler());
                get(proto().landlordsAdministrationCharge()).addValueChangeHandler(createTotalChargeUpdateRequiredHandler());
                get(proto().totalCharge()).addValueChangeHandler(createTotalCharedUpdatedHandler());
                return w;
            }

            private ValueChangeHandler<BigDecimal> createTotalChargeUpdateRequiredHandler() {
                return new ValueChangeHandler<BigDecimal>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                        BigDecimal bankCharge = getValue().bankCharge().getValue(new BigDecimal("0.00"));
                        BigDecimal landlordsAdministrationCharge = getValue().landlordsAdministrationCharge().getValue(new BigDecimal("0.00"));
                        get(proto().totalCharge()).setValue(bankCharge.add(landlordsAdministrationCharge), true, true);
                    }
                };
            }

            private ValueChangeHandler<BigDecimal> createTotalCharedUpdatedHandler() {
                return new ValueChangeHandler<BigDecimal>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                        onTotalChargeChanged();
                    }
                };
            }

        };
        return rowEditor;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        onTotalChargeChanged();
    }

    public void onTotalChargeChanged() {

    }
}
