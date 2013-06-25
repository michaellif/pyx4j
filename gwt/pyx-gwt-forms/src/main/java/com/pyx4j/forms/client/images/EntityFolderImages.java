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
 * Created on Dec 26, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

import com.pyx4j.widgets.client.images.IconButtonImages;
import com.pyx4j.widgets.client.images.WidgetsImages;

public interface EntityFolderImages extends WidgetsImages, IconButtonImages {

    EntityFolderImages INSTANCE = GWT.create(EntityFolderImages.class);

    MoveUpIconButtonImages moveUpButton();

    MoveDownIconButtonImages moveDownButton();

    public interface MoveUpIconButtonImages extends IconButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("arrow_up.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("arrow_up_hover.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("arrow_up.png")
        ImageResource active();
    }

    public interface MoveDownIconButtonImages extends IconButtonImages {
        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("arrow_down.png")
        ImageResource regular();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("arrow_down_hover.png")
        ImageResource hover();

        @Override
        @ImageOptions(repeatStyle = RepeatStyle.Both)
        @Source("arrow_down.png")
        ImageResource active();
    }
}
