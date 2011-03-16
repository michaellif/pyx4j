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
 * Created on Mar 5, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.FormNavigationDebugId;
import com.pyx4j.gwt.commons.HandlerRegistrationGC;

public abstract class BaseFolderDecorator<E extends IEntity> extends FlowPanel implements FolderDecorator<E> {

    protected SimplePanel content;

    protected Image image;

    protected final ImageResource imageResourceRegular;

    protected final ImageResource imageResourceHover;

    protected FlowPanel imageHolder;

    protected Label addButtonLabel;

    protected boolean addable;

    public BaseFolderDecorator(ImageResource addButton, String title, boolean addable) {
        this(addButton, null, title, addable);
    }

    public BaseFolderDecorator(ImageResource addButton, ImageResource addButtonHover, String title, boolean addable) {
        this.addable = addable && (addButton != null);

        imageResourceRegular = addButton;
        imageResourceHover = addButtonHover;

        if (addButton != null) {
            image = new Image(addButton);
            image.getElement().getStyle().setCursor(Cursor.POINTER);
            image.getElement().getStyle().setFloat(Float.LEFT);
            image.addMouseOverHandler(new MouseOverHandler() {

                @Override
                public void onMouseOver(MouseOverEvent event) {
                    setHoverImage();
                }
            });
            image.addMouseOutHandler(new MouseOutHandler() {

                @Override
                public void onMouseOut(MouseOutEvent event) {
                    setRegularImage();
                }
            });

            imageHolder = new FlowPanel();
            imageHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            imageHolder.getElement().getStyle().setPaddingLeft(image.getWidth(), Unit.PX);
            imageHolder.add(image);

            addButtonLabel = new Label(title);
            addButtonLabel.getElement().getStyle().setPaddingLeft(3, Unit.PX);
            addButtonLabel.getElement().getStyle().setFloat(Float.LEFT);
            imageHolder.add(addButtonLabel);

        } else {
            image = null;
            addButtonLabel = null;
        }

        content = new SimplePanel();

    }

    @Override
    public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
        if (!addable) {
            return null;
        }

        HandlerRegistrationGC h = new HandlerRegistrationGC();
        h.add(image.addClickHandler(handler));
        h.add(addButtonLabel.addClickHandler(handler));
        return h;
    }

    @Override
    public void setFolder(CEntityFolder<?> w) {
        content.setWidget(w.getContent());
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        //TODO use inheritance of objects
        //image.ensureDebugId(CompositeDebugId.debugId(parentFolder.getDebugId(), FormNavigationDebugId.Form_Add));
        if (addable) {
            image.ensureDebugId(baseID + "_" + FormNavigationDebugId.Form_Add.getDebugIdString());
            addButtonLabel.ensureDebugId(baseID + "_" + FormNavigationDebugId.Form_Add.getDebugIdString() + "_label");
        }
    }

    protected void setRegularImage() {
        if (imageResourceRegular != null) {
            image.setResource(imageResourceRegular);
        }
    }

    protected void setHoverImage() {
        if (imageResourceHover != null) {
            image.setResource(imageResourceHover);
        }
    }
}
