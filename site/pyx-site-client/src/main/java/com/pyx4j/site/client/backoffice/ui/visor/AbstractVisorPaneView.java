/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Mar 14, 2013
 * @author michaellif
 */
package com.pyx4j.site.client.backoffice.ui.visor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.site.client.backoffice.ui.AbstractPaneView;
import com.pyx4j.site.client.backoffice.ui.IPaneView.IPanePresenter;
import com.pyx4j.site.client.backoffice.ui.PaneTheme;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;
import com.pyx4j.widgets.client.ImageFactory;

public abstract class AbstractVisorPaneView extends AbstractPaneView<IPanePresenter> implements IVisor {

    private IPrimePaneView<?> parentPane;

    private final Controller controller;

    private final LayoutPanel contentHolder;

    public AbstractVisorPaneView(final Controller controller) {
        super();
        this.controller = controller;

        contentHolder = new LayoutPanel();
        add(contentHolder);

        setStyleName(PaneTheme.StyleName.Visor.name());

        final Image closeImage = new Image(ImageFactory.getImages().closeTab());
        closeImage.addStyleName(PaneTheme.StyleName.VisorCloseButton.name());

        closeImage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                controller.hide();
            }
        });
        closeImage.addMouseOverHandler(new MouseOverHandler() {

            @Override
            public void onMouseOver(MouseOverEvent event) {
                closeImage.setResource(ImageFactory.getImages().closeTabFocused());
            }
        });
        closeImage.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                closeImage.setResource(ImageFactory.getImages().closeTab());
            }
        });

        closeImage.setTitle("Close");

        getHeaderCaption().add(closeImage);
    }

    protected IsWidget getContentPane() {
        if (contentHolder.getWidgetCount() == 0) {
            return null;
        }
        return contentHolder.getWidget(0);
    }

    protected void setContentPane(IsWidget widget) {
        contentHolder.add(widget);
    }

    @Override
    public Controller getController() {
        return controller;
    }

    public void setParentPane(IPrimePaneView<?> parentPane) {
        this.parentPane = parentPane;
    }

    public IPrimePaneView<?> getParentPane() {
        return parentPane;
    }

}
