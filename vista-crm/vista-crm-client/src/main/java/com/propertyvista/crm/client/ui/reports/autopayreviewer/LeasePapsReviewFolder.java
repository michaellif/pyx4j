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
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapChargeTotalDTO;
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
            value.paps().add(summarize(value.paps()));
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
                        recalculateTotals();
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

        public void setTotals(LeasePapTotalsDTO totals) {
            for (CComponent<?> c : PapFolder.this.getComponents()) {
                if (c.getValue() instanceof LeasePapTotalsDTO) {
                    ((CComponent<PapDTO>) c).setValue(totals, false);
                }
            }
        }

        private void recalculateTotals() {
            IList<PapDTO> paps = PapFolder.this.getValue();
            LeasePapTotalsDTO totals = summarize(removeTotals(paps));
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
                return new PapChargeForm();
            }
            return super.create(member);
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
            return panel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            get(proto().newPrice()).setVisible(getValue().changeType().getValue() != PapChargeDTO.ChangeType.Removed);
            get(proto().newPreAuthorizedPaymentAmount()).setVisible(getValue().changeType().getValue() != PapChargeDTO.ChangeType.Removed);
            get(proto().newPreAuthorizedPaymentPercent()).setVisible(getValue().changeType().getValue() != PapChargeDTO.ChangeType.Removed);

            setViewable(getValue() instanceof PapChargeTotalDTO);
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

    private static LeasePapTotalsDTO summarize(Iterable<PapDTO> leasePaps) {
        LeasePapTotalsDTO totals = EntityFactory.create(LeasePapTotalsDTO.class);
        totals.setPrimaryKey(new Key(-1));

        for (PapDTO pap : leasePaps) {
            for (PapChargeDTO charge : pap.charges()) {
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
                    if (totalOfCharge.changeType().getValue() != ChangeType.Removed) {
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
                    }
                    totals.charges().add(totalOfCharge);
                }

            }

        }
        PapChargeTotalDTO papChargeTotal = EntityFactory.create(PapChargeTotalDTO.class);
        papChargeTotal.setPrimaryKey(new Key(-1));
        papChargeTotal.chargeName().setValue(i18n.tr("Total"));

        initIfNull(papChargeTotal.suspendedPrice());
        initIfNull(papChargeTotal.suspendedPreAuthorizedPaymentAmount());
        initIfNull(papChargeTotal.newPrice());
        initIfNull(papChargeTotal.newPreAuthorizedPaymentAmount());

        for (PapChargeDTO totalOfCharge : totals.charges()) {
            if (totalOfCharge.changeType().getValue() != ChangeType.New) {
                papChargeTotal.suspendedPrice().setValue(totalOfCharge.suspendedPrice().getValue().add(papChargeTotal.suspendedPrice().getValue()));
                papChargeTotal.suspendedPreAuthorizedPaymentAmount().setValue(
                        totalOfCharge.suspendedPreAuthorizedPaymentAmount().getValue().add(papChargeTotal.suspendedPreAuthorizedPaymentAmount().getValue()));
            }
            if (totalOfCharge.changeType().getValue() != ChangeType.Removed) {
                papChargeTotal.newPrice().setValue(totalOfCharge.newPrice().getValue().add(papChargeTotal.newPrice().getValue()));
                papChargeTotal.newPreAuthorizedPaymentAmount().setValue(
                        totalOfCharge.newPreAuthorizedPaymentAmount().getValue().add(papChargeTotal.newPreAuthorizedPaymentAmount().getValue()));
            }
        }
        papChargeTotal.suspendedPreAuthorizedPaymentPercent().setValue(
                papChargeTotal.suspendedPreAuthorizedPaymentAmount().getValue().divide(papChargeTotal.suspendedPrice().getValue(), MathContext.DECIMAL32));
        papChargeTotal.newPreAuthorizedPaymentPercent().setValue(
                papChargeTotal.newPreAuthorizedPaymentAmount().getValue().divide(papChargeTotal.newPrice().getValue(), MathContext.DECIMAL32));

        totals.charges().add(papChargeTotal);

        return totals;
    }
}
