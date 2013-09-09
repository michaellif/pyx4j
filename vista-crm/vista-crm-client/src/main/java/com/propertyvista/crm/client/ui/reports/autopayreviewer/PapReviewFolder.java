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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
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
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargesTotalDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapReviewDTO;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.PapChargeReviewDTO.ChangeType;

public class PapReviewFolder extends VistaBoxFolder<PapReviewDTO> {

    private static final I18n i18n = I18n.get(PapReviewFolder.class);

    public enum Styles implements IStyleName {

        AutoPayPapChargesContainer, AutoPayPapCharge, AutoPayPapChargeNameColumn, AutoPayPapChargeNumberColumn, AutoPayReviewSelected

    }

    public PapReviewFolder() {
        super(PapReviewDTO.class);
        setAddable(false);
        setRemovable(false);
        setOrderable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof PapReviewDTO) {
            return new PapForm();
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
                PapForm papForm = (PapForm) ((CEntityFolderItem<?>) c).getComponents().iterator().next();
                papForm.setSelected(true);
            }
        }
    }

    private static final class PapForm extends CEntityDecoratableForm<PapReviewDTO> {

        private PapChargeForm chargeTotals;

        public PapForm() {
            super(PapReviewDTO.class);
        }

        @Override
        public IsWidget createContent() {
            HTMLPanel contentPanel = new HTMLPanel(//@formatter:off
                    "<div>" +
                        "<div style='display:inline-block;'><span id='isSelected'></span></div>" +
                        "<div style='display:inline-block;'>" +
                            "<div>" +
                                "<div class='AutoPayReviewUpdaterPapCaptionPanel'><span id='captionLabel'></span></div>" +                                
                            "</div>" +
                            "<div>" +
                                "<div><span id='chargesFolder'></span></div>" +
                                "<div><span id='chargesTotals'></span></div>" +
                            "</div>" +
                        "</div>" +
                    "</div>"
            );//@formatter:on
            contentPanel.addAndReplaceElement(inject(proto().isSelected()), "isSelected");

            contentPanel.addAndReplaceElement(inject(proto().caption(), new PapReviewCaptionForm()), "captionLabel");
            contentPanel.addAndReplaceElement(inject(proto().charges(), new PapChargesFolder()), "chargesFolder");
            contentPanel.addAndReplaceElement(chargeTotals = new PapChargeForm(), "chargesTotals");
            chargeTotals.initContent();
            chargeTotals.setViewable(true);

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
            asWidget().addStyleName(Styles.AutoPayPapChargesContainer.name());
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
                return new PapChargeForm();
            }
            return super.create(member);
        }

    }

    private static final class PapChargeForm extends CEntityDecoratableForm<PapChargeReviewDTO> {

        public PapChargeForm() {
            super(PapChargeReviewDTO.class);
        }

        @Override
        public IsWidget createContent() {
            FlowPanel panel = new FlowPanel();
            panel.setStylePrimaryName(Styles.AutoPayPapCharge.name());

            panel.add(new MiniDecorator(inject(proto().chargeName()), Styles.AutoPayPapChargeNameColumn.name()));
            get(proto().chargeName()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().changeType()), Styles.AutoPayPapChargeNumberColumn.name()));
            get(proto().changeType()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().suspendedPrice()), Styles.AutoPayPapChargeNumberColumn.name()));
            get(proto().suspendedPrice()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().suspendedPreAuthorizedPaymentAmount()), Styles.AutoPayPapChargeNumberColumn.name()));
            get(proto().suspendedPreAuthorizedPaymentAmount()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().suspendedPreAuthorizedPaymentPercent()), Styles.AutoPayPapChargeNumberColumn.name()));
            get(proto().suspendedPreAuthorizedPaymentPercent()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().newPrice()), Styles.AutoPayPapChargeNumberColumn.name()));
            get(proto().newPrice()).setViewable(true);

            panel.add(new MiniDecorator(inject(proto().newPreAuthorizedPaymentAmount()), Styles.AutoPayPapChargeNumberColumn.name()));
            panel.add(new MiniDecorator(inject(proto().newPreAuthorizedPaymentPercent()), Styles.AutoPayPapChargeNumberColumn.name()));

            get(proto().newPreAuthorizedPaymentAmount()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
                @Override
                public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                    BigDecimal newPercent = event.getValue() != null ? event.getValue().divide(get(proto().newPrice()).getValue(), MathContext.DECIMAL32)
                            : null;
                    get(proto().newPreAuthorizedPaymentPercent()).setValue(newPercent, false);
                    updateChangePercent();
                }
            });
            get(proto().newPreAuthorizedPaymentPercent()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
                @Override
                public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                    BigDecimal newAmount = event.getValue() != null ? get(proto().newPrice()).getValue().multiply(event.getValue()) : null;
                    get(proto().newPreAuthorizedPaymentAmount()).setValue(newAmount, false);
                    updateChangePercent();
                }
            });

            panel.add(new MiniDecorator(inject(proto().changePercent()), Styles.AutoPayPapChargeNumberColumn.name()));
            get(proto().changePercent()).setViewable(true);

            return panel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            get(proto().newPrice()).setVisible(getValue().changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed);
            get(proto().newPreAuthorizedPaymentAmount()).setVisible(getValue().changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed);
            get(proto().newPreAuthorizedPaymentPercent()).setVisible(getValue().changeType().getValue() != PapChargeReviewDTO.ChangeType.Removed);
        }

        private void updateChangePercent() {

            BigDecimal changePercent = new BigDecimal("0.00");
            if (getValue().changeType().getValue() == ChangeType.New) {
                changePercent = get(proto().newPreAuthorizedPaymentAmount()).getValue().compareTo(BigDecimal.ZERO) != 0 ? new BigDecimal("1.00")
                        : BigDecimal.ZERO;
            }
            if (getValue().changeType().getValue() == ChangeType.Removed) {
                changePercent = new BigDecimal("-1.00");
            }
            if (getValue().changeType().getValue() == ChangeType.Changed) {
                BigDecimal change = get(proto().newPreAuthorizedPaymentAmount()).getValue().subtract(
                        get(proto().suspendedPreAuthorizedPaymentAmount()).getValue());
                changePercent = change.divide(get(proto().suspendedPreAuthorizedPaymentAmount()).getValue(), MathContext.DECIMAL32);
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
        initIfNull(papChargesTotal.suspendedPreAuthorizedPaymentAmount());
        initIfNull(papChargesTotal.newPrice());
        initIfNull(papChargesTotal.newPreAuthorizedPaymentAmount());

        for (PapChargeReviewDTO totalOfCharge : papCharges) {
            if (totalOfCharge.changeType().getValue() != ChangeType.New) {
                papChargesTotal.suspendedPrice().setValue(totalOfCharge.suspendedPrice().getValue().add(papChargesTotal.suspendedPrice().getValue()));
                papChargesTotal.suspendedPreAuthorizedPaymentAmount().setValue(
                        totalOfCharge.suspendedPreAuthorizedPaymentAmount().getValue().add(papChargesTotal.suspendedPreAuthorizedPaymentAmount().getValue()));
            }
            if (totalOfCharge.changeType().getValue() != ChangeType.Removed) {
                papChargesTotal.newPrice().setValue(totalOfCharge.newPrice().getValue().add(papChargesTotal.newPrice().getValue()));

                papChargesTotal.newPreAuthorizedPaymentAmount().setValue(
                        totalOfCharge.newPreAuthorizedPaymentAmount().getValue().add(papChargesTotal.newPreAuthorizedPaymentAmount().getValue()));
            }
        }
        if (papChargesTotal.suspendedPrice().getValue().compareTo(BigDecimal.ZERO) != 0) {
            papChargesTotal.suspendedPreAuthorizedPaymentPercent()
                    .setValue(
                            papChargesTotal.suspendedPreAuthorizedPaymentAmount().getValue()
                                    .divide(papChargesTotal.suspendedPrice().getValue(), MathContext.DECIMAL32));
        } else {
            papChargesTotal.suspendedPreAuthorizedPaymentPercent().setValue(new BigDecimal("0.00"));
        }
        if (papChargesTotal.newPrice().getValue().compareTo(BigDecimal.ZERO) != 0) {
            papChargesTotal.newPreAuthorizedPaymentPercent().setValue(
                    papChargesTotal.newPreAuthorizedPaymentAmount().getValue().divide(papChargesTotal.newPrice().getValue(), MathContext.DECIMAL32));
        } else {
            papChargesTotal.newPreAuthorizedPaymentPercent().setValue(new BigDecimal("0.00"));
        }

        papChargesTotal.changePercent().setValue(
                papChargesTotal.suspendedPreAuthorizedPaymentAmount().getValue().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : papChargesTotal
                        .newPreAuthorizedPaymentAmount().getValue().subtract(papChargesTotal.suspendedPreAuthorizedPaymentAmount().getValue())
                        .divide(papChargesTotal.suspendedPreAuthorizedPaymentAmount().getValue(), MathContext.DECIMAL32));

        return papChargesTotal;
    }

}
