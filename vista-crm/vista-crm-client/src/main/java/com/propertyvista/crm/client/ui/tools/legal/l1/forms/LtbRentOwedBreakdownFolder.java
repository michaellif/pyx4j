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
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;

public class LtbRentOwedBreakdownFolder extends VistaTableFolder<RentOwingForPeriod> {

    public LtbRentOwedBreakdownFolder() {
        super(RentOwingForPeriod.class);
        setOrderable(false);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().from(), "150px"),
                new FolderColumnDescriptor(proto().to(), "150px"),
                new FolderColumnDescriptor(proto().rentCharged(), "150px"),                
                new FolderColumnDescriptor(proto().rentPaid(), "150px"),
                new FolderColumnDescriptor(proto().rentOwing(), "150px")
        );//@formatter:on
    }

    @Override
    protected CForm<? extends RentOwingForPeriod> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<RentOwingForPeriod>(RentOwingForPeriod.class, columns()) {

            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (proto().from() == column.getObject()) {
                    CDatePicker datePicker = new CDatePicker();
                    datePicker.setMandatory(true);
                    return inject(proto().from(), datePicker);
                } else if (proto().to() == column.getObject()) {
                    CDatePicker datePicker = new CDatePicker();
                    datePicker.setMandatory(true);
                    return inject(proto().to(), datePicker);
                } else if (proto().rentCharged() == column.getObject()) {
                    CMoneyField field = new CMoneyField();
                    return inject(proto().rentCharged(), field);
                } else if (proto().rentPaid() == column.getObject()) {
                    CMoneyField field = new CMoneyField();
                    return inject(proto().rentPaid(), field);
                } else if (proto().rentOwing() == column.getObject()) {
                    CMoneyField field = new CMoneyField();
                    field.setViewable(true);
                    return inject(proto().rentOwing(), field);
                }
                return super.createCell(column);
            }

            @Override
            protected IsWidget createContent() {
                IsWidget w = super.createContent();
                get(proto().rentCharged()).addValueChangeHandler(createOwedRentNeedsUpdateHandler());
                get(proto().rentPaid()).addValueChangeHandler(createOwedRentNeedsUpdateHandler());
                get(proto().rentOwing()).addValueChangeHandler(createOwedRentChangedHandler());
                return w;
            }

            private ValueChangeHandler<BigDecimal> createOwedRentNeedsUpdateHandler() {
                return new ValueChangeHandler<BigDecimal>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                        BigDecimal charged = getValue().rentCharged().getValue(new BigDecimal("0.00"));
                        BigDecimal paid = getValue().rentPaid().getValue(new BigDecimal("0.00"));
                        get(proto().rentOwing()).setValue(charged.subtract(paid), true, true);
                    }
                };
            }

            private ValueChangeHandler<BigDecimal> createOwedRentChangedHandler() {
                return new ValueChangeHandler<BigDecimal>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                        onTotalOwedRentChanged();
                    }
                };
            }

        };
    }

    @Override
    public void addValidations() {
        addValueChangeHandler(new ValueChangeHandler<IList<RentOwingForPeriod>>() {
            @Override
            public void onValueChange(ValueChangeEvent<IList<RentOwingForPeriod>> event) {
                setAddable(getValue().size() < 3);
            }
        });
    }

    public void onTotalOwedRentChanged() {

    }

}
