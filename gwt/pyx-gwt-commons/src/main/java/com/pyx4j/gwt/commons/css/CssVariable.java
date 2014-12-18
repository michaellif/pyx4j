/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 8, 2014
 * @author michaellif
 */
package com.pyx4j.gwt.commons.css;

import java.util.HashMap;

import com.google.gwt.dom.client.Element;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.RootPanel;

public class CssVariable {

    public static void setVariable(Element el, String name, String value) {
        HashMap<String, String> vars = parse(el);
        vars.put(name, value);
        write(el, vars);
    }

    public static void removeVariable(Element el, String name) {
        HashMap<String, String> vars = parse(el);
        vars.remove(name);
        write(el, vars);
    }

    public static String getVariable(Element el, String name) {
        return parse(el).get(name);
    }

    private static HashMap<String, String> parse(Element el) {
        String input = ComputedStyle.getStyleProperty(el, "fontFamily");
        HashMap<String, String> vars = new HashMap<>();

        RegExp regExp = RegExp.compile("['\"]var\\.(.+?)=(.*?)['\"]", "g");

        MatchResult result = null;
        while ((result = regExp.exec(input)) != null) {
            vars.put(result.getGroup(1), result.getGroup(2));
        }
        return vars;
    }

    private static String format(String fontFamily, HashMap<String, String> vars) {
        StringBuilder builder = new StringBuilder(fontFamily);
        for (String name : vars.keySet()) {
            builder.append(",'var.").append(name).append("=").append(vars.get(name)).append("'");
        }
        return builder.toString();
    }

    private static void write(Element el, HashMap<String, String> vars) {
        el.getStyle().setProperty("fontFamily", format(ComputedStyle.getStyleProperty(RootPanel.getBodyElement(), "fontFamily"), vars));
    }
}
