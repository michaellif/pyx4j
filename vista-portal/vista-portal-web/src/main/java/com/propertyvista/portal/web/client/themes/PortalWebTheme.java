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
package com.propertyvista.portal.web.client.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.form.FormDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.theme.TransactionHistoryViewerTheme;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.portal.web.client.resources.PortalImages;

public class PortalWebTheme extends Theme {

    private final Skin skin;

    public PortalWebTheme(Skin skin) {
        this.skin = skin;
        initStyles();

    }

    protected void initStyles() {
        initGeneralStyles();
        initBodyStyles();
        initMenuBarStyles();

        initBackground();

        addTheme(new PortalWebRootPaneTheme());

        addTheme(new ResponsiveLayoutTheme());

        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new BlockMixin());

        addTheme(new DefaultWidgetsTheme() {

            @Override
            protected void initTextBoxStyle() {
                super.initTextBoxStyle();
                Style style = new Style(".", StyleName.TextBox);
                style.addProperty("border-radius", "5px");
                addStyle(style);
            };

            @Override
            protected void initListBoxStyle() {
                super.initListBoxStyle();
                Style style = new Style(".", StyleName.ListBox);
                style.addProperty("border-radius", "5px");
                addStyle(style);
            };

        });

        addTheme(new DefaultWidgetDecoratorTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new FlexFormPanelTheme() {

            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }

            @Override
            protected void initStyles() {
                super.initStyles();

                Style style = new Style(".", StyleName.FormFlexPanel);
                style.addProperty("width", "100%");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelLeftColumn);
                style.addProperty("width", "100%");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelRightColumn);
                style.addProperty("width", "0");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelLeftCell);
                style.addProperty("text-align", "center");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelH1);
                style.addProperty("margin", "0");
                style.addProperty("background-color", "transparent");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelH1Label);
                style.addProperty("color", ThemeColor.foreground, 1);
                style.addProperty("font-size", "1.2em");
                addStyle(style);

                style = new Style(".", StyleName.FormFlexPanelH4Label);
                style.addProperty("color", getBackgroundColor(), 0.9);
                style.addProperty("font-size", "1.1em");
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
                    style.addProperty("border-top-color", ThemeColor.foreground, 0.3);
                    style.addProperty("border-bottom-color", ThemeColor.foreground, 0.5);
                    style.addGradient(ThemeColor.foreground, 0.1, ThemeColor.foreground, 0.4);
                    addStyle(style);
                } else if (Skin.skin2.equals(skin) || Skin.skin3.equals(skin)) {
                    style = new Style(".", StyleName.FormFlexPanelH1);
                    style.addGradient(ThemeColor.foreground, 0.2, ThemeColor.foreground, 0.2);
                    addStyle(style);
                }
            }
        });

        //.vista-pmsite-residentPage .content

