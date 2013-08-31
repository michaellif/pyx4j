/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.dashboard;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.portal.domain.dto.financial.PaymentInfoDTO;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPaySummaryDTO;
import com.propertyvista.portal.rpc.portal.web.dto.BillingSummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class AutoPayAgreementsGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(AutoPayAgreementsGadget.class);

    private final AutoPayViewer autoPayViewer;

    AutoPayAgreementsGadget(FinancialDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.billingIcon(), i18n.tr("Auto Pay Agreements"), ThemeColor.contrast4);

        autoPayViewer = new AutoPayViewer();
        autoPayViewer.setViewable(true);
        autoPayViewer.initContent();

        SimplePanel contentPanel = new SimplePanel(autoPayViewer.asWidget());
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        setContent(contentPanel);
    }

    protected void populate(AutoPaySummaryDTO value) {

    }

    //  getGadgetViewer().getPresenter().setAutopay();

    class AutoPayViewer extends CEntityForm<BillingSummaryDTO> {

        private final BasicFlexFormPanel mainPanel;

        public AutoPayViewer() {
            super(BillingSummaryDTO.class);

            mainPanel = new BasicFlexFormPanel();

            doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

            AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

                @Override
                public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                    doLayout(event.getLayoutType());
                }

            });
        }

        private void doLayout(LayoutType layoutType) {

        }

        @Override
        public IsWidget createContent() {

            mainPanel.getElement().getStyle().setProperty("margin", "0 5%");
            mainPanel.setWidget(0, 0, new FormDecoratorBuilder(inject(proto().currentBalance()), "140px", "100px", "120px").build());
            mainPanel.setWidget(1, 0, new FormDecoratorBuilder(inject(proto().dueDate()), "140px", "100px", "120px").build());

            return mainPanel;
        }
    }

    class AutoPayFolder extends VistaTableFolder<PaymentInfoDTO> {

        public AutoPayFolder() {
            super(PaymentInfoDTO.class, false);
            setViewable(true);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(// @formatter:off
                    new EntityFolderColumnDescriptor(proto().amount(), "7em"),
                    new EntityFolderColumnDescriptor(proto().paymentDate(), "9em"),
                    new EntityFolderColumnDescriptor(proto().paymentMethod().type(), "10em"),
                    new EntityFolderColumnDescriptor(proto().payer(), "20em")
            ); // formatter:on
        }
    }
}
