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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.CPercentageLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.PapBillableItemLabel;
import com.propertyvista.dto.PreauthorizedPaymentCoveredItemDTO;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class PapCoveredItemDtoFolder extends PortalBoxFolder<PreauthorizedPaymentCoveredItemDTO> {

    static final I18n i18n = I18n.get(PapCoveredItemDtoFolder.class);

    public PapCoveredItemDtoFolder() {
        this(false);
    }

    public PapCoveredItemDtoFolder(boolean modifiable) {
        super(PreauthorizedPaymentCoveredItemDTO.class, modifiable);
        setOrderable(false);
        setExpended(true);
        setNoDataLabel(i18n.tr("There are no covered items"));
    }

    public void onAmontValueChange() {
    }

    @Override
    public BoxFolderItemDecorator<PreauthorizedPaymentCoveredItemDTO> createItemDecorator() {
        BoxFolderItemDecorator<PreauthorizedPaymentCoveredItemDTO> decorator = super.createItemDecorator();
        decorator.setCaptionFormatter(new IFormatter<PreauthorizedPaymentCoveredItemDTO, SafeHtml>() {
            @Override
            public SafeHtml format(PreauthorizedPaymentCoveredItemDTO value) {
                String itemDescription = value.billableItem().description().isNull() ? value.billableItem().item().name().getValue() : value.billableItem()
                        .description().getValue();
                return SafeHtmlUtils.fromString(SimpleMessageFormat.format("{0} ({1}/{2})", itemDescription, value.amount(), value.billableItem().agreedPrice()));
            }
        });
        return decorator;
    }

    @Override
    protected CForm<PreauthorizedPaymentCoveredItemDTO> createItemForm(IObject<?> member) {
        return new CoveredItemDtoEditor();
    }

    class CoveredItemDtoEditor extends CForm<PreauthorizedPaymentCoveredItemDTO> {

        public CoveredItemDtoEditor() {
            super(PreauthorizedPaymentCoveredItemDTO.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().billableItem(), new PapBillableItemLabel()).decorate().customLabel(i18n.tr("Lease Charge"));
            formPanel.append(Location.Left, proto().billableItem().agreedPrice(), new CMoneyLabel()).decorate().customLabel(i18n.tr("Price"));
            formPanel.append(Location.Left, proto().covered(), new CMoneyLabel()).decorate();
            formPanel.append(Location.Left, proto().amount()).decorate().componentWidth(100).customLabel(i18n.tr("Payment"));
            formPanel.append(Location.Left, proto().percent(), new CPercentageLabel()).decorate();

            // tweaks:
            get(proto().amount()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
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

            return formPanel;
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().amount()).addComponentValidator(new AbstractComponentValidator<BigDecimal>() {
                @Override
                public BasicValidationError isValid() {
                    if (getCComponent().getValue() != null) {
                        return (getCComponent().getValue().signum() < 0 ? new BasicValidationError(getCComponent(), i18n
                                .tr("Payment amount should be grater than or equal to zero)")) : null);
                    }
                    return null;
                }
            });
        }
    }
}