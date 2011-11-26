/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.themes;

import java.util.List;

import com.pyx4j.commons.css.Selector;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.ThemeColors;
import com.pyx4j.entity.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.DefaultCCOmponentsTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.decorations.VistaLineSeparator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator;
import com.propertyvista.common.client.ui.decorations.VistaWidgetDecorator.StyleSuffix;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.portal.client.ui.PortalScreen;
import com.propertyvista.portal.client.ui.maps.PropertiesMapWidget;

public class PortalTheme extends VistaTheme {

    private final Skin skin;

    public PortalTheme(Skin skin) {
        this.skin = skin;
        initStyles();
    }

    protected void initStyles() {

        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new DefaultWidgetsTheme());

        addTheme(new DefaultWidgetDecoratorTheme());

        addTheme(new DefaultFormFlexPanelTheme() {
            @Override
            protected ThemeColors getBackgroundColor() {
                return ThemeColors.foreground;
            }

            @Override
            protected void initStyles() {
                super.initStyles();

                Style style = new Style(".", StyleName.FormFlexPanelH1);
                style.addProperty("margin", "0");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelH1Label);
                style.addProperty("color", getBackgroundColor(), 1.4);
                style.addProperty("padding", "20px");
                style.addProperty("font-size", "1.3em");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelActionWidget);
                style.addProperty("margin-top", "20px");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelActionWidget, " a");
                style.addProperty("color", getBackgroundColor(), 1.4);
                style.addProperty("font-style", "italic");
                addStyle(style);

                if (Skin.skin1.equals(skin)) {
                    style = new Style(".", StyleName.FormFlexPanelH1);
                    style.addProperty("border-top", "solid 1px");
                    style.addProperty("border-bottom", "solid 1px");
                    style.addProperty("border-top-color", ThemeColors.foreground, 0.3);
                    style.addProperty("border-bottom-color", ThemeColors.foreground, 0.5);
                    style.addGradient(ThemeColors.foreground, 0.1, ThemeColors.foreground, 0.4);
                    addStyle(style);
                } else if (Skin.skin2.equals(skin) || Skin.skin3.equals(skin)) {
                    style = new Style(".", StyleName.FormFlexPanelH1);
                    style.addGradient(ThemeColors.foreground, 0.2, ThemeColors.foreground, 0.2);
                    addStyle(style);
                }
            }
        });

        //.vista-pmsite-residentPage .content

        addTheme(new DefaultDatePickerTheme());

        addTheme(new DefaultSiteCrudPanelsTheme());
        addTheme(new DefaultDataTableTheme());

        addTheme(new DefaultCCOmponentsTheme());

        addTheme(new NewPaymentMethodEditorTheme());

        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColors getBackgroundColor() {
                return ThemeColors.foreground;
            }

            @Override
            protected void initStyles() {
                super.initStyles();
            }
        });

        initCheckBoxStyle();
        initHyperlinkStyle();
        initGroupBoxStyle();
        initSiteViewStyles();
        initPropertyMarkerStyle();

    }

    protected void initSiteViewStyles() {
        String prefix = PortalScreen.DEFAULT_STYLE_PREFIX;

        int minWidth = 960;
        int maxWidth = 960;
        int leftColumnWidth = 0;
        int rightColumnWidth = 0;

        Style style = new Style(Selector.valueOf(prefix));
        style.addProperty("width", "95%");
        style.addProperty("min-width", minWidth + "px");
        style.addProperty("max-width", maxWidth + "px");
        style.addProperty("margin", "0 auto");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PortalScreen.StyleSuffix.Display));
        addStyle(style);

        style = new Style(Selector.valueOf(VistaLineSeparator.DEFAULT_STYLE_PREFIX));
        style.addProperty("border-top-width", "1px");
        style.addProperty("border-top-style", "dotted");
        style.addProperty("border-top-color", ThemeColors.object1, 0.4);
        style.addProperty("margin-bottom", "0.3em");
        style.addProperty("width", "400px");
        addStyle(style);

        style = new Style(Selector.valueOf(VistaWidgetDecorator.DEFAULT_STYLE_PREFIX + StyleSuffix.Label));
        style.addProperty("padding-top", "2px");
        addStyle(style);

        String gwtButton = (".gwt-Button");
        List<Style> styles = getStyles(gwtButton);
        if (styles != null && styles.size() > 0) {
            for (Style st : styles) {
                if (st.getSelector().equals(gwtButton)) {
                    st.addProperty("border-radius", "5px");
                    st.addProperty("-moz-border-radius", "5px");
                    st.addProperty("background-color", ThemeColors.object1, 0.5);
                    st.addProperty("border", "1px solid");
                    st.addProperty("border-color", "black");
                    st.addProperty("min-width", "100px");
                    break;
                }
            }

        }

    }

    private void initPropertyMarkerStyle() {
        String prefix = PropertiesMapWidget.PROPERTY_CARD_STYLE_PREFIX;
        Style style = new Style(Selector.valueOf(prefix));
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardImage));
        style.addProperty("border-color", "#cccccc!important");
        style.addProperty("border", "solid 1px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardImage) + " img");
        style.addProperty("max-width", "100%!important");
        style.addProperty("width", "100%!important");
        style.addProperty("height", "100%!important");
        style.addProperty("-moz-background-size", "100% 100%!important");
        style.addProperty("-webkit-background-size", "100% 100%!important");
        style.addProperty("-khtml-background-size", "100% 100%!important");
        style.addProperty("background-size", "100% 100%!important");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardContent));
        style.addProperty("font-size", "12px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardContentItem));
        style.addProperty("border-bottom", "1px solid");
        style.addProperty("border-color", ThemeColors.object1, 0.4);
        style.addProperty("margin-bottom", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardLeft));
        style.addProperty("font-size", "12px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardLeftItem));
        style.addProperty("font-weight", "bold");
        style.addProperty("margin-bottom", "5px");
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardMenuItem));
        style.addProperty("text-decoration", "none");
        style.addProperty("color", ThemeColors.object1, 0.95);
        addStyle(style);

        style = new Style(Selector.valueOf(prefix, PropertiesMapWidget.StyleSuffix.CardMenuItem) + ":hover");
        style.addProperty("text-decoration", "underline");
        style.addProperty("color", ThemeColors.object1, 0.95);
        addStyle(style);

    }

}
