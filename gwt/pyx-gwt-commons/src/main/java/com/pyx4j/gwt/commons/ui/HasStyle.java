/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 18, 2016
 * @author vlads
 */
package com.pyx4j.gwt.commons.ui;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Delegates Style access bytecode in VM emulation layer to different class.
 *
 * in code developer should use
 * <code>
 *   widget.getStyle().setProperty(...)
 * </code>
 *
 * instead of
 *
 * <code>
 *   widget.getStyle().setProperty(...)
 * </code>
 *
 */
public interface HasStyle extends IsWidget {
    // The introduction of this functionality adds 5K to portal JS code 5,371K -> 5,376K

    /**
     * Access to styles without breaking 'hot code replacement'
     */
    // If left unchanged this add 8K more to portal code (5,376K -> 5,384K ); There are optimization for this
    default Style getStyle() {
        return new Style(asWidget().getElement().getStyle());
    }

    // This is compiler optimization to be inserted in production mode by adding to build HasStyleOptimized.gwt.xml
//    default com.google.gwt.dom.client.Style getStyle() {
//        return asWidget().getElement().getStyle();
//    }

}
