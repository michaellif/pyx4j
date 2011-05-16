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
 * Created on 2011-05-15
 * @author leon
 * @version $Id$
 */
package com.pyx4j.widgets.client.datepicker;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.IDebugId;

public class ImageWithDebugId extends Image implements IDebugId {

    public enum MonthSelectorButtonsId {
        MonthSelectorButton_ForwardYear, MonthSelectorButton_BackwardsYear, MonthSelectorButton_BackwardsMonth, MonthSelectorButton_ForwardMonth
    }

    String debugId = "";

    public ImageWithDebugId(ImageResource resource, MonthSelectorButtonsId id) {
        super(resource);
        debugId = id.toString();
        this.ensureDebugId(debugId);
    }

    @Override
    public String debugId() {
        return debugId;
    }
}
