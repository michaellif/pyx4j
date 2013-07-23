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
 * @version $Id$
 */
package com.pyx4j.site.client.ui.visor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.site.client.ui.AbstractPane;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.widgets.client.ImageFactory;

public abstract class AbstractVisorPane extends AbstractPane implements IVisor {

    private IPane parentPane;

    private final Controller controller;

    public AbstractVisorPane(final Controller controller) {
        super();
        this.controller = controller;
        setStyleName(DefaultPaneTheme.StyleName.Visor.name());

        final Image closeImage = new Image(ImageFactory.getImages().closeTab());
        closeImage.addStyleName(DefaultPaneTheme.StyleName.VisorCloseButton.name());

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

    @Override
    public Controller getController() {
        return controller;
    }

    protected void setParentPane(IPane parentPane) {
        this.parentPane = parentPane;
    }

    protected IPane getParentPane() {
        return parentPane;
    }

}
