/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.searchapt;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.client.ui.maps.PropertyMapWidget;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;

public class ApartmentDetailsViewImpl extends FlowPanel implements ApartmentDetailsView {

    public static String DEFAULT_STYLE_PREFIX = "ApartmentDetails";

    public static enum StyleSuffix implements IStyleSuffix {
        Left, Center, PageHeader, Button
    }

    private Presenter presenter;

    private final FlowPanel leftPanel;

    private final PropertyMapWidget map;

    private final FlowPanel centerPanel;

    private final SlidesPanel slidesPanel;

    private final ApartmentDetailsForm apartmentForm;

    private static I18n i18n = I18nFactory.getI18n(ApartmentDetailsViewImpl.class);

    public ApartmentDetailsViewImpl() {
        apartmentForm = new ApartmentDetailsForm();
        apartmentForm.initialize();

        setSize("100%", "100%");
        setStyleName(DEFAULT_STYLE_PREFIX);
        VerticalPanel header = new VerticalPanel();
        header.setWidth("100%");

        HTML pagetitle = new HTML(i18n.tr("Apartment Details"));
        pagetitle.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.PageHeader);
        header.add(pagetitle);
        add(header);

        leftPanel = new FlowPanel();
        leftPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Left);
        leftPanel.getElement().getStyle().setFloat(Float.LEFT);
        leftPanel.setWidth("35%");
        add(leftPanel);

        slidesPanel = new SlidesPanel();
        leftPanel.add(slidesPanel);

        map = new PropertyMapWidget();
        leftPanel.add(map);

        Button inquire = new Button(i18n.tr("Inquire"));
        inquire.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Button);
        inquire.setWidth("300px");
        inquire.setHeight("40px");
        leftPanel.add(inquire);

        centerPanel = new FlowPanel();
        centerPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Center);
        centerPanel.getElement().getStyle().setFloat(Float.RIGHT);
        centerPanel.setWidth("63%");

        add(centerPanel);

        centerPanel.add(apartmentForm);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        apartmentForm.setPresenter(presenter);
    }

    @Override
    public void populate(PropertyDetailsDTO property) {
        apartmentForm.populate(property);
        map.populate(property);
        slidesPanel.populate(property);
    }

}
