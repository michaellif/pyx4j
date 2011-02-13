/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Feb 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

public class FormsFolderDecorator extends FlowPanel implements FolderDecorator {

    private final Image image;

    private final SimplePanel content;

    public FormsFolderDecorator(ImageResource addButton) {
        image = new Image(addButton);
        content = new SimplePanel();
        add(content);

        add(image);
    }

    @Override
    public void setWidget(IsWidget w) {
        content.setWidget(w);
    }

    @Override
    public HandlerRegistration addRowAddClickHandler(ClickHandler handler) {
        return image.addClickHandler(handler);
    }

}
