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
 * Created on Jan 18, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.themes;

import com.pyx4j.widgets.client.style.Style;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

public abstract class SiteTheme extends WindowsTheme {

    @Override
    protected void initStyles() {
        super.initStyles();
        initGeneralStyles();
        initSitePanelStyles();
        initContentPanelStyles();
        initHeaderStyles();
        initMainPanelStyles();
        initPageWidgetStyles();
        initFooterStyles();
        initHeaderCaptionsStyles();
        initLogoStyles();
        initPrimaryNavigStyles();
        initHeaderLinksStyles();
        initFooterLinksStyles();
        initFooterCopyrightStyles();
        initHtmlPortletStyles();
    }

    protected void initGeneralStyles() {
        Style style = new Style("html");
        style.addProperty("overflow-y", "scroll");
        addStyle(style);
        style = new Style("body");
        style.addProperty("margin", "0");
        style.addProperty("border", "none");
        addStyle(style);
        style = new Style("h1");
        style.addProperty("font-size", "2em");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h2");
        style.addProperty("font-size", "1.5em");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h3");
        style.addProperty("font-size", "1.17em");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h4, p, blockquote, ul, fieldset, form, ol, dl, dir, menu");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h5");
        style.addProperty("font-size", "1.12em");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h6");
        style.addProperty("font-size", ".75em");
        style.addProperty("margin", "0");
        addStyle(style);
        style = new Style("h1, h2, h3, h4, h5, h6, b, strong");
        style.addProperty("font-weight", "bolder");
        addStyle(style);
        style = new Style("blockquote");
        style.addProperty("margin-left", "40px");
        style.addProperty("margin-right", "40px");
        addStyle(style);
    }

    protected abstract void initSitePanelStyles();

    protected abstract void initContentPanelStyles();

    protected abstract void initHeaderStyles();

    protected abstract void initMainPanelStyles();

    protected abstract void initPageWidgetStyles();

    protected abstract void initFooterStyles();

    protected abstract void initHeaderCaptionsStyles();

    protected abstract void initLogoStyles();

    protected abstract void initPrimaryNavigStyles();

    protected abstract void initHeaderLinksStyles();

    protected abstract void initFooterLinksStyles();

    protected abstract void initFooterCopyrightStyles();

    protected abstract void initHtmlPortletStyles();

}
