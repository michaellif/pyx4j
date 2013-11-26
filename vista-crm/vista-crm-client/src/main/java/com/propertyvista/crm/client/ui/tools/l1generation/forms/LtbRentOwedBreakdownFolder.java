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
package com.propertyvista.crm.client.ui.tools.l1generation.forms;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDatePicker;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.legal.ltbcommon.RentOwingForPeriod;

public class LtbRentOwedBreakdownFolder extends VistaTableFolder<RentOwingForPeriod> {

    public LtbRentOwedBreakdownFolder() {
        super(RentOwingForPeriod.class);
        setOrderable(false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().from(), "150px"),
                new EntityFolderColumnDescriptor(proto().to(), "150px"),
                new EntityFolderColumnDescriptor(proto().rentCharged(), "150px"),
                new EntityFolderColumnDescriptor(proto().rentOwing(), "150px"),
                new EntityFolderColumnDescriptor(proto().rentPaid(), "150px")                
        );//@formatter:on
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (proto().from() == member) {
            CDatePicker datePicker = new CDatePicker();
            datePicker.setMandatory(true);
            return datePicker;
        } else if (proto().to() == member) {
            CDatePicker datePicker = new CDatePicker();
            datePicker.setMandatory(true);
            return datePicker;
        } else if (proto().rentCharged() == member) {
            CMoneyField field = new CMoneyField();
            return field;
        } else if (proto().rentPaid() == member) {
            CMoneyField field = new CMoneyField();
            return field;
        } else if (proto().rentOwing() == member) {
            CMoneyField field = new CMoneyField();
            return field;
        }
        return super.create(member);
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

}
