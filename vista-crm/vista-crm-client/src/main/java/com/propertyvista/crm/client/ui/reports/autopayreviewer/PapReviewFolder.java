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
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO.ChangeType;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargesTotalDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;

public class PapReviewFolder extends VistaBoxFolder<PapReviewDTO> {

    private static final I18n i18n = I18n.get(PapReviewFolder.class);

    public enum Styles implements IStyleName {

        AutoPayReviewFolder, AutoPayChargesFolder, AutoPayCharge, AutoPayChargeNameColumn, AutoPayChargeNumberColumn

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

    @Override
    public IFolderItemDecorator<PapReviewDTO> createItemDecorator() {
        VistaBoxFolderItemDecorator<PapReviewDTO> itemDecorator = (VistaBoxFolderItemDecorator<PapReviewDTO>) PapReviewFolder.super.createItemDecorator();
        itemDecorator.setCollapsible(false);
        return itemDecorator;
    }

    public void selectAll() {
        for (CComponent<?> c : getComponents()) {
            if (c instanceof CEntityFolderItem) {
                PapReviewForm papForm = (PapReviewForm) ((CEntityFolderItem<?>) c).getComponents().iterator().next();
                papForm.setSelected(true);
            }
        }
    }

    private static final class PapReviewForm extends CEntityDecoratableForm<PapReviewDTO> {

        private PapChargeReviewForm chargeTotals;

        public PapReviewForm() {
            super(PapReviewDTO.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel contentPanel = new FlowPanel();

            FlowPanel isSelectedAndCaptionHolderPanel = new FlowPanel();
            isSelectedAndCaptionHolderPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            isSelectedAndCaptionHolderPanel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            isSelectedAndCaptionHolderPanel.getElement().getStyle().setPaddingBottom(10, Unit.PX);

            CComponent<?> isSelected = inject(proto().isSelected());
            isSelected.asWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            isSelected.asWidget().getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            isSelected.asWidget().getElement().getStyle().setPaddingRight(15, Unit.PX);
            isSelected.asWidget().getElement().getStyle().setWidth(15, Unit.PX);
            isSelectedAndCaptionHolderPanel.add(isSelected);

            CComponent<?> caption = inject(proto().caption(), new PapReviewCaptionViewer());
            caption.asWidget().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            caption.asWidget().getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            isSelectedAndCaptionHolderPanel.add(caption);

            FlowPanel folderHolderPanel = new FlowPanel();
            isSelectedAndCaptionHolderPanel.add(inject(proto().charges(), new PapChargesFolder()));
            contentPanel.add(isSelectedAndCaptionHolderPanel);

            chargeTotals = new PapChargeReviewForm();
            chargeTotals.initContent();
            chargeTotals.setViewable(true);
            folderHolderPanel.add(chargeTotals);
            contentPanel.add(folderHolderPanel);

            addValueChangeHandler(new ValueChangeHandler<PapReviewDTO>() {
                @Override
                public void onValueChange(ValueChangeEvent<PapReviewDTO> event) {
                    recalculateChargesTotal();
                }
            });
            return contentPanel;
        }

        public void setSelected(boolean isSelected) {
            get(proto().isSelected()).populate(isSelected);
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            recalculateChargesTotal();
            chargeTotals.setVisible(getValue().charges().size() > 1);
            chargeTotals.asWidget().setVisible(getValue().charges().size() > 1);
        }

        private void recalculateChargesTotal() {
            if (getValue() != null) {
                PapChargesTotalDTO totals = summarizeCharges(getValue().charges());
                chargeTotals.populate(totals);
            }
        }

    }

    private static final class PapChargesFolder extends VistaBoxFolder<PapChargeReviewDTO> {

        public PapChargesFolder() {
            super(PapChargeReviewDTO.class);
            setAddable(false);
            setRemovable(false);
            setOrderable(false);
            asWidget().addStyleName(Styles.AutoPayChargesFolder.name());
        }

