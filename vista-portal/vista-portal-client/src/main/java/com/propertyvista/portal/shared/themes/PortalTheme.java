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
package com.propertyvista.portal.shared.themes;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.forms.client.ui.datatable.DataTableTheme;
import com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.FolderTheme;
import com.pyx4j.forms.client.ui.form.FormDecoratorTheme;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;
import com.pyx4j.forms.client.ui.panels.FormPanelTheme;
import com.pyx4j.site.client.frontoffice.ui.layout.FrontOfficeLayoutTheme;
import com.pyx4j.site.client.ui.devconsole.DevConsoleTheme;
import com.pyx4j.site.client.ui.layout.ResponsiveLayoutTheme;
import com.pyx4j.widgets.client.datepicker.DatePickerTheme;
import com.pyx4j.widgets.client.dialog.DialogTheme;
import com.pyx4j.widgets.client.richtext.RichTextEditorTheme;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.theme.TransactionHistoryViewerTheme;
import com.propertyvista.common.client.theme.VistaTheme.StyleName;
import com.propertyvista.domain.site.SiteDescriptor.Skin;
import com.propertyvista.portal.shared.resources.PortalImages;

public class PortalTheme extends Theme {

    private Skin skin;

    public PortalTheme() {
    }

    public void initStyles(Skin skin) {
        this.skin = skin;
        initGeneralStyles();
        initBodyStyles();
        initMenuBarStyles();
        initBackground();
        initMessageStyles();
        initCellListStyle();

        addTheme(new FrontOfficeLayoutTheme() {
            @Override
            protected void initStyles() {
                super.initStyles();

                Style style = new Style(".", StyleName.FrontOfficeLayoutInlineExtraPanel);
                style.addProperty("background", ThemeColor.foreground, 0.01);
                style.addProperty("border-color", ThemeColor.object1, 0.9);
                style.addProperty("border-style", "solid");
                style.addProperty("border-width", "1px");
                style.addProperty("border-radius", "5px");
                addStyle(style);

                style = new Style(".", StyleName.FrontOfficeLayoutInlineExtraPanel, "-", ResponsiveLayoutTheme.StyleDependent.extra2, " ."
                        + StyleName.FrontOfficeLayoutInlineExtraPanelCaption);
                style.addProperty("display", "none");
                addStyle(style);

                style = new Style(".", StyleName.FrontOfficeLayoutInlineExtraPanel, " .", PortalRootPaneTheme.StyleName.ExtraGadget);
                style.addProperty("width", "220px");
                addStyle(style);

            }
        });

        addTheme(new HorizontalAlignCenterMixin());

        addTheme(new BlockMixin());

        addTheme(new NavigationAnchorTheme());

        addTheme(new WidgetTheme() {

            @Override
            protected void initTextBoxStyle() {
                super.initTextBoxStyle();
                Style style = new Style(".", StyleName.TextBoxContainer);
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

            @Override
            protected void initButtonStyle() {
                super.initButtonStyle();

                Style style = new Style(" .", WidgetTheme.StyleName.Button);
                style.addProperty("height", "2.6em");
                style.addGradient(ThemeColor.object2, 1, ThemeColor.object2, 0.95);
                style.addProperty("background", ThemeColor.object2, 0.7);
                style.addProperty("border", "none");
                style.addProperty("border-color", ThemeColor.object2, 0.75);
                style.addProperty("border-radius", "5px");
                style.addProperty("margin", "0  0 0 10px");
                style.addProperty("color", "#fff");
                style.addProperty("outline", "none");
                style.addProperty("padding", "0 6px");
                addStyle(style);

                style = new Style(" .", WidgetTheme.StyleName.ButtonText);
                style.addProperty("line-height", "2.6em");
                addStyle(style);

            }

            @Override
            protected void initToolbarStyle() {
                super.initToolbarStyle();

                Style style = new Style(".", WidgetTheme.StyleName.ToolbarItem);
                style.addProperty("text-align", "center");
                style.addProperty("font-size", "0.9em");
                addStyle(style);

            }

            @Override
            protected void initAnchorStyle() {
                super.initAnchorStyle();
                Style style = new Style(".", StyleName.Anchor);
                style.addProperty("outline", "none");
                addStyle(style);
            }

            @Override
            protected void initSlideshow() {
                super.initSlideshow();
                Style style = new Style(".", StyleName.Slideshow);
                style.addProperty("border-radius", "5px");
                addStyle(style);
            }
        });

        addTheme(new WidgetDecoratorTheme() {
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

                style = new Style(".", StyleName.FormFlexPanelH1);
                style.addProperty("margin", "0");
                style.addProperty("background", "transparent");
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

            }
        });

        addTheme(new FormPanelTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }

            @Override
            protected int getSingleColumnWidth() {
                return 280;
            }

            @Override
            protected int getDualColumnWidth() {
                return 710;
            }

            @Override
            protected void initStyles() {
                super.initStyles();
                Style style = new Style(".", StyleName.FormPanelActionWidget, " .", WidgetTheme.StyleName.Button);
                style.addProperty("height", "auto");
                addStyle(style);

                style = new Style(".", StyleName.FormPanelActionWidget, " .", WidgetTheme.StyleName.Button, " .", WidgetTheme.StyleName.ButtonText);
                style.addProperty("line-height", "normal");
                addStyle(style);
            }
        });

        //.vista-pmsite-residentPage .content

