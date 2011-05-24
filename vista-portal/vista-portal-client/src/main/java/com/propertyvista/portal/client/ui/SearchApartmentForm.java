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
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class SearchApartmentForm extends CEntityForm<PropertySearchCriteria> implements SearchApartmentView {

    public static String DEFAULT_STYLE_PREFIX = "PortalViewSearchForm";

    private static I18n i18n = I18nFactory.getI18n(SearchApartmentViewImpl.class);

    private FlowPanel container;

    public static enum StyleSuffix implements IStyleSuffix {
        Row, RowHeader, Button, ButtonPanel, Element
    }

    private SearchApartmentView.Presenter presenter;

    public SearchApartmentForm() {
        super(PropertySearchCriteria.class);
    }

    @Override
    public IsWidget createContent() {
        container = new FlowPanel();
        container.setWidth("440px");
        container.setStyleName(DEFAULT_STYLE_PREFIX);

        addField(new CriteriaWidgetDecorator(inject(proto().province())));
        addField(new CriteriaWidgetDecorator(inject(proto().city())));

        addBreak();

        addField(new CriteriaWidgetDecorator(inject(proto().numOfBeds())));
        addField(new CriteriaWidgetDecorator(inject(proto().numOfBath())));
        addField(new CriteriaWidgetDecorator(inject(proto().price())));

        addBreak();

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
