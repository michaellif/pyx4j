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

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.EntityDataSource;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.portal.client.ui.decorations.CriteriaWidgetDecorator;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;
import com.propertyvista.portal.rpc.portal.services.PortalSiteServices;

public class RefineApartmentSearchForm extends CEntityForm<PropertySearchCriteria> {

    public static String DEFAULT_STYLE_PREFIX = "RefineApartmentSearch";

    public static enum StyleSuffix implements IStyleSuffix {
        SearchHeader, RowHeader, ButtonPanel
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

        Label label = new Label(i18n.tr("LOCATION"));
        label.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.SearchHeader.name());
        container.add(label);

        addField(new CriteriaWidgetDecorator(inject(proto().city())));

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
                presenter.refineSearch();
            }

        });
        updatePanel.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ButtonPanel);
        updatePanel.add(updateBtn);

        container.add(updatePanel);
        return container;
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

    public PropertyMapView.Presenter getPresenter() {
        return presenter;
    }

    public void setPresenter(PropertyMapView.Presenter presenter) {
        this.presenter = presenter;
    }

}
