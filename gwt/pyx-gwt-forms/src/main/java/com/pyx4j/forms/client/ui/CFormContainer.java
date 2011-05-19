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
 * Created on Apr 23, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;

public abstract class CFormContainer<DATA_TYPE, WIDGET_TYPE extends Widget & INativeEditableComponent<DATA_TYPE>> extends
        CEditableComponent<DATA_TYPE, WIDGET_TYPE> implements SelfManagedComponent {

    private ImageResource image;

    public void setTitleImage(ImageResource image) {
        this.image = image;
    }

    public ImageResource getTitleImage() {
        return image;
    }

}
