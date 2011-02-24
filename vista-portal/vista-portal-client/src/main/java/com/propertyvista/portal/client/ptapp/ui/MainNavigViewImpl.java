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
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.propertyvista.portal.client.ptapp.WizardStep;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;

import com.pyx4j.site.rpc.AppPlace;

public class MainNavigViewImpl extends FlowPanel implements MainNavigView {

    private Presenter presenter;

    public MainNavigViewImpl() {
        setHeight("43px");
        setWidth("100%");
    }

    class NavigTab extends FlowPanel {

        private final AppPlace place;

        public AppPlace getPlace() {
            return place;
        }

        NavigTab(final WizardStep step, boolean visited) {
            super();
            getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);

            Anchor anchor = new Anchor(presenter.getNavigLabel(step.getPlace()));
            anchor.getElement().getStyle().setProperty("textDecoration", "none");
            anchor.getElement().getStyle().setPaddingLeft(7, Unit.PX);
            anchor.getElement().getStyle().setFontSize(15, Unit.PX);
            anchor.getElement().getStyle().setProperty("color", "#333");
            anchor.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
            add(anchor);

            switch (step.getStatus()) {
            case hasAlert:
                Image image = new Image(SiteImages.INSTANCE.exclamation());
                image.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
                add(image);
                break;
            case complete:
                image = new Image(SiteImages.INSTANCE.check());
                image.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
                add(image);
                break;
            case current:
                image = new Image(SiteImages.INSTANCE.stepPointer());
                image.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
                add(image);
                break;

            default:
                break;
            }

            this.place = step.getPlace();
            setWidth("115px");
            getElement().getStyle().setFontWeight(FontWeight.BOLD);

            getElement().getStyle().setProperty("lineHeight", "43px");
            getElement().getStyle().setProperty("textAlign", "center");

            if (visited) {
                getElement().getStyle().setBackgroundColor("#999999");
            }

            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigTo(place);
                }
            });
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

        clear();

        List<NavigTab> tabs = new ArrayList<NavigTab>();

        boolean visited = false;
        for (int i = presenter.getWizardSteps().size() - 1; i >= 0; i--) {
            WizardStep step = presenter.getWizardSteps().get(i);
            if (ApplicationWizardStep.Status.current.equals(step.getStatus())) {
                visited = true;
            }
            tabs.add(0, new NavigTab(step, visited));
        }

        for (NavigTab navigTab : tabs) {
            add(navigTab);
        }

    }
}
