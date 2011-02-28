/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.propertyvista.portal.client.ptapp.WizardStep;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;

import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class MainNavigViewImpl extends SimplePanel implements MainNavigView {

    public static String DEFAULT_STYLE_PREFIX = "vista_Steps";

    public static enum StyleSuffix implements IStyleSuffix {
        Holder, Tab, ArrowHolder, LabelHolder, StatusHolder, Label
    }

    public static enum StyleDependent implements IStyleDependent {
        hover, latest, complete, invalid, current
    }

    private MainNavigPresenter presenter;

    private NavigTabList tabsHolder;

    public MainNavigViewImpl() {
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    @Override
    public void setPresenter(MainNavigPresenter presenter) {
        this.presenter = presenter;

        clear();
        tabsHolder = new NavigTabList();

        List<NavigTab> tabs = new ArrayList<NavigTab>();

        boolean visited = false;
        for (int i = presenter.getWizardSteps().size() - 1; i >= 0; i--) {
            WizardStep step = presenter.getWizardSteps().get(i);
            if (ApplicationWizardStep.Status.latest.equals(step.getStatus())) {
                visited = true;
            }
            tabs.add(0, new NavigTab(step, visited));
        }

        for (NavigTab navigTab : tabs) {
            tabsHolder.add(navigTab);
        }

        setWidget(tabsHolder);

    }

    class NavigTabList extends ComplexPanel {
        public NavigTabList() {
            setElement(DOM.createElement("ul"));
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Holder.name());
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }
    }

    class NavigTab extends ComplexPanel {

        private final AppPlace place;

        private final SimplePanel arrowHolder;

        private final FlowPanel labelHolder;

        private final HTML statusHolder;

        private final Label label;

        public AppPlace getPlace() {
            return place;
        }

        NavigTab(final WizardStep step, boolean visited) {
            super();
            setElement(DOM.createElement("li"));
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Tab.name());

            getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            sinkEvents(Event.ONCLICK);

            arrowHolder = new SimplePanel();
            arrowHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ArrowHolder.name());
            add(arrowHolder);

            labelHolder = new FlowPanel();
            labelHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.LabelHolder.name());
            arrowHolder.add(labelHolder);

            statusHolder = new HTML("&nbsp;");
            statusHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.StatusHolder.name());
            add(statusHolder);

            label = new Label(presenter.getNavigLabel(step.getPlace()));
            label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Label.name());
            label.getElement().getStyle().setFontSize(15, Unit.PX);
            label.getElement().getStyle().setProperty("color", "#333");
            labelHolder.add(statusHolder);
            labelHolder.add(label);

            switch (step.getStatus()) {
            case invalid:
                addStyleDependentName(StyleDependent.invalid);
                break;
            case complete:
                addStyleDependentName(StyleDependent.complete);
                break;
            case latest:
                addStyleDependentName(StyleDependent.latest);
                break;
            default:
                break;
            }

            if (step.getPlace().equals(presenter.getWhere())) {
                label.addStyleDependentName(StyleDependent.current.name());
            }

            this.place = step.getPlace();
            getElement().getStyle().setFontWeight(FontWeight.BOLD);

            getElement().getStyle().setCursor(Cursor.DEFAULT);

            if (visited) {
                addDomHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.navigTo(place);
                    }
                }, ClickEvent.getType());
                addDomHandler(new MouseOverHandler() {
                    @Override
                    public void onMouseOver(MouseOverEvent event) {
                        label.getElement().getStyle().setProperty("color", "#555");
                    }
                }, MouseOverEvent.getType());
                addDomHandler(new MouseOutHandler() {
                    @Override
                    public void onMouseOut(MouseOutEvent event) {
                        label.getElement().getStyle().setProperty("color", "#333");
                    }
                }, MouseOutEvent.getType());
                getElement().getStyle().setCursor(Cursor.POINTER);
            } else {
                if (!GWT.isProdMode()) {
                    addDomHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            presenter.navigTo(place);
                        }
                    }, ClickEvent.getType());
                }
            }

        }

        public void addStyleDependentName(StyleDependent style) {
            super.addStyleDependentName(style.name());
            arrowHolder.addStyleDependentName(style.name());
            labelHolder.addStyleDependentName(style.name());
            statusHolder.addStyleDependentName(style.name());
        }

        @Override
        public void add(Widget w) {
            super.add(w, getElement());
        }
    }
}