//        addTheme(new DefaultPaneTheme());
        addTheme(new DataTableTheme());
        addTheme(new CComponentTheme());

        addTheme(new DatePickerTheme() {

            @Override
            protected void initDatePickerStyle() {
                super.initDatePickerStyle();

                Style style = new Style(".gwt-DatePicker");
                style.addProperty("border-color", ThemeColor.foreground, 1);
                addStyle(style);

                style = new Style(".", StyleName.DatePickerMonthSelector);
                style.addProperty("background-color", ThemeColor.foreground);
                style.addProperty("color", ThemeColor.foreground, 0.1);
                addStyle(style);

                style = new Style(" .", StyleName.DatePickerWeekdayLabel, " .", StyleName.DatePickerWeekendLabel);
                style.addProperty("background-color", ThemeColor.formBackground);
                addStyle(style);

                style = new Style(".", StyleName.DatePickerWeekendDayLabel);
                style.addProperty("color", ThemeColor.formBackground);
                addStyle(style);
            };

        });

        addTheme(new FolderTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }

            @Override
            protected void initStyles() {
                super.initStyles();
                Style style = new Style(".", StyleName.CFolderTableHeader);
                style.addProperty("width", "100%");
                style.addProperty("background-color", ThemeColor.foreground, 0.1);
                style.addProperty("line-height", "35px");
                style.addProperty("color", ThemeColor.foreground, 0.7);
                style.addProperty("border", "none");
                addStyle(style);

                style = new Style(".", StyleName.CFolderRowItemDecorator);
                style.addProperty("height", "35px");
                style.addProperty("border-bottom", "dotted 1px");
                style.addProperty("border-bottom-color", ThemeColor.foreground, 0.7);
                addStyle(style);

            }
        });
        addTheme(new DialogTheme() {

            @Override
            protected void initStyles() {
                super.initStyles();

                Style style = new Style(".", StyleName.Dialog);
                style.addProperty("border", "5px solid");
                style.addProperty("border-radius", "5px");
                style.addProperty("background-color", ThemeColor.foreground, 1);
                addStyle(style);

                style = new Style(".", StyleName.DialogCaption);
                style.addProperty("background-color", ThemeColor.foreground, 0.9);
                addStyle(style);
            }
        });

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
                style.addProperty("font-weight", "bold");
                addStyle(style);

                style = new Style(".", StyleName.FormDecoratorCaption);
                style.addProperty("float", "left");
                style.addProperty("font-size", "1.3em");
                style.addProperty("line-height", "40px");
                addStyle(style);

                style = new Style(".", StyleName.FormDecoratorHeader, " .", WidgetTheme.StyleName.Toolbar);
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
                style.addProperty("padding", "10px 15px");
                style.addProperty("margin", "10px");
                style.addProperty("overflow", "hidden");
                addStyle(style);

                style = new Style(".", StyleName.FormDecoratorMain, " ." + WidgetTheme.StyleName.Anchor);
                style.addProperty("color", ThemeColor.contrast2, 1);
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

                style = new Style(".", StyleName.FormDecoratorFooter, " .", WidgetTheme.StyleName.Toolbar);
                style.addProperty("float", "right");
                addStyle(style);

                style = new Style(".", StyleName.FormDecoratorFooter, " .", WidgetTheme.StyleName.ToolbarItem);
                style.addProperty("margin-left", "10px");
                addStyle(style);

            }
        });

        addTheme(new TenantDashboardTheme());
        addTheme(new CommunicationCenterTheme());
        addTheme(new PortalBillingTheme());
        addTheme(new TransactionHistoryViewerTheme());
        addTheme(new DashboardTheme());
        addTheme(new EntityViewTheme());
        addTheme(new StepsTheme());
        addTheme(new RichTextEditorTheme());

        addTheme(new DevConsoleTheme());

        //Call last
        addTheme(new SkinTheme(skin));

    }

    private void initBackground() {

        Style style = new Style(".", FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutMainHolder.name());
        if (skin == null) {
            style.addProperty("background-image", "url('" + PortalImages.INSTANCE.background().getSafeUri().asString() + "')");
        } else {
            style.addProperty("background-color", ThemeColor.siteBackground);
        }
        addStyle(style);

        style = new Style(".", FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutContentBackground.name());
        style.addProperty("background-color", ThemeColor.formBackground);
        style.addProperty("opacity", "0");
        addStyle(style);

        style = new Style(".", FrontOfficeLayoutTheme.StyleName.FrontOfficeLayoutFooterHolder.name());
        style.addProperty("background-color", ThemeColor.formBackground);
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

        style = new Style("li");
        style.addProperty("list-style-position", "inside");
        addStyle(style);

        style = new Style(".", WidgetTheme.StyleName.DropDownPanel);
        style.addProperty("z-index", "20");
        addStyle(style);

    }

    protected void initBodyStyles() {
        Style style = new Style("body");
        style.addProperty("background-color", "white");
        style.addProperty("color", ThemeColor.foreground);
        style.addProperty("margin", "0");
        style.addProperty("border", "none");
        style.addProperty("font-size", "16px");
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

    protected void initMessageStyles() {
        Style style = new Style(".", StyleName.ErrorMessage);
        style.addProperty("font-size", "1.1em");
        style.addProperty("color", "#EF231B");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.WarningMessage);
        style.addProperty("font-size", "1.1em");
        style.addProperty("color", "#F68308");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.InfoMessage);
        style.addProperty("font-size", "1.1em");
        style.addProperty("color", "#0091DC");
        style.addProperty("text-align", "left");
        addStyle(style);
    }

    protected void initCellListStyle() {
        // Available Selectors
        // cellListWidget
        // cellListEvenItem
        // cellListOddItem
        // cellListKeyboardSelectedItem
        // cellListSelectedItem

        Style style = new Style(".cellListWidget");
        style.addProperty("background-color", ThemeColor.foreground, 0.01);
        style.addProperty("color", ThemeColor.foreground, 0.9);
        addStyle(style);

        style = new Style(".cellListEvenItem");
        style.addProperty("color", ThemeColor.foreground, 0.9);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(".cellListOddItem");
        style.addProperty("background-color", ThemeColor.foreground, 0.05);
        style.addProperty("color", ThemeColor.foreground, 0.9);
        style.addProperty("cursor", "pointer");
        style.addProperty("cursor", "hand");
        addStyle(style);

        style = new Style(".cellListSelectedItem");
        style.addProperty("background-color", ThemeColor.foreground, 0.5);
        style.addProperty("color", ThemeColor.foreground, 0);
        addStyle(style);

    }

    @Override
    public ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

}
