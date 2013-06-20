/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.field.client.ui.components.header;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent.ChangeType;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.field.client.event.ListerNavigateEvent;
import com.propertyvista.field.client.event.NavigateAction;
import com.propertyvista.field.client.resources.FieldImages;
import com.propertyvista.field.client.ui.components.sort.SortDropDownPanel;
import com.propertyvista.field.rpc.FieldSiteMap;

public class ToolbarViewImpl extends VerticalPanel implements ToolbarView {

    private static final I18n i18n = I18n.get(ToolbarViewImpl.class);

    private final HorizontalPanel mainToolbar;

    private final HorizontalPanel navigDetailsToolbar;

    private Image contextMenuImage;

    private Image menuImage;

    public ToolbarViewImpl() {
        setSize("100%", "100%");
        getElement().getStyle().setBackgroundColor("#bca");

        mainToolbar = createMainToolbar();
        navigDetailsToolbar = createNavigDetailsToolbar();

        add(mainToolbar);
        add(navigDetailsToolbar);

        showNavigationDetails(false);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    private void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            menuImage.setVisible(true);
            break;

        default:
            menuImage.setVisible(false);
            break;
        }
    }

    private HorizontalPanel createMainToolbar() {
        HorizontalPanel container = new HorizontalPanel();
        container.setSize("100%", "100%");
        container.setVerticalAlignment(ALIGN_MIDDLE);

        final Toolbar leftActionsContainer = new Toolbar();
        final Toolbar rightActionsContainer = new Toolbar();

        container.add(leftActionsContainer);
        container.add(rightActionsContainer);
        container.setCellHorizontalAlignment(leftActionsContainer, ALIGN_LEFT);
        container.setCellHorizontalAlignment(rightActionsContainer, ALIGN_RIGHT);

        final SortDropDownPanel sortPanel = new SortDropDownPanel();

        menuImage = new Image(FieldImages.INSTANCE.menu());
        menuImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.getEventBus().fireEvent(new LayoutChangeRerquestEvent(ChangeType.toggleSideMenu));
            }
        });

        final Image sortImage = new Image(FieldImages.INSTANCE.sort());
        sortImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (sortPanel.isShowing()) {
                    sortPanel.hide();
                } else {
                    sortPanel.showRelativeTo(leftActionsContainer);
                }
            }
        });

        contextMenuImage = new Image(FieldImages.INSTANCE.contextMenu());
        final Image searchImage = createSearchImage();

        leftActionsContainer.add(menuImage);
        leftActionsContainer.add(sortImage);
        rightActionsContainer.add(contextMenuImage);
        rightActionsContainer.add(searchImage);

        return container;
    }

    private HorizontalPanel createNavigDetailsToolbar() {
        HorizontalPanel container = new HorizontalPanel();
        container.setSize("100%", "100%");
        container.setVerticalAlignment(ALIGN_MIDDLE);

        final Toolbar leftActionsContainer = new Toolbar();
        final Toolbar rightActionsContainer = new Toolbar();

        container.add(leftActionsContainer);
        container.add(rightActionsContainer);
        container.setCellHorizontalAlignment(leftActionsContainer, ALIGN_LEFT);
        container.setCellHorizontalAlignment(rightActionsContainer, ALIGN_RIGHT);

        final Image backImage = new Image(FieldImages.INSTANCE.back());
        backImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                showNavigationDetails(false);
                AppSite.getEventBus().fireEvent(new ListerNavigateEvent(NavigateAction.Back));
            }
        });

        final Image previousImage = new Image(FieldImages.INSTANCE.previous());
        previousImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.getEventBus().fireEvent(new ListerNavigateEvent(NavigateAction.PreviousItem));
            }
        });

        final Image nextImage = new Image(FieldImages.INSTANCE.next());
        nextImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.getEventBus().fireEvent(new ListerNavigateEvent(NavigateAction.NextItem));
            }
        });

        final Image searchImage = createSearchImage();
        final Image contextMenuImage = new Image(FieldImages.INSTANCE.contextMenu());

        leftActionsContainer.add(backImage);
        leftActionsContainer.add(previousImage);
        leftActionsContainer.add(nextImage);
        rightActionsContainer.add(contextMenuImage);
        rightActionsContainer.add(searchImage);

        return container;
    }

    private Image createSearchImage() {
        final Image searchImage = new Image(FieldImages.INSTANCE.search());
        searchImage.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AppSite.getPlaceController().goTo(new FieldSiteMap.Search());
            }
        });
        return searchImage;
    }

    @Override
    public void showNavigationDetails(boolean isVisible) {
        navigDetailsToolbar.setVisible(isVisible);
        mainToolbar.setVisible(!isVisible);
    }

}
