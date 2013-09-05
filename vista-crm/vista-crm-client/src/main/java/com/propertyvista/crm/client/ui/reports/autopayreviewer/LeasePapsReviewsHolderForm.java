/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-30
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports.autopayreviewer;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.crm.client.ui.reports.autopayreviewer.dto.LeasePapsReviewsHolderDTO;

public final class LeasePapsReviewsHolderForm extends CEntityDecoratableForm<LeasePapsReviewsHolderDTO> {

    private final static I18n i18n = I18n.get(LeasePapsReviewFolder.class);

    public enum Styles implements IStyleName {

        AutoPaySuperCaptionsPanel, AutoPayCaptionsPanel

    }

    public LeasePapsReviewsHolderForm() {
        super(LeasePapsReviewsHolderDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();
        panel.getElement().getStyle().setPosition(Position.ABSOLUTE);
        panel.getElement().getStyle().setTop(0, Unit.PX);
        panel.getElement().getStyle().setLeft(0, Unit.PX);
        panel.getElement().getStyle().setRight(0, Unit.PX);
        panel.getElement().getStyle().setBottom(0, Unit.PX);

        FlowPanel superCaptionsPanel = new FlowPanel();
        superCaptionsPanel.addStyleName(Styles.AutoPaySuperCaptionsPanel.name());
        superCaptionsPanel.add(new HTML(i18n.tr("Suspended")));
        superCaptionsPanel.add(new HTML(i18n.tr("Suggested")));
        panel.add(superCaptionsPanel);

        FlowPanel captionsPanel = new FlowPanel();
        captionsPanel.addStyleName(Styles.AutoPayCaptionsPanel.name());
        captionsPanel.add(new HTML(i18n.tr("Price")));
        captionsPanel.add(new HTML(i18n.tr("Payment")));
        captionsPanel.add(new HTML(i18n.tr("%")));
        captionsPanel.add(new HTML(i18n.tr("Price")));
        captionsPanel.add(new HTML(i18n.tr("Payment")));
        captionsPanel.add(new HTML(i18n.tr("%")));

        panel.add(captionsPanel);
        FlowPanel leasePapsFolderHolder = new FlowPanel();
        leasePapsFolderHolder.getElement().getStyle().setOverflow(Overflow.AUTO);
        leasePapsFolderHolder.getElement().getStyle().setPosition(Position.ABSOLUTE);
        leasePapsFolderHolder.getElement().getStyle().setTop(50, Unit.PX);
        leasePapsFolderHolder.getElement().getStyle().setBottom(0, Unit.PX);
        leasePapsFolderHolder.getElement().getStyle().setLeft(0, Unit.PX);
        leasePapsFolderHolder.getElement().getStyle().setRight(0, Unit.PX);
        leasePapsFolderHolder.add(inject(proto().leasePapsReviews(), new LeasePapsReviewFolder()));

        HTML more = new HTML(i18n.tr("More..."));
        more.getElement().getStyle().setTextAlign(TextAlign.CENTER);
        more.getElement().getStyle().setCursor(Cursor.POINTER);
        more.getElement().getStyle().setLineHeight(5, Unit.EM);
        more.getElement().getStyle().setWidth(100, Unit.PCT);
        more.getElement().getStyle().setHeight(5, Unit.EM);
        leasePapsFolderHolder.add(more);

        panel.add(leasePapsFolderHolder);

        return panel;
    }
}