        @Override
        public IFolderItemDecorator<PapChargeReviewDTO> createItemDecorator() {
            VistaBoxFolderItemDecorator<PapChargeReviewDTO> itemDecorator = (VistaBoxFolderItemDecorator<PapChargeReviewDTO>) PapChargesFolder.super
                    .createItemDecorator();
            itemDecorator.setCollapsible(false);
            return itemDecorator;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof PapChargeReviewDTO) {
                return new PapChargeReviewForm();
            }
            return super.create(member);
        }

    }

    private static final class PapChargeReviewForm extends CEntityDecoratableForm<PapChargeReviewDTO> {

        public PapChargeReviewForm() {
            super(PapChargeReviewDTO.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel panel = new FlowPanel();
            panel.setStylePrimaryName(Styles.AutoPayCharge.name());

            panel.add(new MiniDecorator(inject(proto().chargeName()), Styles.AutoPayChargeNameColumn.name()));
            get(proto().chargeName()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().changeType()), Styles.AutoPayChargeNumberColumn.name()));
            get(proto().changeType()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().suspendedPrice()), Styles.AutoPayChargeNumberColumn.name()));
            get(proto().suspendedPrice()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().suspendedPapAmount()), Styles.AutoPayChargeNumberColumn.name()));
            get(proto().suspendedPapAmount()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().suspendedPapPercent()), Styles.AutoPayChargeNumberColumn.name()));
            get(proto().suspendedPapPercent()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().newPrice()), Styles.AutoPayChargeNumberColumn.name()));
            get(proto().newPrice()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().newPapAmount()), Styles.AutoPayChargeNumberColumn.name()));
            panel.add(new MiniDecorator(inject(proto().newPapPercent()), Styles.AutoPayChargeNumberColumn.name()));

            get(proto().newPapAmount()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
                @Override
                public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                    BigDecimal newPercent = event.getValue() != null ? event.getValue().divide(get(proto().newPrice()).getValue(), MathContext.DECIMAL32)
                            : null;
                    get(proto().newPapPercent()).setValue(newPercent, false);
                    updateChangePercent();
                }
            });
            get(proto().newPapPercent()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
                @Override
                public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                    BigDecimal newAmount = event.getValue() != null ? get(proto().newPrice()).getValue().multiply(event.getValue()) : null;
                    get(proto().newPapAmount()).setValue(newAmount, false);
                    updateChangePercent();
                }
            });

            panel.add(new MiniDecorator(inject(proto().changePercent()), Styles.AutoPayChargeNumberColumn.name()));
            get(proto().changePercent()).setViewable(true);

            return panel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().newPrice()).setVisible(getValue().changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed);
            get(proto().newPapAmount()).setVisible(getValue().changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed);
            get(proto().newPapPercent()).setVisible(getValue().changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed);
        }

        private void updateChangePercent() {

            BigDecimal changePercent = new BigDecimal("0.00");
            if (getValue().changeType().getValue() == ChangeType.New) {
                changePercent = get(proto().newPapAmount()).getValue().compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal("1.00") : BigDecimal.ZERO;
            }
            if (getValue().changeType().getValue() == ChangeType.Removed) {
                changePercent = new BigDecimal("-1.00");
            }
            if (getValue().changeType().getValue() == ChangeType.Changed || getValue().changeType().getValue() == ChangeType.Unchanged) {
                if (get(proto().newPapAmount()).getValue() != null) {
                    BigDecimal change = get(proto().newPapAmount()).getValue().subtract(get(proto().suspendedPapAmount()).getValue());
                    changePercent = change.divide(get(proto().suspendedPapAmount()).getValue(), MathContext.DECIMAL32);
                } else {
                    changePercent = null;
                }
            }
            get(proto().changePercent()).setValue(changePercent, false);
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
                        .divide(papChargesTotal.suspendedPapAmount().getValue(), MathContext.DECIMAL32));

        return papChargesTotal;
    }

}
