/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Oct 20, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.decorators;

import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleDependent.noMandatoryStar;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleDependent.readOnly;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleName.WidgetDecorator;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleName.WidgetDecoratorComponent;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleName.WidgetDecoratorComponentHolder;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleName.WidgetDecoratorContentPanel;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleName.WidgetDecoratorInfoImage;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleName.WidgetDecoratorLabel;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleName.WidgetDecoratorLabelHolder;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleName.WidgetDecoratorMandatoryImage;
import static com.pyx4j.forms.client.ui.decorators.WidgetDecorator.StyleName.WidgetDecoratorValidationLabel;

import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;

public class DefaultWidgetDecoratorTheme extends Theme {

    public DefaultWidgetDecoratorTheme() {
        initStyles();
    }

    protected void initStyles() {
        Style style = new Style(".", WidgetDecorator);
        addStyle(style);

        style = new Style(".", WidgetDecoratorLabelHolder);
        style.addProperty("padding-right", "10px");
        addStyle(style);

        style = new Style(".", WidgetDecoratorLabel);
        style.addProperty("color", "#333333");
        style.addProperty("font-weight", "bold");
        style.addProperty("display", "inline");
        addStyle(style);

        style = new Style(".", WidgetDecorator, "-", readOnly, " .", WidgetDecoratorLabel);
        style.addProperty("color", "#888888");
        addStyle(style);

        style = new Style(".", WidgetDecoratorInfoImage);
        style.addProperty("display", "inline-block");
        style.addProperty("padding-left", "5px");
        style.addProperty("vertical-align", "middle");
        addStyle(style);

        style = new Style(".", WidgetDecoratorMandatoryImage);
        style.addProperty("display", "inline-block");
        style.addProperty("width", "5px");
        style.addProperty("padding-right", "3px");
        addStyle(style);

        style = new Style(".", WidgetDecorator, "-", noMandatoryStar, " .", WidgetDecoratorMandatoryImage, " ", "img");
        style.addProperty("display", "none");
        addStyle(style);

        style = new Style(".", WidgetDecoratorValidationLabel);
        style.addProperty("color", "red");
        addStyle(style);

        style = new Style(".", WidgetDecoratorContentPanel);
        addStyle(style);

        style = new Style(".", WidgetDecoratorComponentHolder);
        //style.addProperty("padding-right", "10px");
        style.addProperty("display", "inline-block");
        addStyle(style);

        style = new Style(".", WidgetDecoratorComponent);
        addStyle(style);

    }
}
