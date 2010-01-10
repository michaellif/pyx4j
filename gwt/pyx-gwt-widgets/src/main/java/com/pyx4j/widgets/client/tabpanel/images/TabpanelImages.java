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
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TabpanelImages extends ClientBundle {

    @Source("closeTab.gif")
    ImageResource closeTab();

    @Source("closeTabFocused.gif")
    ImageResource closeTabFocused();

    @Source("tabbar-left.png")
    ImageResource moveTabbarLeft();

    @Source("tabbar-right.png")
    ImageResource moveTabbarRight();

    @Source("tabbar-empty.png")
    ImageResource moveTabbarEmpty();
}
