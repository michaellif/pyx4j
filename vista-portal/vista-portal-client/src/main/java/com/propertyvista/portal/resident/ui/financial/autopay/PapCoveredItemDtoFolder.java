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
package com.propertyvista.portal.resident.ui.financial.autopay;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CPercentageLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.resident.ui.util.decorators.FormWidgetDecoratorBuilder;

public class PapCoveredItemDtoFolder extends VistaBoxFolder<PreauthorizedPaymentCoveredItemDTO> {

    static final I18n i18n = I18n.get(PapCoveredItemDtoFolder.class);

    public PapCoveredItemDtoFolder() {
        super(PreauthorizedPaymentCoveredItemDTO.class, false);
    }

    public void onAmontValueChange() {
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PreauthorizedPaymentCoveredItemDTO) {
            return new CoveredItemDtoEditor();
        }
        return super.create(member);
    }

    class CoveredItemDtoEditor extends CEntityForm<PreauthorizedPaymentCoveredItemDTO> {

        public CoveredItemDtoEditor() {
            super(PreauthorizedPaymentCoveredItemDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel(i18n.tr("Details"));
            int row = -1;

            content.setWidget(++row, 0,
                    new FormWidgetDecoratorBuilder(inject(proto().billableItem(), new PapBillableItemLabel()), 200).customLabel(i18n.tr("Lease Charge")).build());
            content.setWidget(++row, 0,
                    new FormWidgetDecoratorBuilder(inject(proto().billableItem().agreedPrice(), new CMoneyLabel()), 100).customLabel(i18n.tr("Price")).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().covered(), new CMoneyLabel()), 100).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().amount()), 100).customLabel(i18n.tr("Payment")).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().percent(), new CPercentageLabel()), 100).build());

            // tweaks:
            get(proto().amount()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
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

            return content;
        }
    }
}