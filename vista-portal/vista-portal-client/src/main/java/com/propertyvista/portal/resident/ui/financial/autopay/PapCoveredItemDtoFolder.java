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

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CPercentageLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class PapCoveredItemDtoFolder extends PortalBoxFolder<PreauthorizedPaymentCoveredItemDTO> {

    static final I18n i18n = I18n.get(PapCoveredItemDtoFolder.class);

    public PapCoveredItemDtoFolder() {
        super(PreauthorizedPaymentCoveredItemDTO.class, false);
        setExpended(true);
    }

    public void onAmontValueChange() {
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
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
        protected IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel(i18n.tr("Details"));
            int row = -1;

            content.setWidget(++row, 0,
                    inject(proto().billableItem(), new PapBillableItemLabel(), new FieldDecoratorBuilder(200).customLabel(i18n.tr("Lease Charge")).build()));
            content.setWidget(++row, 0,
                    inject(proto().billableItem().agreedPrice(), new CMoneyLabel(), new FieldDecoratorBuilder(100).customLabel(i18n.tr("Price")).build()));
            content.setWidget(++row, 0, inject(proto().covered(), new CMoneyLabel(), new FieldDecoratorBuilder(100).build()));
            content.setWidget(++row, 0, inject(proto().amount(), new FieldDecoratorBuilder(100).customLabel(i18n.tr("Payment")).build()));
            content.setWidget(++row, 0, inject(proto().percent(), new CPercentageLabel(), new FieldDecoratorBuilder(100).build()));

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