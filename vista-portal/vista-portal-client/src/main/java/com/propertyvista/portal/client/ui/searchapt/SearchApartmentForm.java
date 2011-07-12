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
package com.propertyvista.portal.client.ui.searchapt;

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.EntityDataSource;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.portal.client.ui.decorations.CriteriaWidgetDecorator;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;

public class SearchApartmentForm extends CEntityForm<PropertySearchCriteria> implements SearchApartmentView {

    public static String DEFAULT_STYLE_PREFIX = "SearchApartment";

    private static I18n i18n = I18nFactory.getI18n(SearchApartmentViewImpl.class);

    private FlowPanel container;

    private SearchApartmentView.Presenter presenter;

    public SearchApartmentForm() {
        super(PropertySearchCriteria.class);
    }

    @Override
    public IsWidget createContent() {
        container = new FlowPanel();
        container.setWidth("600px");
        container.setStyleName(DEFAULT_STYLE_PREFIX);

        addField(new CriteriaWidgetDecorator(inject(proto().city())));
        addField(new CriteriaWidgetDecorator(inject(proto().minPrice())));

        addBreak();

        addField(new CriteriaWidgetDecorator(inject(proto().minBeds())));
        addField(new CriteriaWidgetDecorator(inject(proto().minBath())));

        addBreak();

        FlowPanel search = new FlowPanel();

        Button searchBtn = new Button(i18n.tr("Search"));
        searchBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.search();
            }

        });
        search.add(searchBtn);
        container.add(search);

        return container;
    }

    @Override
    public void initialize() {
        super.initialize();
        populate(EntityFactory.create(PropertySearchCriteria.class));
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

    private void addField(Widget widget) {
        widget.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        container.add(widget);
    }

    private void addBreak() {
        HTML breack = new HTML("&nbsp;");
        breack.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.NONE);
        breack.getElement().getStyle().setProperty("clear", "both");
        container.add(breack);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public Presenter getPresenter() {
        return presenter;

    }

}
