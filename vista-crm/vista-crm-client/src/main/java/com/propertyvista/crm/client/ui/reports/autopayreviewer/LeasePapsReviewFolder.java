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
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapTotalsDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapsReviewDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapChargeDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapChargeDTO.ChangeType;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapChargesTotalDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapDTO;

public class LeasePapsReviewFolder extends VistaBoxFolder<LeasePapsReviewDTO> {

    public enum Styles implements IStyleName {
        AutoPayReviewUpdaterLeaseCaption, AutoPayPapChargesContainer, AutoPayPapCharge, AutoPayPapChargeNameColumn, AutoPayPapChargeNumberColumn, AutoPayReviewSelected
    }

    private static final I18n i18n = I18n.get(LeasePapsReviewFolder.class);

    public LeasePapsReviewFolder() {
        super(LeasePapsReviewDTO.class);
        setAddable(false);
        setRemovable(false);
        setOrderable(false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof LeasePapsReviewDTO) {
            return new LeasePapsReviewForm();
        }
        return super.create(member);
    }

    @Override
    public IFolderItemDecorator<LeasePapsReviewDTO> createItemDecorator() {
        VistaBoxFolderItemDecorator<LeasePapsReviewDTO> itemDecorator = (VistaBoxFolderItemDecorator<LeasePapsReviewDTO>) super.createItemDecorator();
        itemDecorator.setCollapsible(false);
        return itemDecorator;
    }

    private static final class LeasePapsReviewForm extends CEntityDecoratableForm<LeasePapsReviewDTO> {

        private HTML leaseCaption;

        public LeasePapsReviewForm() {
            super(LeasePapsReviewDTO.class);
        }

        @Override
        public IsWidget createContent() {
            HTMLPanel contentPanel = new HTMLPanel(//@formatter:off
                    "<div>" +                        
                        "<div class='AutoPayReviewUpdaterLeaseCaption'><div class='AutoPayReviewSelected'><span id='selectionMark'></span></div><span id='leaseCaption'></span></div>" +
                        "<div id='folder'></div>" +                        
                    "</div>"
            );//@formatter:on

            contentPanel.addAndReplaceElement(leaseCaption = new HTML(), "leaseCaption");
            contentPanel.addAndReplaceElement(inject(proto().isSelected()), "selectionMark");
            contentPanel.addAndReplaceElement(inject(proto().paps(), new PapFolder()), "folder");
            return contentPanel;
        }

