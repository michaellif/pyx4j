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
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapsReviewDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapChargeDTO;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.PapDTO;

public class LeasePapsReviewFolder extends VistaBoxFolder<LeasePapsReviewDTO> {

    public enum Styles implements IStyleName {
        AutoPayReviewUpdaterLeaseCaption, AutoPayPapChargesContainer, AutoPayPapCharge, AutoPayPapChargeNameColumn, AutoPayPapChargeNumberColumn
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
                        "<div class='AutoPayReviewUpdaterLeaseCaption'><span id='leaseCaption'></span></div>" +
                        "<div id='folder'></div>" +
                        "<div id='leaseTotal'></div>" +
                    "</div>");
            //@formatter:on

            contentPanel.addAndReplaceElement(leaseCaption = new HTML(), "leaseCaption");
            contentPanel.addAndReplaceElement(inject(proto().paps(), new PapFolder()), "folder");
            contentPanel.addAndReplaceElement(new HTML("Lease Total"), "leaseTotal");
            return contentPanel;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            leaseCaption.setHTML(new SafeHtmlBuilder().appendEscaped(
                    i18n.tr("Lease: {0} Unit: {1}", getValue().lease().leaseId().getValue(), getValue().lease().unit().info().number().getValue()))
                    .toSafeHtml());
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
                return new PapForm();
            }
            return super.create(member);
        }

        @Override
        public IFolderItemDecorator<PapDTO> createItemDecorator() {
            VistaBoxFolderItemDecorator<PapDTO> itemDecorator = (VistaBoxFolderItemDecorator<PapDTO>) PapFolder.super.createItemDecorator();
            itemDecorator.setCollapsible(false);
            return itemDecorator;
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
                            "<div style='float:right;'><span id='isMarkedAsRevised'></span></div>" +
                        "</div>" +
                    "</div>"
            );//@formatter:on
            contentPanel.addAndReplaceElement(inject(proto().tenantAndPaymentMethod(), new CLabel<String>()), "tenantAndPaymentMethodCaption");

            contentPanel.addAndReplaceElement(inject(proto().charges(), new PapChargesFolder()), "chargesFolder");
            contentPanel.addAndReplaceElement(inject(proto().isMarkedAsRevised()), "isMarkedAsRevised");
            return contentPanel;
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
}
