/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 11, 2013
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.shared.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

public class CommunicationCenterTheme extends Theme {

    public static enum StyleName implements IStyleName {//@formatter:off
        CommunicationTableButton,
        CommunicationTableBorder,
        NewMessagePanelBorder,
        NewMessageControlPanelBorder,
        CommunicationTableHeaderRowBg,
        CommunicationTableFirstRowBg,
        CommunicationTableSecondRowBg,
        CommunicationTableChechBox,
        CommunicationTableVerticalBorder,
        CommunicationTableHeaderVB,
        CommunicationTableFirstButtonBG,
        CommunicationTableSecondButtonBG;
    }

    public CommunicationCenterTheme() {
        Style style = new Style(".vista-pmsite-residentPage .", StyleName.CommunicationTableButton);
        style.addProperty("padding", "2px");
        style.addProperty("border", "solid");
        addStyle(style);

        style = new Style(".vista-pmsite-residentPage .", StyleName.CommunicationTableFirstButtonBG);
        style.addProperty("padding", "2px");
        style.addProperty("border", "solid");
        style.addProperty("border-color","#EEEEEE");
        addStyle(style);
        
        style = new Style(".vista-pmsite-residentPage .", StyleName.CommunicationTableSecondButtonBG);
        style.addProperty("padding", "2px");
        style.addProperty("border", "solid");
        style.addProperty("border-color","#BBBBBB");
        addStyle(style);
        
        style = new Style(".", StyleName.CommunicationTableBorder);
        style.addProperty("border-left", "1px solid black");
        style.addProperty("border-right", "1px solid black");
        style.addProperty("border-top", "1px solid black");
        style.addProperty("border-bottom", "1px solid black");
        addStyle(style);

        style = new Style(".", StyleName.CommunicationTableVerticalBorder);
        style.addProperty("border-left", "1px solid black");
        style.addProperty("border-right", "1px solid black");
        addStyle(style);

        style = new Style(".", StyleName.CommunicationTableHeaderVB);
        style.addProperty("border-left", "1px solid black");
        addStyle(style);

        style = new Style(".", StyleName.CommunicationTableHeaderRowBg);
        style.addProperty("background-color", "grey");
        style.addProperty("font-weight","bold");
        style.addProperty("font-size","15px");
        addStyle(style);

        style = new Style(".", StyleName.CommunicationTableChechBox);
        style.addProperty("text-align", "center");
        style.addProperty("border-left","1px solid black");
        addStyle(style);

        style = new Style(".", StyleName.CommunicationTableFirstRowBg);
        style.addProperty("background-color", "#EEEEEE");
        addStyle(style);

        style = new Style(".", StyleName.CommunicationTableSecondRowBg);
        style.addProperty("background-color", "#BBBBBB");
        addStyle(style);

        style = new Style(".", StyleName.NewMessagePanelBorder);
        style.addProperty("border-left", "1px solid black");
        style.addProperty("border-right", "1px solid black");
        style.addProperty("border-top", "1px solid black");
        addStyle(style);

        style = new Style(".", StyleName.NewMessageControlPanelBorder);
        style.addProperty("border-top", "1px solid black");
        style.addProperty("border-left", "1px solid black");
        style.addProperty("border-right", "1px solid black");
        style.addProperty("border-bottom", "1px solid black");
        addStyle(style);
    }
    
    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}