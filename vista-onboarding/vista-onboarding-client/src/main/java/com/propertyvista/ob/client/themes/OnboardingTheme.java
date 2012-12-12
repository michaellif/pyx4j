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

import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.DefaultCComponentsTheme;
import com.pyx4j.forms.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.forms.client.ui.decorators.DefaultWidgetDecoratorTheme;
import com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.forms.client.ui.panels.DefaultFormFlexPanelTheme;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.widgets.client.DefaultWidgetsTheme;
import com.pyx4j.widgets.client.datepicker.DefaultDatePickerTheme;
import com.pyx4j.widgets.client.dialog.DefaultDialogTheme;
import com.pyx4j.widgets.client.richtext.DefaultRichTextEditorTheme;
import com.pyx4j.widgets.client.tabpanel.DefaultTabTheme;

import com.propertyvista.ob.client.forms.PmcAccountCreationRequestForm;
import com.propertyvista.ob.client.forms.StepStatusIndicator;
import com.propertyvista.ob.client.views.PmcAccountCreationCompleteViewImpl;

public class OnboardingTheme extends Theme {

    public OnboardingTheme() {

        addTheme(new DefaultWidgetsTheme());
        addTheme(new DefaultWidgetDecoratorTheme());
        addTheme(new DefaultFormFlexPanelTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new DefaultEntityFolderTheme() {
            @Override
            protected ThemeColor getBackgroundColor() {
                return ThemeColor.foreground;
            }
        });
        addTheme(new DefaultRichTextEditorTheme());
        addTheme(new DefaultDatePickerTheme());
        addTheme(new DefaultSiteCrudPanelsTheme());
        addTheme(new DefaultDataTableTheme());
        addTheme(new DefaultDialogTheme());
        addTheme(new DefaultCComponentsTheme());

        addTheme(new DefaultTabTheme());

        Style style = new Style(".", OnboardingStyles.VistaObMainPanel.name());
        style.addProperty("min-width", "700px");
        style.addProperty("min-height", "500px");
        addStyle(style);

        style = new Style(".", OnboardingStyles.VistaObView.name());
        style.addProperty("width", "700px");
        style.addProperty("height", "500px");
        style.addProperty("display", "table-cell");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", StepStatusIndicator.Styles.StepStatusIndicatorPanel);
        style.addGradient(ThemeColor.contrast1, ThemeColor.contrast2);
        style.addProperty("padding-left", "20px");
        style.addProperty("padding-right", "20px");
        style.addProperty("padding-top", "5px");
        style.addProperty("padding-bottom", "5px");
        style.addProperty("font-weight", "bold");
        addStyle(style);

        style = new Style(".", PmcAccountCreationRequestForm.Styles.PmcAccountCreationSubmitButton.name());
        style.addProperty("padding-left", "30px");
        style.addProperty("padding-right", "30px");
        style.addProperty("padding-top", "10px");
        style.addProperty("padding-bottom", "10px");
        style.addProperty("font-size", "16pt");
        style.addProperty("border-radius", "10px");
        style.addProperty("margin-top", "30px");
        addStyle(style);

        style = new Style(".", PmcAccountCreationCompleteViewImpl.Styles.PmcAccountCreationCompleteLabel.name());
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("text-align", "center");
        style.addProperty("width", "100%");
        addStyle(style);

        style = new Style(".", PmcAccountCreationCompleteViewImpl.Styles.PmcAccountCreationCompleteAnchor.name());
        style.addProperty("margin-top", "20px");
        style.addProperty("margin-left", "auto");
        style.addProperty("margin-right", "auto");
        style.addProperty("text-align", "center");
        style.addProperty("width", "100%");
        addStyle(style);
    }

}
