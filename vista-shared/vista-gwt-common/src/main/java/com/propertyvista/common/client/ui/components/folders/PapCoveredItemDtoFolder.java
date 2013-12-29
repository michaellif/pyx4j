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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;

public class PapCoveredItemDtoFolder extends VistaTableFolder<PreauthorizedPaymentCoveredItemDTO> {

    static final I18n i18n = I18n.get(PapCoveredItemDtoFolder.class);

    public PapCoveredItemDtoFolder() {
        super(PreauthorizedPaymentCoveredItemDTO.class, false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().billableItem(),"25em", i18n.tr("Lease Charge")),
                new EntityFolderColumnDescriptor(proto().billableItem().agreedPrice(),"6em", i18n.tr("Price"), true),
                new EntityFolderColumnDescriptor(proto().covered(), "6em", true),
                new EntityFolderColumnDescriptor(proto().amount(), "6em", i18n.tr("Payment")),
                new EntityFolderColumnDescriptor(proto().percent(), "4em", true));
          //@formatter:on                
    }

    public void onAmontValueChange() {
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PreauthorizedPaymentCoveredItemDTO) {
            return new CoveredItemEditor();
        }
        return super.create(member);
    }

    class CoveredItemEditor extends CEntityFolderRowEditor<PreauthorizedPaymentCoveredItemDTO> {

        public CoveredItemEditor() {
            super(PreauthorizedPaymentCoveredItemDTO.class, columns());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?> comp;

            if (column.getObject() == proto().billableItem()) {
                comp = inject(column.getObject(), new PapBillableItemLabel());
            } else {
                comp = super.createCell(column);
            }

            // handle value changes: 
            if (column.getObject() == proto().amount()) {
                ((CComponent<BigDecimal>) comp).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                        BigDecimal percent = BigDecimal.ONE;
                        if (getValue().billableItem().agreedPrice().getValue().compareTo(BigDecimal.ZERO) != 0) {
                            percent = BigDecimal.ZERO;
                            if (event.getValue() != null) {
                                percent = event.getValue().divide(getValue().billableItem().agreedPrice().getValue(), 2, RoundingMode.FLOOR);
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