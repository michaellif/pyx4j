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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class RefineApartmentSearchForm extends CEntityForm<PropertySearchCriteria> {

    public static String DEFAULT_STYLE_PREFIX = "RefineApartmentSearch";

    public static enum StyleSuffix implements IStyleSuffix {
        SearchHeader, RowHeader, ButtonPanel
    }

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    private PropertyMapView.Presenter presenter;

    private VistaDecoratorsFlowPanel container;

    public RefineApartmentSearchForm() {
        super(PropertySearchCriteria.class);
    }

    @Override
    public IsWidget createContent() {
        container = new VistaDecoratorsFlowPanel();
        container.setStyleName(DEFAULT_STYLE_PREFIX);

        HTML label = new HTML("<label>" + i18n.tr("LOCATION") + "</label>");
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchHeader.name());
        container.add(label);

        addField(new CriteriaWidgetDecorator(inject(proto().province())));
        addBrake();
        addField(new CriteriaWidgetDecorator(inject(proto().city())));
        addBrake();

        label = new HTML("<label>" + i18n.tr("REFINE SEARCH") + "</label>");
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchHeader.name());
        container.add(label);
        addBrake();

        addField(new CriteriaWidgetDecorator(inject(proto().price())));
        addBrake();
        addField(new CriteriaWidgetDecorator(inject(proto().numOfBeds())));
        addBrake();
        addField(new CriteriaWidgetDecorator(inject(proto().numOfBath())));
        addBrake();

        FlowPanel search = new FlowPanel();
        Button searchBtn = new Button(i18n.tr("Search"));
        searchBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.goToPropertyMap((PropertySearchCriteria) null);
            }

        });
        search.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ButtonPanel);
        search.add(searchBtn);
        container.add(search);
        return container;
    }

    private void addField(Widget widget) {
        widget.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        container.add(widget);
    }

    private void addBrake() {
        HTML lineBrake = new HTML("&nbsp;");
        lineBrake.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.NONE);
        lineBrake.getElement().getStyle().setProperty("clear", "both");
        container.add(lineBrake);
    }

    public PropertyMapView.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(PropertyMapView.Presenter presenter) {
        this.presenter = presenter;
    }

}
