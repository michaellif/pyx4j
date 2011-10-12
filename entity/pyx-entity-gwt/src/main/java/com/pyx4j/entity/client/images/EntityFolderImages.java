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
package com.pyx4j.entity.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.widgets.client.images.WidgetsImages;

public interface EntityFolderImages extends WidgetsImages {

    EntityFolderImages INSTANCE = GWT.create(EntityFolderImages.class);

    @Source("arrow_up.png")
    ImageResource moveUp();

    @Source("arrow_up_hover.png")
    ImageResource moveUpHover();

    @Source("arrow_down.png")
    ImageResource moveDown();

    @Source("arrow_down_hover.png")
    ImageResource moveDownHover();

    ImageResource warn();
}
