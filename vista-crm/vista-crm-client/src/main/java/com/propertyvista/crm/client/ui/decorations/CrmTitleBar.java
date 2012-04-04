/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-21
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.decorations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.mvp.PlaceResolver;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.domain.breadcrumbs.BreadcrumbDTO;
import com.propertyvista.domain.breadcrumbs.BreadcrumbTrailDTO;

public class CrmTitleBar extends HorizontalPanel {

    public static String DEFAULT_STYLE_PREFIX = "vista_CrmTitleBar";

    public static enum StyleSuffix implements IStyleName {
        Caption, Breadcrumb
    }

    private final HTML captionHolder;

    private final BreadcrumbsBar breadcrumbHolder;

    public CrmTitleBar() {
        breadcrumbHolder = new BreadcrumbsBar();
        add(breadcrumbHolder);
        setCellVerticalAlignment(breadcrumbHolder, HorizontalPanel.ALIGN_MIDDLE);
        setCellWidth(breadcrumbHolder, "100%");

        captionHolder = new HTML("", false);
        setStyleName(getStylePrefix());
        captionHolder.setStyleName(getStylePrefix() + StyleSuffix.Caption.name());
        add(captionHolder);
        setCellVerticalAlignment(captionHolder, HorizontalPanel.ALIGN_MIDDLE);
        setCellWidth(captionHolder, "100%");

        getElement().getStyle().setProperty("clear", "both");
    }

    public void setCaption(String caption) {
        captionHolder.setText(caption);
        captionHolder.asWidget().setVisible(true);
        breadcrumbHolder.asWidget().setVisible(false);
    }

    public void populateBreadcrumbs(BreadcrumbTrailDTO breadcrumbTrail) {
        breadcrumbHolder.populate(breadcrumbTrail);
        captionHolder.asWidget().setVisible(false);
        breadcrumbHolder.asWidget().setVisible(true);
    }

    protected String getStylePrefix() {
        return DEFAULT_STYLE_PREFIX;
    }

    public static class BreadcrumbsBar implements IsWidget {

        HorizontalPanel widget;

        HorizontalPanel breadcrumbsPanel;

        public BreadcrumbsBar() {
            widget = new HorizontalPanel();
            widget.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Breadcrumb.name());
            breadcrumbsPanel = new HorizontalPanel();
            breadcrumbsPanel.setVerticalAlignment(ALIGN_MIDDLE);
            widget.add(breadcrumbsPanel);
        }

        public void populate(BreadcrumbTrailDTO breadcrumbTrail) {
            breadcrumbsPanel.clear();

            int i = 0;
            for (; i < breadcrumbTrail.trail().size() - 1; ++i) {
                BreadcrumbDTO breadcrumb = breadcrumbTrail.trail().get(i);
                breadcrumbsPanel.add(new BreadcrumbAnchor(breadcrumb).asWidget());
                breadcrumbsPanel.add(new BreadcrumbSepearator().asWidget());
            }
            breadcrumbsPanel.add(new Caption(breadcrumbTrail.trail().get(i).label().getValue()));
        }

        @Override
        public Widget asWidget() {
            return widget;
        }
    }

    public static class Caption implements IsWidget {

        private final HTML captionWidget;

        public Caption(String caption) {
            captionWidget = new HTML(caption);
            captionWidget.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Caption.name());
        }

        @Override
        public Widget asWidget() {
            return captionWidget;
        }

    }

    public static class BreadcrumbSepearator extends Caption implements IsWidget {

        public BreadcrumbSepearator() {
            super(">");
        }
    }

    public static class BreadcrumbAnchor implements IsWidget {

        private final AnchorButton anchor;

        public BreadcrumbAnchor(final BreadcrumbDTO breadcrumb) {
            anchor = new AnchorButton(breadcrumb.label().getValue(), new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    AppSite.getPlaceController().goTo(PlaceResolver.resolvePlace(breadcrumb.entityClass().getValue(), breadcrumb.entityId().getValue()));
                }
            });
            asWidget().setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Breadcrumb.name());
            //asWidget().getElement().getStyle().setPaddingRight(0.5, Unit.EM);
        }

        @Override
        public Widget asWidget() {
            return anchor.asWidget();
        }
    }
}