//        addTheme(new DefaultPaneTheme());
        addTheme(new DefaultDataTableTheme());
        addTheme(new DefaultCComponentsTheme());
        addTheme(new DefaultDatePickerTheme());
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }

            @Override
            protected void initStyles() {
                super.initStyles();
                Style style = new Style(".", StyleName.EntityFolderTableHeader);
                style.addProperty("width", "100%");
                style.addProperty("background-color", ThemeColor.foreground, 0.1);
                style.addProperty("line-height", "35px");
                style.addProperty("color", ThemeColor.foreground, 0.7);
                style.addProperty("border", "none");
                addStyle(style);

                style = new Style(".", StyleName.EntityFolderRowItemDecorator);
                style.addProperty("height", "35px");
                style.addProperty("border-bottom", "dotted 1px");
                style.addProperty("border-bottom-color", ThemeColor.foreground, 0.7);
                addStyle(style);

            }
        });
        addTheme(new DefaultDialogTheme());

        addTheme(new FormDecoratorTheme() {

            @Override
            protected void initHeaderStyles() {
                Style style = new Style(".", StyleName.FormDecoratorHeader);
                style.addProperty("background", ThemeColor.foreground, 0.01);
                style.addProperty("border-color", ThemeColor.foreground, 0.3);
                style.addProperty("border-style", "solid");
                style.addProperty("border-width", "1px");
                style.addProperty("border-radius", "5px");
                style.addProperty("padding", "10px");
                style.addProperty("margin", "10px");
                style.addProperty("overflow", "hidden");
                style.addProperty("font-size", "1.3em");
                style.addProperty("font-weight", "bold");
                addStyle(style);

                style = new Style(".", StyleName.FormDecoratorCaption);
                style.addProperty("float", "left");
                style.addProperty("line-height", "40px");
                addStyle(style);

                style = new Style(".", StyleName.FormDecoratorHeader, " .", DefaultWidgetsTheme.StyleName.Toolbar);
                style.addProperty("float", "right");
                addStyle(style);
            }

            @Override
            protected void initContentStyles() {
                Style style = new Style(".", StyleName.FormDecoratorMain);
                style.addProperty("background", ThemeColor.foreground, 0.01);
                style.addProperty("border-color", ThemeColor.foreground, 0.3);
                style.addProperty("border-style", "solid");
                style.addProperty("border-width", "1px");
                style.addProperty("border-radius", "5px");
                style.addProperty("padding", "10px");
                style.addProperty("margin", "10px");
                style.addProperty("overflow", "hidden");
                addStyle(style);

                style = new Style(".", StyleName.FormDecorator, " .", DefaultWidgetsTheme.StyleName.Toolbar, " .", DefaultWidgetsTheme.StyleName.Button);
                style.addProperty("width", "100%");
                style.addProperty("line-height", "40px");
                style.addProperty("background", "#aaa");
                style.addProperty("border-radius", "5px");
                style.addProperty("border", "none");
                style.addProperty("color", ThemeColor.foreground, 0.01);
                addStyle(style);

                super.initContentStyles();
            }

            @Override
            protected void initFooterStyles() {
                Style style = new Style(".", StyleName.FormDecoratorFooter);
                style.addProperty("background", ThemeColor.foreground, 0.01);
                style.addProperty("border-color", ThemeColor.foreground, 0.3);
                style.addProperty("border-style", "solid");
                style.addProperty("border-width", "1px");
                style.addProperty("border-radius", "5px");
                style.addProperty("padding", "10px");
                style.addProperty("margin", "10px");
                style.addProperty("overflow", "hidden");
                addStyle(style);

                style = new Style(".", StyleName.FormDecoratorFooter, " .", DefaultWidgetsTheme.StyleName.Toolbar);
                style.addProperty("float", "right");
                addStyle(style);

                style = new Style(".", StyleName.FormDecoratorFooter, " .", DefaultWidgetsTheme.StyleName.ToolbarItem);
                style.addProperty("margin-left", "10px");
                addStyle(style);

            }
        });

        addTheme(new TenantDashboardTheme());
        addTheme(new CommunicationCenterTheme());
        addTheme(new NewPaymentMethodEditorTheme());
        addTheme(new BillingTheme());
        addTheme(new TransactionHistoryViewerTheme());
        addTheme(new DashboardTheme());
        addTheme(new ExtraGadgetsTheme());
        addTheme(new EntityViewTheme());
        addTheme(new TenantInsuranceTheme());

    }

    private void initBackground() {

        Style style = new Style(".", ResponsiveLayoutTheme.StyleName.ResponsiveLayoutMainHolder.name());
        style.addProperty("background-image", "url('" + PortalImages.INSTANCE.background().getSafeUri().asString() + "')");
        addStyle(style);

        style = new Style(".", ResponsiveLayoutTheme.StyleName.ResponsiveLayoutContentBackground.name());
        style.addProperty("background-color", ThemeColor.background);
        style.addProperty("opacity", "0");
        addStyle(style);

        style = new Style(".", ResponsiveLayoutTheme.StyleName.ResponsiveLayoutFooterHolder.name());
        style.addProperty("background-color", ThemeColor.background);
        style.addProperty("opacity", "0.9");
        addStyle(style);
    }

    protected void initGeneralStyles() {
        Style style = new Style("html");
        style.addProperty("overflow", "hidden");
        addStyle(style);

        style = new Style("a");
        style.addProperty("text-decoration", "none");
        addStyle(style);

        style = new Style("a:hover");
        style.addProperty("text-decoration", "underline");
        addStyle(style);

        style = new Style("table");
        style.addProperty("border-collapse", "collapse");
        addStyle(style);

        style = new Style("td");
        style.addProperty("padding", "0px");
        addStyle(style);

        style = new Style("p");
        style.addProperty("margin", "0.3em");
        addStyle(style);

        style = new Style("h1");
        style.addProperty("font-size", "2em");
        style.addProperty("line-height", "2.5em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h2");
        style.addProperty("font-size", "1.5em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h3");
        style.addProperty("font-size", "1.17em");
        style.addProperty("padding-bottom", "0.5px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h4, blockquote");
        style.addProperty("font-size", "1.12em");
        style.addProperty("padding-bottom", "0.3px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h5");
        style.addProperty("font-size", "1.08em");
        style.addProperty("padding-bottom", "0.2px");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h6");
        style.addProperty("font-size", ".75em");
        style.addProperty("padding-bottom", "0.2px");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style("h1, h2, h3, h4, h5, h6, b, strong");
        style.addProperty("font-weight", "bolder");
        addStyle(style);

        style = new Style("blockquote, ul, fieldset, form, ol, dl, dir, menu");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style("blockquote");
        style.addProperty("margin-left", "40px");
        style.addProperty("margin-right", "40px");
        addStyle(style);

        style = new Style(".", DefaultWidgetsTheme.StyleName.DropDownPanel);
        style.addProperty("z-index", "20");
        addStyle(style);

    }

    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background-color", "white");
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("margin", "0");
        style.addProperty("border", "none");
        style.addProperty("font-family", "'Lato', sans-serif");
        addStyle(style);

    }

    protected void initMenuBarStyles() {
        Style style = new Style(".gwt-MenuBar");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("color", ThemeColor.foreground, 0.2);
        addStyle(style);

        style = new Style(".gwt-MenuItem");
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("background-color", ThemeColor.foreground, 0.2);
        style.addProperty("background", "transparent");
        style.addProperty("color", "#E5F0E1");
        style.addProperty("border", "0");
        addStyle(style);

        style = new Style(".gwt-MenuItem-selected");
        style.addProperty("background", ThemeColor.foreground, 0.8);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        style.addProperty("text-decoration", "underline");
        style.addProperty("background", "transparent");
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical");
        style.addProperty("margin-top", "0px");
        style.addProperty("margin-left", "0px");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColor.foreground, 0.2);
        style.addProperty("background", "#FFFFFF");
        addStyle(style);

        style = new Style(".gwt-MenuBar-vertical .gwt-MenuItem");
        style.addProperty("padding", "4px 14px 4px 1px");
        style.addProperty("color", "#666666");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal");
        addStyle(style);

        style = new Style(".gwt-MenuBar-horizontal .gwt-MenuItem");
        style.addProperty("vertical-align", "bottom");
        style.addProperty("background", "transparent");
        addStyle(style);
    }

    @Override
    public ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
