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
 * Created on Jan 6, 2010
 * @author Michael
 */
package com.pyx4j.commons.css;

/**
 * Each component will have its own Theme, M.L. works on this
 * 
 */
@Deprecated
public enum CSSClass {

    pyx4j_Toolbar,

    pyx4j_StatusBar,

    // Style for GWT button active { The one that has Enter focus in dialog}
    gwtButtonDefault,

    //Tooltip
    pyx4j_Tooltip, pyx4j_Tooltip_Shadow,

    //Photoalbom
    pyx4j_Photoalbom_Thumbnail, pyx4j_SlideshowPopup, pyx4j_Photoalbom_Caption,

}
