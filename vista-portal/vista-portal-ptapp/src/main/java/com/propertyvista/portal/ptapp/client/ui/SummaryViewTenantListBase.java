/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.editor.FolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.widgets.client.ImageButton;

import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderItemDecorator;

public abstract class SummaryViewTenantListBase<E extends IEntity> extends CEntityFolderItem<E> {

    protected final I18n i18n = I18nFactory.getI18n(SummaryViewTenantListBase.class);

    protected IsWidget fullView;

    public SummaryViewTenantListBase(Class<E> clazz) {
        super(clazz);
    }

    @Override
    public IsWidget createContent() {

        FlowPanel main = new FlowPanel();

        main.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        main.getElement().getStyle().setBackgroundColor("white");

        main.getElement().getStyle().setBorderWidth(1, Unit.PX);
        main.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
        main.getElement().getStyle().setBorderColor("#bbb");
        main.getElement().getStyle().setMarginBottom(0.5, Unit.EM);

        main.getElement().getStyle().setPaddingTop(0.5, Unit.EM);
        main.getElement().getStyle().setPaddingBottom(0.5, Unit.EM);
        main.getElement().getStyle().setPaddingLeft(15, Unit.PX);
        main.getElement().getStyle().setPaddingRight(15, Unit.PX);
        main.setWidth("670px");

        main.add(bindCompactView());
        fullView = bindFullView();
        fullView.asWidget().setVisible(false);
        main.add(fullView);

        return main;
    }

    @Override
    public FolderItemDecorator createFolderItemDecorator() {
        return new BoxReadOnlyFolderItemDecorator(false);
    }

    public IsWidget bindCompactView() {

        HorizontalPanel panel = new HorizontalPanel();

        IsWidget switcher;
        panel.add(switcher = addViewSwitcher());

        IsWidget tenant = getTenantFullName();
        tenant.asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
        tenant.asWidget().getElement().getStyle().setFontSize(1.5, Unit.EM);
        tenant.asWidget().getElement().getStyle().setPaddingTop(0.2, Unit.EM);
        tenant.asWidget().getElement().getStyle().setPaddingBottom(0.3, Unit.EM);
        tenant.asWidget().getElement().getStyle().setMarginLeft(2, Unit.EM);
        panel.add(tenant);

        panel.setCellVerticalAlignment(switcher.asWidget(), HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setCellVerticalAlignment(tenant.asWidget(), HasVerticalAlignment.ALIGN_MIDDLE);
        return panel;
    }

    public abstract IsWidget getTenantFullName();

    public abstract IsWidget bindFullView();

    protected IsWidget addViewSwitcher() {

        final Image switcher = new ImageButton(PortalImages.INSTANCE.pointerCollapsed(), PortalImages.INSTANCE.pointerExpanded());
        switcher.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (fullView.asWidget().isVisible()) {
                    fullView.asWidget().setVisible(false);
                } else {
                    fullView.asWidget().setVisible(true);
                }
            }
        });

        return switcher;
    }
}
