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
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

public enum CSSClass {
    pyx4j_Toolbar,

    pyx4j_StatusBar,

    pyx4j_BarSeparator,

    //Section Panel
    pyx4j_Section, pyx4j_Section_Border, pyx4j_Section_SelectionBorder, pyx4j_Section_Background, pyx4j_Section_Content, pyx4j_Section_ContentBorder, pyx4j_Section_header2Holder,

    //Folder Panel
    pyx4j_Folder,

    //Picker
    pyx4j_Picker, pyx4j_PickerPanel, pyx4j_PickerLine, pyx4j_PickerLine_Selected,

    //Button
    pyx4j_Button, pyx4j_ButtonContainer, pyx4j_ButtonContent, pyx4j_ButtonText, pyx4j_ButtonImage,

    // Style for GWT button active { The one that has Enter focus in dialog}
    gwtButtonDefault,

    //Tooltip
    pyx4j_Tooltip, pyx4j_Tooltip_Shadow,

    //Dialog
    pyx4j_Dialog, pyx4j_Dialog_Caption, pyx4j_Dialog_Resizer, pyx4j_Dialog_Content,

    pyx4j_GlassPanel_Transparent, pyx4j_GlassPanel_SemiTransparent, pyx4j_GlassPanel_Transparent_Label, pyx4j_GlassPanel_SemiTransparent_Label,

    //CheckBox
    pyx4j_CheckBox,

    //TextBox
    pyx4j_TextBox,

    //Hyperlink
    pyx4j_Hyperlink,

    //GroupBox
    pyx4j_GroupBox, pyx4j_GroupBox_Caption,

    //Photoalbom
    pyx4j_Photoalbom_Thumbnail, pyx4j_SlideshowPopup, pyx4j_SlideshowAction, pyx4j_Photoalbom_Caption,

    //Banner
    pyx4j_Banner,

}
