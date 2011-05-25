/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class RefineApartmentSearchForm extends CEntityForm<PropertySearchCriteria> {

    public static String DEFAULT_STYLE_PREFIX = "RefineApartmentSearch";

    public static enum StyleSuffix implements IStyleSuffix {
        SearchHeader, RowHeader, ButtonPanel
    }

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    private PropertyMapView.Presenter presenter;

    private FlowPanel container;

    public RefineApartmentSearchForm() {
        super(PropertySearchCriteria.class);
    }

    @Override
    public IsWidget createContent() {
        container = new FlowPanel();
        container.setStyleName(DEFAULT_STYLE_PREFIX);

        Label label = new Label(i18n.tr("LOCATION"));
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchHeader.name());
        container.add(label);

        addField(new CriteriaWidgetDecorator(inject(proto().province())));
        addField(new CriteriaWidgetDecorator(inject(proto().city())));

        FlowPanel searchPanel = new FlowPanel();
        Button searchBtn = new Button(i18n.tr("Search"));
        searchBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.goToPropertyMap((PropertySearchCriteria) null);
            }

        });
        searchPanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ButtonPanel);
        searchPanel.add(searchBtn);
        container.add(searchPanel);

        label = new Label(i18n.tr("REFINE SEARCH"));
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchHeader.name());
        container.add(label);

        addField(new CriteriaWidgetDecorator(inject(proto().price())));
        addField(new CriteriaWidgetDecorator(inject(proto().numOfBeds())));
        addField(new CriteriaWidgetDecorator(inject(proto().numOfBath())));

        FlowPanel updatePanel = new FlowPanel();
        Button updateBtn = new Button(i18n.tr("Update"));
        updateBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.goToPropertyMap((PropertySearchCriteria) null);
            }

        });
        updatePanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ButtonPanel);
        updatePanel.add(updateBtn);

        container.add(updatePanel);
        return container;
    }

    private void addField(Widget widget) {
        widget.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        container.add(widget);
    }

    public PropertyMapView.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(PropertyMapView.Presenter presenter) {
        this.presenter = presenter;
    }

}
