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
package com.propertyvista.portal.client.ui.searchapt;

import java.util.Arrays;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.EntityDataSource;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.widgets.client.style.CSSClass;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria.SearchType;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;

public class RefineApartmentSearchForm extends CEntityForm<PropertySearchCriteria> {

    public static String DEFAULT_STYLE_PREFIX = "RefineApartmentSearch";

    public static enum StyleSuffix implements IStyleSuffix {
        SearchHeader, RowHeader, ButtonPanel, Holder, LabelHolder, Tab, StatusHolder, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, current
    }

    private static I18n i18n = I18nFactory.getI18n(PropertyMapViewImpl.class);

    private PropertyMapView.Presenter presenter;

    private VerticalPanel container;

    public RefineApartmentSearchForm() {
        super(PropertySearchCriteria.class);

    }

    @Override
    public IsWidget createContent() {

        container = new VerticalPanel();
        container.setStyleName(DEFAULT_STYLE_PREFIX);
        Label label = new Label(i18n.tr("FIND AN APARTMENT"));
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchHeader.name());
        container.add(label);

        container.add(inject(proto().searchType()));

        get(proto().searchType()).addValueChangeHandler(new ValueChangeHandler<SearchType>() {
            @Override
            public void onValueChange(ValueChangeEvent<SearchType> event) {
                setSearchType(event.getValue());
            }
        });

        container.add(inject(proto().city()));
        ((CComboBox<City>) get(proto().city())).setNoSelectionText("Select City");

        container.add(inject(proto().location()));

        container.add(inject(proto().distance()));

        container.add(inject(proto().startingFrom()));

        HorizontalPanel bedsPanel = new HorizontalPanel();

        CComboBox<Integer> minBedsCombo = new CComboBox<Integer>();
        {
            minBedsCombo.setNoSelectionText("Min");
            minBedsCombo.setOptions(Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 }));
            bedsPanel.add(inject(proto().minBeds(), minBedsCombo));
        }

        CComboBox<Integer> maxBedsCombo = new CComboBox<Integer>();
        {
            maxBedsCombo.setNoSelectionText("Max");
            maxBedsCombo.setOptions(Arrays.asList(new Integer[] { 1, 2, 3, 4, 5 }));
            bedsPanel.add(inject(proto().maxBeds(), maxBedsCombo));
        }

        container.add(bedsPanel);

        HorizontalPanel bathsPanel = new HorizontalPanel();

        CComboBox<Integer> minBathsCombo = new CComboBox<Integer>();
        {
            minBathsCombo.setNoSelectionText("Min");
            minBathsCombo.setOptions(Arrays.asList(new Integer[] { 1, 2, 3 }));
            bathsPanel.add(inject(proto().minBath(), minBathsCombo));
        }

        CComboBox<Integer> maxBathsCombo = new CComboBox<Integer>();
        {
            maxBathsCombo.setNoSelectionText("Max");
            maxBathsCombo.setOptions(Arrays.asList(new Integer[] { 1, 2, 3 }));
            bathsPanel.add(inject(proto().maxBath(), maxBathsCombo));
        }

        container.add(bathsPanel);

        HorizontalPanel pricePanel = new HorizontalPanel();

        pricePanel.add(inject(proto().maxPrice()));

        pricePanel.add(inject(proto().minPrice()));

        container.add(pricePanel);

        HorizontalPanel amenities1Panel = new HorizontalPanel();

        amenities1Panel.add(inject(proto().elevator()));
        amenities1Panel.setCellWidth(get(proto().elevator()), "10%");
        get(proto().elevator()).asWidget().removeStyleName(CSSClass.pyx4j_CheckBox.name());
        Label elevatorLabel = new Label("Elevator");
        amenities1Panel.add(elevatorLabel);
        amenities1Panel.setCellWidth(elevatorLabel, "40%");

        amenities1Panel.add(inject(proto().fitness()));
        amenities1Panel.setCellWidth(get(proto().fitness()), "10%");
        get(proto().fitness()).asWidget().removeStyleName(CSSClass.pyx4j_CheckBox.name());
        Label fitnessLabel = new Label("Fitness");
        amenities1Panel.add(fitnessLabel);
        amenities1Panel.setCellWidth(fitnessLabel, "40%");

        container.add(amenities1Panel);

        HorizontalPanel amenities2Panel = new HorizontalPanel();

        amenities2Panel.add(inject(proto().parking()));
        amenities2Panel.setCellWidth(get(proto().parking()), "10%");
        get(proto().parking()).asWidget().removeStyleName(CSSClass.pyx4j_CheckBox.name());
        Label parkingLabel = new Label("Parking");
        amenities2Panel.add(parkingLabel);
        amenities2Panel.setCellWidth(parkingLabel, "40%");

        amenities2Panel.add(inject(proto().pool()));
        amenities2Panel.setCellWidth(get(proto().pool()), "10%");
        get(proto().pool()).asWidget().removeStyleName(CSSClass.pyx4j_CheckBox.name());
        Label poolLabel = new Label("Pool");
        amenities2Panel.add(poolLabel);
        amenities2Panel.setCellWidth(poolLabel, "40%");

        container.add(amenities2Panel);

        FlowPanel updatePanel = new FlowPanel();
        Button updateBtn = new Button(i18n.tr("Search"));
        updateBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.refineSearch();
            }

        });
        updatePanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ButtonPanel);
        updatePanel.add(updateBtn);

        container.add(updatePanel);

        return container;
    }

    private void setSearchType(SearchType searchType) {
        boolean searchByCity = SearchType.city.equals(searchType);
        get(proto().city()).setVisible(searchByCity);
        get(proto().location()).setVisible(!searchByCity);
        get(proto().distance()).setVisible(!searchByCity);
    }

    @Override
    public void populate(PropertySearchCriteria value) {
        super.populate(value);
        setSearchType(value.searchType().getValue());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        CEditableComponent<?, ?> c = super.create(member);
        if (member == proto().city()) {
            ((CEntityComboBox<City>) c).setOptionsDataSource(new EntityDataSource<City>() {

                @Override
                public void obtain(EntityQueryCriteria<City> criteria, AsyncCallback<List<City>> handlingCallback, boolean background) {
                    ((PortalSiteServices) GWT.create(PortalSiteServices.class)).retrieveCityList((AsyncCallback) handlingCallback);
                }

            });
        }
        return c;
    }

    public PropertyMapView.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(PropertyMapView.Presenter presenter) {
        this.presenter = presenter;
    }

}