        @Override
        protected LeasePapsReviewDTO preprocessValue(LeasePapsReviewDTO value, boolean fireEvent, boolean populate) {
            value.paps().add(summarizePaps(value.paps()));
            return super.preprocessValue(value, fireEvent, populate);
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            leaseCaption.setHTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Lease: {0} Unit: {1} Buidling: {2} Move Out: {3,date,short}",

            getValue().lease().leaseId().getValue(),

            getValue().lease().unit().info().number().getValue(),

            getValue().lease().unit().building().propertyCode().getValue(),

            getValue().lease().expectedMoveOut().getValue()

            )).toSafeHtml());
        }

    }

    private static final class PapFolder extends VistaBoxFolder<PapDTO> {

        public PapFolder() {
            super(PapDTO.class);
            setAddable(false);
            setRemovable(false);
            setOrderable(false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof PapDTO) {
                PapForm form = new PapForm();
                form.addValueChangeHandler(new ValueChangeHandler<PapDTO>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<PapDTO> event) {
                        recalculateLeaseTotals();
                    }
                });
                return form;
            }
            return super.create(member);
        }

        @Override
        public IFolderItemDecorator<PapDTO> createItemDecorator() {
            VistaBoxFolderItemDecorator<PapDTO> itemDecorator = (VistaBoxFolderItemDecorator<PapDTO>) PapFolder.super.createItemDecorator();
            itemDecorator.setCollapsible(false);
            return itemDecorator;
        }

        private void setTotals(LeasePapTotalsDTO totals) {
            for (CComponent<?> c : PapFolder.this.getComponents()) {
                if (c.getValue() instanceof LeasePapTotalsDTO) {
                    ((CComponent<PapDTO>) c).setValue(totals, false);
                }
            }
        }

        private void recalculateLeaseTotals() {
            IList<PapDTO> paps = PapFolder.this.getValue();
            LeasePapTotalsDTO totals = summarizePaps(removeTotals(paps));
            setTotals(totals);
        }
    }

    private static final class PapForm extends CEntityDecoratableForm<PapDTO> {

        public PapForm() {
            super(PapDTO.class);
        }

        @Override
        public IsWidget createContent() {
            HTMLPanel contentPanel = new HTMLPanel(//@formatter:off
                    "<div>" +
                        "<div><span id='tenantAndPaymentMethodCaption'></span></div>" +
                        "<div>" +
                            "<div style='float:left;'><span id='chargesFolder'></span></div>" +
                        "</div>" +
                    "</div>"
            );//@formatter:on
            contentPanel.addAndReplaceElement(inject(proto().tenantAndPaymentMethod(), new CLabel<String>()), "tenantAndPaymentMethodCaption");
            contentPanel.addAndReplaceElement(inject(proto().charges(), new PapChargesFolder()), "chargesFolder");
            return contentPanel;
        }

        @Override
        protected PapDTO preprocessValue(PapDTO value, boolean fireEvent, boolean populate) {
            if (value instanceof LeasePapTotalsDTO) {
                value.tenantAndPaymentMethod().setValue(i18n.tr("Lease Total"));
            }
            value.charges().add(summarizeCharges(removeChargesTotal(value.charges())));
            return super.preprocessValue(value, fireEvent, populate);
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            setViewable(getValue() instanceof LeasePapTotalsDTO);
        }
    }

    private static final class PapChargesFolder extends VistaBoxFolder<PapChargeDTO> {

        public PapChargesFolder() {
            super(PapChargeDTO.class);
            setAddable(false);
            setRemovable(false);
            setOrderable(false);
            asWidget().addStyleName(Styles.AutoPayPapChargesContainer.name());
        }

        @Override
        public IFolderItemDecorator<PapChargeDTO> createItemDecorator() {
            VistaBoxFolderItemDecorator<PapChargeDTO> itemDecorator = (VistaBoxFolderItemDecorator<PapChargeDTO>) PapChargesFolder.super.createItemDecorator();
            itemDecorator.setCollapsible(false);
            return itemDecorator;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof PapChargeDTO) {
                PapChargeForm form = new PapChargeForm();
                form.addValueChangeHandler(new ValueChangeHandler<PapChargeDTO>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<PapChargeDTO> event) {
                        recalculateChargesTotal();
                    }
                });
                return form;
            }
            return super.create(member);
        }

        private void setTotals(PapChargesTotalDTO totals) {
            for (CComponent<?> c : PapChargesFolder.this.getComponents()) {
                if (c.getValue() instanceof PapChargesTotalDTO) {
                    ((CComponent<PapChargeDTO>) c).setValue(totals, false);
                }
            }
        }

        private void recalculateChargesTotal() {
            IList<PapChargeDTO> paps = PapChargesFolder.this.getValue();
            PapChargesTotalDTO totals = summarizeCharges(removeChargesTotal(paps));
            setTotals(totals);
        }

    }

    private static final class PapChargeForm extends CEntityDecoratableForm<PapChargeDTO> {

        public PapChargeForm() {
            super(PapChargeDTO.class);
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

                }
            });
            get(proto().newPreAuthorizedPaymentPercent()).addValueChangeHandler(new ValueChangeHandler<BigDecimal>() {
                @Override
                public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                    BigDecimal newAmount = event.getValue() != null ? get(proto().newPrice()).getValue().multiply(event.getValue()) : null;
                    get(proto().newPreAuthorizedPaymentAmount()).setValue(newAmount, false);
                }
            });

            panel.add((inject(proto().discardCharge())));
            get(proto().discardCharge()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    get(proto().newPreAuthorizedPaymentAmount()).setVisible(!event.getValue());
                    get(proto().newPreAuthorizedPaymentPercent()).setVisible(!event.getValue());
                }
            });
            return panel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            setViewable(getValue() instanceof PapChargesTotalDTO);

            get(proto().newPrice()).setVisible(getValue().changeType().getValue() != PapChargeDTO.ChangeType.Removed);
            get(proto().newPreAuthorizedPaymentAmount()).setVisible(getValue().changeType().getValue() != PapChargeDTO.ChangeType.Removed);
            get(proto().newPreAuthorizedPaymentPercent()).setVisible(getValue().changeType().getValue() != PapChargeDTO.ChangeType.Removed);
            get(proto().discardCharge()).setVisible(
                    !isViewable() && (getValue().changeType().getValue() != PapChargeDTO.ChangeType.Removed) && !(getValue() instanceof PapChargesTotalDTO));

        }
    }

    private static class MiniDecorator extends SimplePanel {

        public MiniDecorator(Widget widget, String styleName) {
            setWidget(widget);
            addStyleName(styleName);
        }

        public MiniDecorator(IsWidget widget, String styleName) {
            this(widget.asWidget(), styleName);
        }
    }

    private static void initIfNull(IPrimitive<BigDecimal> member) {
        if (member.isNull()) {
            member.setValue(new BigDecimal("0.00"));
        }
    }

    private static Iterable<PapDTO> removeTotals(Iterable<PapDTO> leasePaps) {
        List<PapDTO> paps = new LinkedList<PapDTO>();
        for (PapDTO pap : leasePaps) {
            if (!(pap instanceof LeasePapTotalsDTO)) {
                paps.add(pap);
            }
        }
        return paps;
    }

    private static Iterable<PapChargeDTO> removeChargesTotal(IList<PapChargeDTO> charges) {
        List<PapChargeDTO> paps = new LinkedList<PapChargeDTO>();
        for (PapChargeDTO pap : charges) {
            if (!(pap instanceof PapChargesTotalDTO)) {
                paps.add(pap);
            }
        }
        return paps;
    }

    private static LeasePapTotalsDTO summarizePaps(Iterable<PapDTO> leasePaps) {
        LeasePapTotalsDTO totals = EntityFactory.create(LeasePapTotalsDTO.class);
        totals.setPrimaryKey(new Key(-1));

        for (PapDTO pap : leasePaps) {
            for (PapChargeDTO charge : pap.charges()) {
                if ((charge instanceof PapChargesTotalDTO)) {
                    continue;
                }
                PapChargeDTO totalOfCharge = null;
                foundTotalOfCharge: for (PapChargeDTO totalOfChargeCandidate : totals.charges()) {
                    if (totalOfChargeCandidate.getPrimaryKey().equals(charge.getPrimaryKey())) {
                        totalOfCharge = totalOfChargeCandidate;
                        break foundTotalOfCharge;
                    }
                }
                if (totalOfCharge != null) {
                    if (totalOfCharge.changeType().getValue() != ChangeType.New) {
                        totalOfCharge.suspendedPreAuthorizedPaymentAmount().setValue(
                                totalOfCharge.suspendedPreAuthorizedPaymentAmount().getValue().add(charge.suspendedPreAuthorizedPaymentAmount().getValue()));
                        totalOfCharge.suspendedPreAuthorizedPaymentPercent().setValue(
                                totalOfCharge.suspendedPreAuthorizedPaymentPercent().getValue().add(charge.suspendedPreAuthorizedPaymentPercent().getValue()));
                    }
                    if (totalOfCharge.changeType().getValue() != ChangeType.Removed && !charge.discardCharge().isBooleanTrue()) {
                        totalOfCharge.newPreAuthorizedPaymentAmount().setValue(
                                totalOfCharge.newPreAuthorizedPaymentAmount().getValue().add(charge.newPreAuthorizedPaymentAmount().getValue()));
                        totalOfCharge.newPreAuthorizedPaymentPercent().setValue(
                                totalOfCharge.newPreAuthorizedPaymentPercent().getValue().add(charge.newPreAuthorizedPaymentPercent().getValue()));
                    }
                } else {
                    totalOfCharge = charge.duplicate(PapChargeDTO.class);
                    if (totalOfCharge.changeType().getValue() != ChangeType.New) {
                        initIfNull(totalOfCharge.suspendedPreAuthorizedPaymentAmount());
                        initIfNull(totalOfCharge.suspendedPreAuthorizedPaymentPercent());
                    }
                    if (totalOfCharge.changeType().getValue() != ChangeType.Removed) {
                        initIfNull(totalOfCharge.newPreAuthorizedPaymentAmount());
                        initIfNull(totalOfCharge.newPreAuthorizedPaymentPercent());
                        if (charge.discardCharge().isBooleanTrue()) {
                            totalOfCharge.newPreAuthorizedPaymentAmount().setValue(new BigDecimal("0.00"));
                            totalOfCharge.newPreAuthorizedPaymentPercent().setValue(new BigDecimal("0.00"));
                        }
                    }
                    totals.charges().add(totalOfCharge);
                }

            }

        }
        return totals;
    }

    private static PapChargesTotalDTO summarizeCharges(Iterable<PapChargeDTO> papCharges) {
        PapChargesTotalDTO papChargesTotal = EntityFactory.create(PapChargesTotalDTO.class);
        papChargesTotal.setPrimaryKey(new Key(-1));
        papChargesTotal.chargeName().setValue(i18n.tr("Total"));

        initIfNull(papChargesTotal.suspendedPrice());
        initIfNull(papChargesTotal.suspendedPreAuthorizedPaymentAmount());
        initIfNull(papChargesTotal.newPrice());
        initIfNull(papChargesTotal.newPreAuthorizedPaymentAmount());

        for (PapChargeDTO totalOfCharge : papCharges) {
            if (totalOfCharge.changeType().getValue() != ChangeType.New) {
                papChargesTotal.suspendedPrice().setValue(totalOfCharge.suspendedPrice().getValue().add(papChargesTotal.suspendedPrice().getValue()));
                papChargesTotal.suspendedPreAuthorizedPaymentAmount().setValue(
                        totalOfCharge.suspendedPreAuthorizedPaymentAmount().getValue().add(papChargesTotal.suspendedPreAuthorizedPaymentAmount().getValue()));
            }
            if (totalOfCharge.changeType().getValue() != ChangeType.Removed) {
                papChargesTotal.newPrice().setValue(totalOfCharge.newPrice().getValue().add(papChargesTotal.newPrice().getValue()));
                if (!totalOfCharge.discardCharge().isBooleanTrue()) {
                    papChargesTotal.newPreAuthorizedPaymentAmount().setValue(
                            totalOfCharge.newPreAuthorizedPaymentAmount().getValue().add(papChargesTotal.newPreAuthorizedPaymentAmount().getValue()));
                }
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

        return papChargesTotal;
    }

}
