/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2011
 * @author Dad
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

public class SearchApartmentForm extends CEntityForm<PropertySearchCriteria> implements SearchApartmentView {

    public static String DEFAULT_STYLE_PREFIX = "PortalViewSearchForm";

    private static I18n i18n = I18nFactory.getI18n(SearchApartmentViewImpl.class);

    public static enum StyleSuffix implements IStyleSuffix {
        Row, RowHeader, Button, ButtonPanel, Element
    }

    private SearchApartmentView.Presenter presenter;

    public SearchApartmentForm() {
        super(PropertySearchCriteria.class);
    }

    @Override
    public IsWidget createContent() {
        VistaDecoratorsFlowPanel container = new VistaDecoratorsFlowPanel();
        container.setWidth("440px");
        container.setStyleName(DEFAULT_STYLE_PREFIX);

        FlowPanel row1 = new FlowPanel();
        row1.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Row);

        FlowPanel field1 = new FlowPanel();
        field1.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        //     ListBox province = new ListBox(false);
        CComboBox<String> provinceCombo = new CComboBox<String>();
        //   province.setName("province");
        provinceCombo.asWidget().setName("province");
        provinceCombo.setWidth("100%");
        HTML label = new HTML("<label for=\"province\">" + i18n.tr(proto().province().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field1.add(label);
        // field1.add(province);
        field1.add(inject(proto().province(), provinceCombo));
        row1.add(DecorationUtils.inline(field1, "210px"));

        FlowPanel field2 = new FlowPanel();
        field2.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        CComboBox<String> cityCombo = new CComboBox<String>();
        // ListBox city = new ListBox(false);
        //cityCombo.setName("province");
        cityCombo.asWidget().setName("city");
        cityCombo.setWidth("100%");
        label = new HTML("<label for=\"city\">" + i18n.tr(proto().city().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field2.add(label);
        field2.add(inject(proto().city(), cityCombo));
        row1.add(DecorationUtils.inline(field2, "210px"));
        container.add(row1);

        FlowPanel row2 = new FlowPanel();
        row2.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Row);

        FlowPanel field3 = new FlowPanel();
        field3.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        // ListBox mprice = new ListBox(false);
        CComboBox<Double> minPriceCombo = new CComboBox<Double>();
        minPriceCombo.asWidget().setName("minp");
        minPriceCombo.setWidth("100%");
        label = new HTML("<label for=\"minp\">" + i18n.tr(proto().minPrice().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field3.add(label);
        field3.add(inject(proto().minPrice(), minPriceCombo));
        row2.add(DecorationUtils.inline(field3, "100px"));

        FlowPanel field4 = new FlowPanel();
        field4.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        // ListBox maxprice = new ListBox(false);
        CComboBox<Double> maxPriceCombo = new CComboBox<Double>();
        maxPriceCombo.asWidget().setName("minp");
        maxPriceCombo.setWidth("100%");
        label = new HTML("<label for=\"minp\">" + i18n.tr(proto().maxPrice().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field4.add(label);
        field4.add(inject(proto().maxPrice(), maxPriceCombo));
        row2.add(DecorationUtils.inline(field4, "100px"));

        FlowPanel field5 = new FlowPanel();
        field5.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        //   ListBox beds = new ListBox(false);
        CComboBox<Integer> bedsCombo = new CComboBox<Integer>();
        bedsCombo.asWidget().setName("beds");
        bedsCombo.setWidth("100%");
        label = new HTML("<label for=\"beds\">" + i18n.tr(proto().numOfBeds().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field5.add(label);
        field5.add(inject(proto().numOfBeds(), bedsCombo));
        row2.add(DecorationUtils.inline(field5, "100px", "left"));

        FlowPanel field6 = new FlowPanel();
        field6.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Element);
        //  ListBox bath = new ListBox(false);
        CComboBox<Integer> bathCombo = new CComboBox<Integer>();
        bathCombo.asWidget().setName("baths");
        bathCombo.setWidth("100%");
        label = new HTML("<label for=\"baths\">" + i18n.tr(proto().numOfBath().getMeta().getCaption()) + "</label>");
        label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.RowHeader);
        field6.add(label);
        field6.add(inject(proto().numOfBath(), bathCombo));
        row2.add(DecorationUtils.inline(field6, "100px"));
        container.add(row2);

        FlowPanel search = new FlowPanel();

        Button searchBtn = new Button(i18n.tr("Search"));
        searchBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.goToPropertyMap((PropertySearchCriteria) null);
            }

        });
        searchBtn.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Button);
        search.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ButtonPanel);
        search.add(searchBtn);
        container.add(search);

        return container;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

    public Presenter getPresenter() {
        return presenter;

    }
}
