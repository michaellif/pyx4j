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
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureAgreementDTO;
import com.propertyvista.portal.web.client.themes.BlockMixin;
import com.propertyvista.portal.web.client.themes.EntityViewTheme;

public class TenantSureConfirmationForm extends CEntityForm<TenantSureAgreementDTO> {

    private static final I18n i18n = I18n.get(TenantSureConfirmationForm.class);

    private final TenantSureConfirmationViewImpl view;

    public TenantSureConfirmationForm(TenantSureConfirmationViewImpl view) {
        super(TenantSureAgreementDTO.class);
        this.view = view;
        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel mainPanel = new TwoColumnFlexFormPanel();
        int row = -1;
        Widget w;

        mainPanel.setWidget(++row, 0, w = new HTML(i18n.tr("Insurance REquest submitted Successfully!")));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        w.getElement().getStyle().setFontSize(1.2, Unit.EM);

        SimplePanel contentPanel = new SimplePanel(mainPanel);
        contentPanel.setStyleName(EntityViewTheme.StyleName.EntityViewContent.name());
        contentPanel.addStyleName(BlockMixin.StyleName.PortalBlock.name());
        contentPanel.getElement().getStyle().setProperty("borderTopWidth", "5px");
        contentPanel.getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(ThemeColor.contrast4, 1));

        return contentPanel;
    }

}
