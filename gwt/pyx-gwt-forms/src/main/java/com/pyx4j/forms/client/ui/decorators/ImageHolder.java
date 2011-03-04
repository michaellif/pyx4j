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
 * Created on Feb 10, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.decorators;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

//Non-collapsible on hide image holder
public class ImageHolder extends FlowPanel {

    private final Image image;

    private final HTML spaceHolder;

    public ImageHolder(Image image) {
        this.image = image;
        add(image);
        spaceHolder = new HTML("&nbsp;");
        spaceHolder.setVisible(false);
        spaceHolder.setWidth(image.getWidth() + "px");
        add(spaceHolder);
    }

    @Override
    public void setVisible(boolean visible) {
        spaceHolder.setVisible(!visible);
        image.setVisible(visible);
    }
}