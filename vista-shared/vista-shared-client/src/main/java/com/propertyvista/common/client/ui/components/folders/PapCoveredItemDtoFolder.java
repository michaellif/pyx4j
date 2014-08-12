/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-24
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.folders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;

public class PapCoveredItemDtoFolder extends VistaTableFolder<PreauthorizedPaymentCoveredItemDTO> {

    static final I18n i18n = I18n.get(PapCoveredItemDtoFolder.class);

    public PapCoveredItemDtoFolder() {
        super(PreauthorizedPaymentCoveredItemDTO.class, false);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto().billableItem(),"25em", i18n.tr("Lease Charge")),
                new FolderColumnDescriptor(proto().billableItem().agreedPrice(),"6em", i18n.tr("Price"), true),
                new FolderColumnDescriptor(proto().covered(), "6em", true),
                new FolderColumnDescriptor(proto().amount(), "6em", i18n.tr("Payment")),
                new FolderColumnDescriptor(proto().percent(), "4em", true));
          //@formatter:on                
    }

    public void onAmontValueChange() {
    }

    @Override
    protected CForm<PreauthorizedPaymentCoveredItemDTO> createItemForm(IObject<?> member) {
        return new CoveredItemEditor();
    }

    class CoveredItemEditor extends CFolderRowEditor<PreauthorizedPaymentCoveredItemDTO> {

        public CoveredItemEditor() {
            super(PreauthorizedPaymentCoveredItemDTO.class, columns());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected CField<?, ?> createCell(FolderColumnDescriptor column) {
            CField<?, ?> comp;

            if (column.getObject() == proto().billableItem()) {
                comp = inject(column.getObject(), new PapBillableItemLabel());
            } else {
                comp = super.createCell(column);
            }

            // handle value changes: 
            if (column.getObject() == proto().amount()) {
                ((CField<BigDecimal, ?>) comp).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                        BigDecimal percent = BigDecimal.ONE;
                        if (getValue().billableItem().agreedPrice().getValue(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) != 0) {
                            percent = BigDecimal.ZERO;
                            if (event.getValue() != null) {
                                percent = event.getValue().divide(getValue().billableItem().agreedPrice().getValue(BigDecimal.ZERO), 2, RoundingMode.FLOOR);
                            }
                        }
                        get(proto().percent()).setValue(percent);
                        onAmontValueChange();
                    }
                });
            } else if (column.getObject() == proto().percent()) {
                comp.setEditable(false);
            }

            return comp;
        }
    }
}