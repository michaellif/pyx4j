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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface FormImages extends ClientBundle {

    ImageResource triggerUp();

    ImageResource triggerDown();

    ImageResource triggerDisabled();

    // ----

    ImageResource triggerBlueUp();

    ImageResource triggerBlueOver();

    ImageResource triggerBlueDown();

    ImageResource triggerBlueDisabled();

    // ----

    ImageResource arrowLightBlueRight();

    ImageResource arrowLightBlueLeft();

    ImageResource arrowLightBlueRightDown();

    ImageResource arrowLightBlueLeftDown();

    ImageResource arrowLightGreyRight();

    ImageResource arrowLightGreyLeft();

    ImageResource arrowGreyRight();

    ImageResource arrowGreyLeft();

    // ----

    ImageResource formTooltipInfo();

    ImageResource formTooltipWarn();

    // ----

    ImageResource groupBoxOpen();

    ImageResource groupBoxClose();

    // ---

    ImageResource mandatory();

    // --- CForm tools

    @Source("arrow_up.png")
    ImageResource moveUp();

    @Source("arrow_down.png")
    ImageResource moveDown();

    @Source("cross.png")
    ImageResource deleteItem();

}
