/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.autopay;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.portal.rpc.portal.web.dto.AutoPayDTO;
import com.propertyvista.portal.web.client.themes.BlockMixin;
import com.propertyvista.portal.web.client.themes.EntityViewTheme;

public class AutoPaySubmissionForm extends CEntityForm<AutoPayDTO> {

    private static final I18n i18n = I18n.get(AutoPaySubmissionForm.class);

    private static String cutOffDateWarning = i18n.tr("All changes will take effect after this date!");

    public AutoPaySubmissionForm() {
        super(AutoPayDTO.class);
        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel mainPanel = new TwoColumnFlexFormPanel();
        int row = -1;
        Widget w;

        mainPanel.setWidget(++row, 0, w = new HTML(i18n.tr("Automatic Payment Submitted Successfully!")));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        w.getElement().getStyle().setFontSize(1.2, Unit.EM);

        mainPanel.setBR(++row, 0, 1);

        mainPanel.setWidget(++row, 0,
                new FormDecoratorBuilder(inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()), 22).labelAlignment(Alignment.left).build());

        mainPanel.setWidget(++row, 0, inject(proto().coveredItems(), new PapCoveredItemFolder()));
        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().total()), 22).build());

        mainPanel.setBR(++row, 0, 1);

        mainPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nextScheduledPaymentDate(), new CDateLabel()), 22).build());
        mainPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        mainPanel.setHR(++row, 0, 1);

        SimplePanel contentPanel = new SimplePanel(mainPanel);
        contentPanel.setStyleName(EntityViewTheme.StyleName.EntityViewContent.name());
        contentPanel.addStyleName(BlockMixin.StyleName.PortalBlock.name());
        contentPanel.getElement().getStyle().setProperty("borderTopWidth", "5px");
        contentPanel.getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));

        return contentPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        LogicalDate today = new LogicalDate(ClientContext.getServerDate());
        if (!today.before(getValue().paymentCutOffDate().getValue()) && !today.after(getValue().nextScheduledPaymentDate().getValue())) {
            get(proto().nextScheduledPaymentDate()).setNote(cutOffDateWarning, NoteStyle.Warn);
        } else {
            get(proto().nextScheduledPaymentDate()).setNote(null);
        }
    }
}
