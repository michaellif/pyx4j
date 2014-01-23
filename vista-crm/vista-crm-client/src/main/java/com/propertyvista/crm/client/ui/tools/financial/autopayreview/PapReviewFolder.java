/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.autopayreview;

import java.math.BigDecimal;
import java.math.MathContext;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.tools.common.BulkItemsFolder;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO.ChangeType;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargesTotalDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;

public class PapReviewFolder extends BulkItemsFolder<PapReviewDTO> {

    private static final I18n i18n = I18n.get(PapReviewFolder.class);

    public enum Styles implements IStyleName {

        AutoPayReviewFolder, AutoPayChargesFolder, AutoPayCharge, AutoPayChargeNameColumn, AutoPayChargeNumberColumn, AutoPaySelected

    }

    public PapReviewFolder() {
        super(PapReviewDTO.class);
        setAddable(false);
        setRemovable(false);
        setOrderable(false);
        asWidget().setStyleName(Styles.AutoPayReviewFolder.name());
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PapReviewDTO) {
            return new PapReviewForm();
        }
        return super.create(member);
    }

    private static final class PapReviewForm extends CEntityDecoratableForm<PapReviewDTO> {

        private PapChargeReviewForm chargeTotals;

        private FlowPanel formPanel;

        public PapReviewForm() {
            super(PapReviewDTO.class);
        }

        @Override
        public IsWidget createContent() {
            formPanel = new FlowPanel();

            FlowPanel isSelectedAndCaptionHolderPanel = new FlowPanel();
            isSelectedAndCaptionHolderPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            isSelectedAndCaptionHolderPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            isSelectedAndCaptionHolderPanel.getElement().getStyle().setPaddingBottom(10, Unit.PX);

            final CComponent<Boolean> isSelected = (CComponent<Boolean>) inject(proto().isSelected());
            isSelected.asWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            isSelected.asWidget().getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            isSelected.asWidget().getElement().getStyle().setPaddingRight(15, Unit.PX);
            isSelected.asWidget().getElement().getStyle().setWidth(15, Unit.PX);
            isSelected.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    formPanel.setStyleName(Styles.AutoPaySelected.name(), isSelected.getValue());
                }
            });
            isSelectedAndCaptionHolderPanel.add(isSelected);

            CComponent<?> caption = inject(proto().caption(), new PapReviewCaptionViewer());
            caption.asWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            caption.asWidget().getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            isSelectedAndCaptionHolderPanel.add(caption);

            FlowPanel folderHolderPanel = new FlowPanel();
            isSelectedAndCaptionHolderPanel.add(inject(proto().charges(), new PapChargesFolder()));
            formPanel.add(isSelectedAndCaptionHolderPanel);

            chargeTotals = new PapChargeReviewForm();
            chargeTotals.initContent();
            chargeTotals.setViewable(true);
            folderHolderPanel.add(chargeTotals);
            formPanel.add(folderHolderPanel);

            addValueChangeHandler(new ValueChangeHandler<PapReviewDTO>() {
                @Override
                public void onValueChange(ValueChangeEvent<PapReviewDTO> event) {
                    recalculateChargesTotal();
                }
            });
            return formPanel;
        }

        public void setSelected(boolean isSelected) {
            get(proto().isSelected()).setValue(isSelected);
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            recalculateChargesTotal();
            chargeTotals.setVisible(getValue().charges().size() > 1);
            chargeTotals.asWidget().setVisible(getValue().charges().size() > 1);
            formPanel.setStyleName(Styles.AutoPaySelected.name(), (get(proto().isSelected()).getValue() != null && get(proto().isSelected()).getValue()));
        }

        private void recalculateChargesTotal() {
            if (getValue() != null) {
                PapChargesTotalDTO totals = summarizeCharges(getValue().charges());
                chargeTotals.populate(totals);
            }
        }

    }

    private static void initIfNull(IPrimitive<BigDecimal> member) {
        if (member.isNull()) {
            member.setValue(new BigDecimal("0.00"));
        }
    }

    private static PapChargesTotalDTO summarizeCharges(Iterable<PapChargeReviewDTO> papCharges) {
        PapChargesTotalDTO papChargesTotal = EntityFactory.create(PapChargesTotalDTO.class);
        papChargesTotal.setPrimaryKey(new Key(-1));
        papChargesTotal.chargeName().setValue(i18n.tr("Total"));

        initIfNull(papChargesTotal.suspendedPrice());
        initIfNull(papChargesTotal.suspendedPapAmount());
        initIfNull(papChargesTotal.newPrice());
        initIfNull(papChargesTotal.newPapAmount());

        for (PapChargeReviewDTO totalOfCharge : papCharges) {
            if (totalOfCharge.changeType().getValue() != ChangeType.New) {
                papChargesTotal.suspendedPrice().setValue(totalOfCharge.suspendedPrice().getValue().add(papChargesTotal.suspendedPrice().getValue()));
                papChargesTotal.suspendedPapAmount().setValue(
                        totalOfCharge.suspendedPapAmount().getValue().add(papChargesTotal.suspendedPapAmount().getValue()));
            }
            if (totalOfCharge.changeType().getValue() != ChangeType.Removed) {
                papChargesTotal.newPrice().setValue(totalOfCharge.newPrice().getValue().add(papChargesTotal.newPrice().getValue()));

                if (totalOfCharge.newPapAmount().getValue() != null) {
                    papChargesTotal.newPapAmount().setValue(totalOfCharge.newPapAmount().getValue().add(papChargesTotal.newPapAmount().getValue()));
                }
            }
        }
        if (papChargesTotal.suspendedPrice().getValue().compareTo(BigDecimal.ZERO) != 0) {
            papChargesTotal.suspendedPapPercent().setValue(
                    papChargesTotal.suspendedPapAmount().getValue().divide(papChargesTotal.suspendedPrice().getValue(), MathContext.DECIMAL32));
        } else {
            papChargesTotal.suspendedPapPercent().setValue(new BigDecimal("0.00"));
        }
        if (papChargesTotal.newPrice().getValue().compareTo(BigDecimal.ZERO) != 0) {
            papChargesTotal.newPapPercent().setValue(
                    papChargesTotal.newPapAmount().getValue().divide(papChargesTotal.newPrice().getValue(), MathContext.DECIMAL32));
        } else {
            papChargesTotal.newPapPercent().setValue(new BigDecimal("0.00"));
        }

        papChargesTotal.changePercent().setValue(
                papChargesTotal.suspendedPapAmount().getValue().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : papChargesTotal.newPapAmount().getValue()
                        .subtract(papChargesTotal.suspendedPapAmount().getValue())
                        .divide(papChargesTotal.suspendedPapAmount().getValue(), 2, BigDecimal.ROUND_HALF_UP));

        return papChargesTotal;
    }

}
