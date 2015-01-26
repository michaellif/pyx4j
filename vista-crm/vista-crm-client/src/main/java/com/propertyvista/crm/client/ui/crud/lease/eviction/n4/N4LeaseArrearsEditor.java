/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2015
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.lease.eviction.n4;

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.domain.legal.n4.N4LeaseArrears;
import com.propertyvista.domain.legal.n4.N4UnpaidCharge;

public class N4LeaseArrearsEditor extends CForm<N4LeaseArrears> {

    public N4LeaseArrearsEditor() {
        super(N4LeaseArrears.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().totalRentOwning(), new CMoneyLabel()).decorate();
        formPanel.append(Location.Dual, proto().unpaidCharges(), new BatchItemChargesFolder());

        return formPanel;
    }

    class BatchItemChargesFolder extends VistaBoxFolder<N4UnpaidCharge> {

        public BatchItemChargesFolder() {
            super(N4UnpaidCharge.class);

            setOrderable(false);
        }

        @Override
        protected void addItem(N4UnpaidCharge newEntity) {
            newEntity.rentCharged().setValue(new BigDecimal("0.00"));
            newEntity.rentPaid().setValue(new BigDecimal("0.00"));
            newEntity.rentOwing().setValue(new BigDecimal("0.00"));
            super.addItem(newEntity);
        }

        @Override
        public VistaBoxFolderItemDecorator<N4UnpaidCharge> createItemDecorator() {
            VistaBoxFolderItemDecorator<N4UnpaidCharge> itemDecorator = super.createItemDecorator();
            itemDecorator.setExpended(false);
            return itemDecorator;
        }

        @Override
        protected CForm<? extends N4UnpaidCharge> createItemForm(IObject<?> member) {
            return new CForm<N4UnpaidCharge>(N4UnpaidCharge.class) {

                private CForm<N4UnpaidCharge> getForm() {
                    return this;
                }

                private final ValueChangeHandler<BigDecimal> amountChangeHandler = new ValueChangeHandler<BigDecimal>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                        // update charge total
                        N4UnpaidCharge item = getForm().getValue();
                        item.rentOwing().setValue(item.rentCharged().getValue().subtract(item.rentPaid().getValue()));
                        getForm().refresh(false);

                        // update parent with grand total
                        N4LeaseArrears rec = N4LeaseArrearsEditor.this.getValue();
                        BigDecimal total = BigDecimal.ZERO;
                        for (N4UnpaidCharge owing : rec.unpaidCharges()) {
                            total = total.add(owing.rentOwing().getValue());
                        }
                        rec.totalRentOwning().setValue(total);
                        N4LeaseArrearsEditor.this.refresh(false);
                    }
                };

                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);
                    formPanel.append(Location.Left, proto().fromDate()).decorate();
                    formPanel.append(Location.Left, proto().toDate()).decorate();
                    formPanel.append(Location.Left, proto().arCode()).decorate();
                    formPanel.append(Location.Right, proto().rentCharged()).decorate();
                    formPanel.append(Location.Right, proto().rentPaid()).decorate();
                    formPanel.append(Location.Right, proto().rentOwing()).decorate();

                    if (isEditable()) {
                        get(proto().rentOwing()).setEditable(false);
                        get(proto().rentCharged()).addValueChangeHandler(amountChangeHandler);
                        get(proto().rentPaid()).addValueChangeHandler(amountChangeHandler);
                    }

                    return formPanel;
                }
            };
        }
    }
}
