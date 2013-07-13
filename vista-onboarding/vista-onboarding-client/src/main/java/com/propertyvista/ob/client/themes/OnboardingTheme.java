/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-10
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.themes;

import com.google.gwt.canvas.dom.client.CssColor;

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.commons.css.ThemeId;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;
import com.pyx4j.widgets.client.richtext.DefaultRichTextEditorTheme;
import com.pyx4j.widgets.client.tabpanel.DefaultTabTheme;

import com.propertyvista.ob.client.forms.PmcAccountCreationRequestForm;
import com.propertyvista.ob.client.forms.StepStatusIndicator;
import com.propertyvista.ob.client.views.PmcAccountCreationCompleteViewImpl;
import com.propertyvista.ob.client.views.PmcAccountCreationProgressViewAltImpl;
import com.propertyvista.ob.client.views.RuntimeErrorViewImpl;

public class OnboardingTheme extends Theme {

    public final static CssColor COLOR_PROGRESS_CIRCLE_COMPLETE = CssColor.make(0x77, 0xCC, 0x77);

    public final static CssColor COLOR_PROGRESS_CIRCLE_INCOMPLETE = CssColor.make(0xDD, 0xDD, 0xDD);

    private final static String COLOR_SPECIAL_FOREGROUND = "#607280";

    public OnboardingTheme() {

        addTheme(new DefaultWidgetsTheme());
        addTheme(new DefaultWidgetDecoratorTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new FlexFormPanelTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.background;
            }
        });
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.background;
            }
        });
        addTheme(new DefaultRichTextEditorTheme());
        addTheme(new DefaultDatePickerTheme());
        addTheme(new DefaultPaneTheme());
        addTheme(new DefaultDataTableTheme());
        addTheme(new DefaultDialogTheme());
        addTheme(new DefaultCComponentsTheme());

        addTheme(new DefaultTabTheme());

        initCommonStuff();
        initPmcAccountRequestFormLayoutAndStyles();
        initPmcAccountCreationProgressViewStyles();
        initPmcAccountCreationCompleteStyles();
        initRuntimeErrorViewStyles();
    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }

    private void initCommonStuff() {
        Style style = new Style("div, tr, td, th");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".TextBox");
        style.addProperty("border-style", "inset");
        addStyle(style);

        style = new Style(".", OnboardingStyles.VistaObMainPanel.name());
        style.addProperty("width", "700px");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        addStyle(style);

        style = new Style(".", OnboardingStyles.VistaObView.name());
        style.addProperty("width", "700px");
        style.addProperty("height", "500px");
        style.addProperty("display", "table-cell");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style("." + OnboardingStyles.OnboardingCaption.name());
        style.addProperty("text-align", "center");
        style.addProperty("margin-bottom", "40px");
        style.addProperty("font-family", "Tahoma");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "2.5em");
        style.addProperty("color", COLOR_SPECIAL_FOREGROUND);
        addStyle(style);

    }

    private void initPmcAccountRequestFormLayoutAndStyles() {
        Style style = new Style(".", PmcAccountCreationRequestForm.Styles.PmcUrlFieldNote.name() + " p");
        style.addProperty("padding-bottom", "5px");
        style.addProperty("text-align", "justify");
        style.addProperty("color", ThemeColor.object1, 0.7);
        addStyle(style);

        style = new Style(".", PmcAccountCreationRequestForm.Styles.PmcUrlFieldNote.name());
        style.addProperty("margin-bottom", "10px");
        addStyle(style);

        style = new Style(".", PmcAccountCreationRequestForm.Styles.PmcAccountCreationSubmitButton.name());
        style.addProperty("padding-left", "30px");
        style.addProperty("padding-right", "30px");
        style.addProperty("padding-top", "10px");
        style.addProperty("padding-bottom", "10px");
        style.addProperty("font-size", "1.2em");
        style.addProperty("border-radius", "10px");
        addStyle(style);

        style = new Style("." + PmcAccountCreationRequestForm.Styles.PmcUrlFieldNote.name() + " .ValidationLabel");
        style.addProperty("width", "10em");
        addStyle(style);

        style = new Style("." + PmcAccountCreationRequestForm.Styles.PmcAccountCreationRequestForm.name() + " .ValidationLabel");
        style.addProperty("width", "25em");
        addStyle(style);

        // this is for text on the left
        style = new Style("." + OnboardingStyles.SignUpText.name());
        style.addProperty("margin-right", "20px");
        addStyle(style);

        style = new Style("." + OnboardingStyles.SignUpTextSection.name());
        style.addProperty("margin-bottom", "20px");
        style.addProperty("font-size", "14px");
        addStyle(style);

        style = new Style("." + OnboardingStyles.SignUpSectionCaption.name());
        style.addProperty("font-style", "italic");
        addStyle(style);

        style = new Style("." + OnboardingStyles.SignUpSectionDetails.name());
        addStyle(style);

        style = new Style(".", OnboardingStyles.OnboardingMessage.name());
        style.addProperty("font-weight", "bold");
        style.addProperty("color", "#BB0000");
        style.addProperty("width", "100%");
        style.addProperty("text-align", "center");
        style.addProperty("font-size", "16px");
        style.addProperty("margin", "1em");
        style.addProperty("margin-bottom", "3em");
        addStyle(style);
    }

    private void initPmcAccountCreationProgressViewStyles() {

        Style style = new Style(".", StepStatusIndicator.Styles.StepStatusIndicatorPanel);
        style.addGradient(ThemeColor.contrast1, ThemeColor.contrast2);
        style.addProperty("padding-left", "20px");
        style.addProperty("padding-right", "20px");
        style.addProperty("padding-top", "5px");
        style.addProperty("padding-bottom", "5px");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", PmcAccountCreationCompleteViewImpl.Styles.PmcAccountCreationCompleteAnchor.name());
        style.addProperty("margin-top", "20px");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("text-align", "center");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", PmcAccountCreationProgressViewAltImpl.Styles.PleaseWaitLabel.name());
        style.addProperty("margin-bottom", "30px");
        style.addProperty("font-style", "italic");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-family", "Tahoma");
        style.addProperty("color", COLOR_SPECIAL_FOREGROUND);
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(".", PmcAccountCreationProgressViewAltImpl.Styles.ProgressStepDetails.name());
        style.addProperty("margin-top", "30px");
        style.addProperty("text-align", "center");
        addStyle(style);
    }

    private void initRuntimeErrorViewStyles() {
        Style style = new Style("." + RuntimeErrorViewImpl.Styles.RuntimeErrorTitle.name());
        style.addProperty("text-align", "center");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "1.2em");
        addStyle(style);

        style = new Style("." + RuntimeErrorViewImpl.Styles.RuntimeErrorMessage.name());
        style.addProperty("padding-top", "20px");
        style.addProperty("padding-bottom", "20px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style("." + RuntimeErrorViewImpl.Styles.RuntimeErrorAck.name());
        addStyle(style);
    }

    private void initPmcAccountCreationCompleteStyles() {
        Style style = new Style(".", PmcAccountCreationCompleteViewImpl.Styles.PmcAccountCreationCompleteLabel.name());
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("font-family", "Tahoma");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "1.2em");
        style.addProperty("text-align", "center");
        style.addProperty("color", COLOR_SPECIAL_FOREGROUND);
        style.addProperty("width", "100%");
        addStyle(style);
    }

}
