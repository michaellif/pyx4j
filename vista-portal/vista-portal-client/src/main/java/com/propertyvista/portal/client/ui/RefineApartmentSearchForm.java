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
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class RefineApartmentSearchForm extends CEntityForm<PropertySearchCriteria> {

    public static String DEFAULT_STYLE_PREFIX = "RefineApartmentSearch";

    public static enum StyleSuffix implements IStyleSuffix {
        SearchHeader, RowHeader, Element, ButtonPanel
    }

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    private PropertyMapView.Presenter presenter;

    public RefineApartmentSearchForm() {
        super(PropertySearchCriteria.class);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel container = new VistaDecoratorsFlowPanel();
        container.setStyleName(DEFAULT_STYLE_PREFIX);

        HTML label = new HTML("<label>" + i18n.tr("LOCATION") + "</label>");
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchHeader.name());
        container.add(label);

        FlowPanel field1 = new FlowPanel();
        field1.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        CComboBox<String> provinceCombo = new CComboBox<String>();
        provinceCombo.asWidget().setName("province");
        provinceCombo.setWidth("100%");
        label = new HTML("<label for=\"province\">" + i18n.tr(proto().province().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field1.add(label);
        field1.add(inject(proto().province(), provinceCombo));
        container.add(field1);

        FlowPanel field2 = new FlowPanel();
        field2.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        CComboBox<String> cityCombo = new CComboBox<String>();
        cityCombo.asWidget().setName("city");
        cityCombo.setWidth("100%");
        label = new HTML("<label for=\"city\">" + i18n.tr(proto().city().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field2.add(label);
        field2.add(inject(proto().city(), cityCombo));
        container.add(field2);

        label = new HTML("<label>" + i18n.tr("REFINE SEARCH") + "</label>");
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchHeader.name());
        container.add(label);

        FlowPanel field3 = new FlowPanel();
        field3.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        CComboBox<Double> minPriceCombo = new CComboBox<Double>();
        minPriceCombo.asWidget().setName("minp");
        minPriceCombo.setWidth("100%");

        CComboBox<Double> maxPriceCombo = new CComboBox<Double>();
        maxPriceCombo.setWidth("100%");

        label = new HTML("<label for=\"minp\">" + i18n.tr("Price Range") + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field3.add(label);
        field3.add(DecorationUtils.inline(inject(proto().minPrice(), minPriceCombo), "45%"));
        field3.add(DecorationUtils.inline(new HTML("<span>-</span>"), "10%"));
        field3.add(DecorationUtils.inline(inject(proto().maxPrice(), maxPriceCombo), "45%"));
        container.add(field3);

        FlowPanel field5 = new FlowPanel();
        field5.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        CComboBox<Integer> bedsCombo = new CComboBox<Integer>();
        bedsCombo.asWidget().setName("beds");
        bedsCombo.setWidth("100%");
        label = new HTML("<label for=\"beds\">" + i18n.tr(proto().numOfBeds().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field5.add(label);
        field5.add(inject(proto().numOfBeds(), bedsCombo));
        container.add(field5);

        FlowPanel field6 = new FlowPanel();
        field6.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        CComboBox<Integer> bathCombo = new CComboBox<Integer>();
        bathCombo.asWidget().setName("baths");
        bathCombo.setWidth("100%");
        label = new HTML("<label for=\"baths\">" + i18n.tr(proto().numOfBath().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field6.add(label);
        field6.add(inject(proto().numOfBath(), bathCombo));
        container.add(field6);

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

    public PropertyMapView.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(PropertyMapView.Presenter presenter) {
        this.presenter = presenter;
    }

}
