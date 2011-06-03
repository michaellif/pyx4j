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
package com.propertyvista.portal.client.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.client.ui.maps.PropertyMapWidget;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class ApartmentDetailsViewImpl extends SimplePanel implements ApartmentDetailsView {

    public static String DEFAULT_STYLE_PREFIX = "AppartmentDetails";

    public static enum StyleSuffix implements IStyleSuffix {
        Left, Center, PageHeader, DetailsButton
    }

    private Presenter presenter;

    private final PageLayout container;

    private final ApartmentDetailsForm apartmentForm;

    private static I18n i18n = I18nFactory.getI18n(ApartmentDetailsViewImpl.class);

    public ApartmentDetailsViewImpl() {
        container = new PageLayout();
        apartmentForm = new ApartmentDetailsForm();
        apartmentForm.initialize();
        container.addToCenterPanel(apartmentForm);
        setWidget(container);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        apartmentForm.setPresenter(presenter);
    }

    @Override
    public void populate(PropertyDetailsDTO property) {
        apartmentForm.populate(property);
        container.setMap(property);
    }

    private class PageLayout extends FlowPanel {
        private final FlowPanel leftPanel;

        private final PropertyMapWidget map;

        private final FlowPanel centerPanel;

        public PageLayout() {
            super();
            setSize("100%", "100%");
            setStyleName(DEFAULT_STYLE_PREFIX);
            VerticalPanel header = new VerticalPanel();
            header.setWidth("100%");
            Anchor back = new Anchor("Back to Search");
            back.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(new PortalSiteMap.FindApartment.PropertyMap());
                }
            });
            header.add(back);

            HTML pagetitle = new HTML("<span>" + i18n.tr("Apartment Details") + "</span>");
            pagetitle.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.PageHeader);
            header.add(pagetitle);
            add(header);

            leftPanel = new FlowPanel();
            leftPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Left);
            leftPanel.getElement().getStyle().setFloat(Float.LEFT);
            leftPanel.setWidth("35%");
            add(leftPanel);

            map = new PropertyMapWidget();
            leftPanel.add(map);

            centerPanel = new FlowPanel();
            centerPanel.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Center);
            centerPanel.getElement().getStyle().setFloat(Float.RIGHT);
            centerPanel.setWidth("63%");

            add(centerPanel);
        }

        public void addToLeftPanel(IsWidget child) {
            leftPanel.add(child);
        }

        public void addToCenterPanel(IsWidget child) {
            centerPanel.add(child);
        }

        public void clearCenterPanel() {
            centerPanel.clear();
        }

        public void setMap(PropertyDetailsDTO property) {
            map.populate(property);
        }
    }

}
