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

import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.mvp.PlaceResolver;
import com.propertyvista.crm.client.ui.components.AnchorButton;

public class CrmTitleBar extends HorizontalPanel {

    public static String DEFAULT_STYLE_PREFIX = "vista_CrmTitleBar";

    public static enum StyleSuffix implements IStyleName {
        Caption, Breadcrumb
    }

    private final BreadcrumbsBar breadcrumbsBar;

    public CrmTitleBar() {
        setStyleName(getStylePrefix());
        getElement().getStyle().setProperty("clear", "both");
        setSize("100%", "36px");

        breadcrumbsBar = new BreadcrumbsBar();
        add(breadcrumbsBar);
        setCellVerticalAlignment(breadcrumbsBar, HorizontalPanel.ALIGN_MIDDLE);
        setCellWidth(breadcrumbsBar, "100%");
    }

    public void populate(Vector<IEntity> breadcrumbTrail, String caption) {
        breadcrumbsBar.populate(breadcrumbTrail, caption);
    }

    public void populate(String caption) {
        breadcrumbsBar.populate(null, caption);
    }

    protected String getStylePrefix() {
        return DEFAULT_STYLE_PREFIX;
    }

    public class BreadcrumbsBar implements IsWidget {

        HorizontalPanel widget;

        HorizontalPanel breadcrumbsPanel;

        public BreadcrumbsBar() {
            widget = new HorizontalPanel();
            widget.setStyleName(getStyleName() + StyleSuffix.Breadcrumb.name());
            breadcrumbsPanel = new HorizontalPanel();
            breadcrumbsPanel.setVerticalAlignment(ALIGN_MIDDLE);
            widget.add(breadcrumbsPanel);
        }

        public void populate(Vector<IEntity> breadcrumbTrail, String caption) {
            breadcrumbsPanel.clear();

            if (breadcrumbTrail != null) {
                for (IEntity breadcrumb : breadcrumbTrail) {
                    breadcrumbsPanel.add(new BreadcrumbAnchor(breadcrumb).asWidget());
                    breadcrumbsPanel.add(new BreadcrumbSeparator().asWidget());
                }
            }
            breadcrumbsPanel.add(new Caption(caption));
        }

        @Override
        public Widget asWidget() {
            return widget;
        }
    }

    public class Caption implements IsWidget {

        private final HTML captionWidget;

        public Caption(String caption) {
            captionWidget = new HTML(caption);
            captionWidget.setStyleName(getStylePrefix() + StyleSuffix.Caption.name());
        }

        @Override
        public Widget asWidget() {
            return captionWidget;
        }

    }

    public class BreadcrumbSeparator extends Caption implements IsWidget {

        public BreadcrumbSeparator() {
            super(">");
        }
    }

    public class BreadcrumbAnchor implements IsWidget {

        private final AnchorButton anchor;

        public BreadcrumbAnchor(final IEntity breadcrumb) {
            String label = !breadcrumb.getStringView().isEmpty() ? breadcrumb.getStringView() : breadcrumb.getEntityMeta().getCaption();
            anchor = new AnchorButton(label, new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    AppSite.getPlaceController().goTo(PlaceResolver.resolvePlace(breadcrumb));
                }
            });
            asWidget().setStyleName(getStyleName() + StyleSuffix.Breadcrumb.name());
        }

        @Override
        public Widget asWidget() {
            return anchor.asWidget();
        }
    }
}