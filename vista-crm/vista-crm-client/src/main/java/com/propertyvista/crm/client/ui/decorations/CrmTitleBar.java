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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.mvp.PlaceResolver;
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

        // TODO remove caption???
        captionHolder = new HTML("", false);
        setStyleName(getStylePrefix());
        captionHolder.setStyleName(getStylePrefix() + StyleSuffix.Caption.name());
//        add(captionHolder);
//        setCellVerticalAlignment(captionHolder, HorizontalPanel.ALIGN_MIDDLE);

        getElement().getStyle().setProperty("clear", "both");
    }

    public void setCaption(String caption) {
        captionHolder.setText(caption);
    }

    public void populateBreadcrumbs(BreadcrumbTrailDTO breadcrumbTrail) {
        breadcrumbHolder.populate(breadcrumbTrail);
    }

    protected String getStylePrefix() {
        return DEFAULT_STYLE_PREFIX;
    }

    public static class BreadcrumbsBar implements IsWidget {

        HorizontalPanel widget;

        HorizontalPanel breadcrumbsPanel;

        public BreadcrumbsBar() {
            widget = new HorizontalPanel();
            widget.setSize("100%", "1em");
            widget.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Breadcrumb.name());

            breadcrumbsPanel = new HorizontalPanel();
            widget.add(breadcrumbsPanel);
        }

        public void populate(BreadcrumbTrailDTO breadcrumbTrail) {
            breadcrumbsPanel.clear();
            // TODO only if caption present
//            for (int i = 0; i < breadcrumbTrail.trail().size() - 1; ++i) {
//                BreadcrumbDTO breadcrumb = breadcrumbTrail.trail().get(i);
//                breadcrumbsPanel.add(new BreadcrumbEntityListAnchor(breadcrumb).asWidget());
//                breadcrumbsPanel.add(new BreadcrumbLink(breadcrumb).asWidget());
//            }
//            breadcrumbsPanel.add(new BreadcrumbEntityListAnchor(breadcrumbTrail.trail().get(breadcrumbTrail.trail().size() - 1)));
            for (BreadcrumbDTO breadcrumb : breadcrumbTrail.trail()) {
                breadcrumbsPanel.add(new BreadcrumbEntityListAnchor(breadcrumb).asWidget());
                breadcrumbsPanel.add(new BreadcrumbLink(breadcrumb).asWidget());
            }
        }

        @Override
        public Widget asWidget() {
            return widget;
        }
    }

    public static class BreadcrumbLink implements IsWidget {

        private final CHyperlink hyperlink;

        public BreadcrumbLink(final BreadcrumbDTO breadcrumb) {
            hyperlink = new CHyperlink(new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(PlaceResolver.resolvePlace(breadcrumb.entityClass().getValue(), breadcrumb.entityId().getValue()));
                }
            });
            hyperlink.setVisible(true);
            hyperlink.setViewable(true);
            hyperlink.populate(breadcrumb.label().getValue());

            asWidget().getElement().getStyle().setPaddingRight(0.5, Unit.EM);
        }

        @Override
        public Widget asWidget() {
            return hyperlink.asWidget();
        }
    }

    public static class BreadcrumbEntityListAnchor implements IsWidget {

        private final CHyperlink hyperlink;

        public BreadcrumbEntityListAnchor(final BreadcrumbDTO breadcrumb) {
            hyperlink = new CHyperlink(new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(PlaceResolver.resolvePlace(breadcrumb.entityClass().getValue()));
                }
            });

            hyperlink.populate(">");
            asWidget().getElement().getStyle().setPaddingRight(1., Unit.EM);
        }

        @Override
        public Widget asWidget() {
            return hyperlink.asWidget();
        }
    }
}