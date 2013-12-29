/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 7, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;

public class NewPaymentMethodEditorTheme extends Theme {

    public static enum StyleName implements IStyleName {
        //@formatter:off
        PaymentEditor, 
        PaymentEditorButtons, 
        PaymentEditorImages, 
        
        PaymentEditorLegalTerms, 
        PaymentEditorLegalTermsHeader,
        PaymentEditorLegalTermsContent, 
        
        PaymentEditorForm
        //@formatter:on
    }

    public static enum StyleDependent implements IStyleDependent {
        item, selected
    }

    public NewPaymentMethodEditorTheme() {

        Style style = new Style(".", StyleName.PaymentEditor);
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorImages);
        style.addProperty("width", "50px");
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorImages, " div");
        style.addProperty("height", "30px");
//        style.addProperty("border-top-left-radius", "3px");
//        style.addProperty("border-bottom-left-radius", "3px");
//        style.addProperty("border-left", "1px solid #F7F7F7");
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorImages, " div.selected");
//        style.addProperty("padding-top", "4px");
//        style.addProperty("height", "26px");
//        style.addProperty("border-top", "1px solid #bbb");
//        style.addProperty("border-bottom", "1px solid #bbb");
//        style.addProperty("border-left", "1px solid #bbb");
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorImages, " div img");
        style.addProperty("height", "30px");
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorButtons);
        style.addProperty("width", "100px");
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorButtons, " .", DefaultWidgetsTheme.StyleName.RadioGroupItem);
        style.addProperty("width", "100%");
        style.addProperty("padding-top", "3px");
        style.addProperty("height", "30px");
//        style.addProperty("border-top", "1px solid #F7F7F7");
//        style.addProperty("border-bottom", "1px solid #F7F7F7");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorButtons, ".", DefaultWidgetsTheme.StyleName.RadioGroupItem, "-",
                DefaultWidgetsTheme.StyleDependent.active);
//        style.addProperty("border-top", "1px solid #bbb");
//        style.addProperty("border-bottom", "1px solid #bbb");
        style.addProperty("width", "130px");
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorForm);
        style.addProperty("margin", "10px");
        style.addProperty("margin-right", "0");

        style.addProperty("border-radius", "5px");
        style.addProperty("border", "solid 1px #666");

        style.addProperty("padding", "1em");
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorLegalTerms);
        style.addProperty("font-weight", "normal");

        style.addProperty("background-color", "white");
        style.addProperty("color", "black");

        style.addProperty("padding-left", "0.5em");
        style.addProperty("padding-right", "0.5em");

        style.addProperty("height", "30em");
        style.addProperty("width", "60em");

        style.addProperty("border", "solid 1px #666");

        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorLegalTermsHeader);
        style.addProperty("background-color", ThemeColor.background, 0.1);
        style.addProperty("color", ThemeColor.foreground, 0.8);

        style.addProperty("border-bottom", "solid 2px #666");

        style.addProperty("padding", "0.5em 1em");

        style.addProperty("font-size", "1.3em");
        style.addProperty("font-weight", "bold");

        style.addProperty("height", "1.5em");
        addStyle(style);

        style = new Style(".", StyleName.PaymentEditorLegalTermsContent);
        style.addProperty("height", "25em");
        style.addProperty("padding-top", "4px");
        style.addProperty("padding-bottom", "4px");
        style.addProperty("border-bottom", "solid 1px #666");
        addStyle(style);
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}
