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
 * Created on Nov 8, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.datepicker;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeColors;

public class DefaultDatePickerTheme extends Theme {

    public static enum StyleName implements IStyleName {
        DatePickerMultyMonth, DatePickerGrid, DatePickerMonthSelector, DatePickerGridDaysRow, DatePickerNavigation,

        DatePickerMonthLabel, DatePickerYearLabel, DatePickerYearNavigation,

        DatePickerDay, DatePickerWeekdayLabel, DatePickerWeekendLabel, DatePickerTodayDay, DatePickerWeekendDay
    }

    public static enum StyleDependent implements IStyleDependent {
        disabled, heighlighted, selected, right, multiple, first, top, bottom, middle
    }

    public DefaultDatePickerTheme() {
        initStyles();
    }

    protected void initStyles() {
        initDatePickerStyle();
        initMultipleDatePicker();
    }

    protected void initDatePickerStyle() {

        Style style = new Style(".gwt-DatePicker");
        style.addProperty("margin", "2px 4px");
        style.addProperty("border", "1px solid");
        style.addProperty("border-color", ThemeColors.object1, 1.1);
        style.addProperty("color", ThemeColors.foreground);
        style.addProperty("width", "250px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthSelector);
        style.addProperty("background-color", ThemeColors.object1);
        style.addProperty("color", ThemeColors.object1, 0.1);
        style.addProperty("line-height", "12px");
        style.addProperty("border-collapse", "collapse");
        style.addProperty("width", "100%");
        style.addProperty("margin", "0");
        style.addProperty("padding", "0");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthLabel);
        style.addProperty("width", "110px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthSelector, " table");
        style.addProperty("border-collapse", "collapse");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthSelector, " td:focus");
        style.addProperty("outline-style", "none");
        style.addProperty("outline-width", "medium");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthSelector, " .gwt-Label");
        style.addProperty("font-weight", "bold");
        style.addProperty("font-size", "13px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthSelector, " .multiple");
        style.addProperty("line-height", "24px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthSelector, "  img");
        style.addProperty("width", "10px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthSelector, "  img.top");
        style.addProperty("position", "relative");
        style.addProperty("top", "4px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthSelector, "  img.middle");
        style.addProperty("position", "relative");
        style.addProperty("top", "2px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMonthSelector, "  img.bottom");
        style.addProperty("position", "relative");
        style.addProperty("top", "-2px");
        addStyle(style);

        style = new Style(" .monthSelectorNavigation.right");
        style.addProperty("border-right", "1px solid #A8B8B8");
        addStyle(style);

        style = new Style(" .monthSelectorNavigation");
        style.addProperty("width", "15%");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerDay, " .", StyleName.DatePickerWeekdayLabel, " .", StyleName.DatePickerWeekendLabel);
        style.addProperty("font-size", "75%");
        style.addProperty("outline-color", ThemeColors.foreground);
        style.addProperty("outline-style", "none");
        style.addProperty("outline-width", "medium");
        style.addProperty("padding", "4px");
        style.addProperty("text-align", "center");
        addStyle(style);

        style = new Style(" .", StyleName.DatePickerWeekdayLabel, " .", StyleName.DatePickerWeekendLabel);
        style.addProperty("background-color", ThemeColors.object1);
        style.addProperty("cursor", "default");
        style.addProperty("padding", "0 4px 2px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerDay);
        style.addProperty("cursor", "pointer");
        style.addProperty("padding", "4px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerTodayDay);
        style.addProperty("border", "1px solid black");
        style.addProperty("padding", "3px");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerWeekendDay);
        style.addProperty("background", "#EEEEEE none repeat scroll 0 0");
        addStyle(style);

        style = new Style(".DatePickerDayIsFiller");
        style.addProperty("color", "#888888");
        addStyle(style);

        style = new Style(".DatePickerDayIsValue");
        style.addProperty("background", "#AACCEE none repeat scroll 0 0");
        addStyle(style);

        style = new Style(".DatePickerDayIsDisabled");
        style.addProperty("color", "#AAAAAA");
        style.addProperty("font-style", "italic");
        addStyle(style);

        style = new Style(".DatePickerDayIsHighlighted");
        style.addProperty("background", "#F0E68C none repeat scroll 0 0");
        addStyle(style);

        style = new Style(".DatePickerDayIsValueAndHighlighted");
        style.addProperty("background", "#BBDDD9 none repeat scroll 0 0");
        addStyle(style);

        style = new Style("td.DatePickerMonth");
        style.addProperty("color", "blue");
        style.addProperty("font-size", "70%");
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "center");
        style.addProperty("white-space", "nowrap");
        addStyle(style);

        style = new Style(".DatePickerPreviousButton, .DatePickerNextButton");
        style.addProperty("color", "blue");
        style.addProperty("cursor", "pointer");
        style.addProperty("font-size", "120%");
        style.addProperty("line-height", "1em");
        style.addProperty("padding", "0 4px");
        addStyle(style);

        style = new Style(" .gwt-DatePicker td");
        style.addProperty("text-align", "center");
        style.addProperty("padding", "0");
        style.addProperty("font-size", "11px");
        style.addProperty("cursor", "pointer");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerGrid);
        style.addProperty("width", "100%");
        style.addProperty("background-color", ThemeColors.foreground, 0.1);
        style.addProperty("padding", "10px");
        addStyle(style);

        style = new Style(" .DatePickerGrid .gwt-Label");
        style.addProperty("border", "1px solid #F0F0F0");
        addStyle(style);

        style = new Style(" .DatePickerGrid .gwt-Label.disabled");
        style.addProperty("color", "#B0B0B0");
        addStyle(style);

        style = new Style(" .DatePickerGrid .gwt-Label.heighlighted");
        style.addProperty("border", "1px solid #D0D0F0");
        style.addProperty("background-color", ThemeColors.SELECTION);
        style.addProperty("color", ThemeColors.SELECTION_TEXT);
        addStyle(style);

        style = new Style(" .DatePickerGrid .gwt-Label.selected");
        style.addProperty("border", "1px solid #E06020");
        addStyle(style);

        style = new Style(" .DatePickerGrid tr.DatePickerGridDaysRow");
        style.addProperty("height", "20px");
        addStyle(style);

        style = new Style(" .DatePickerGrid .DatePickerGridDaysRow td");
        style.addProperty("border-bottom", "1px solid black");
        style.addProperty("margin-bottom", "5px");
        addStyle(style);

    }

    private void initMultipleDatePicker() {
        Style style = new Style(".", StyleName.DatePickerMultyMonth, " .DatePickerMonthSelector");
        style.addProperty("background-color", "#99A2A9");
        style.addProperty("color", "#FFF");
        style.addProperty("line-height", "12px");
        style.addProperty("border-bottom", "1px solid #A8A8A8");
        style.addProperty("border-collapse", "collapse");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMultyMonth, " .gwt-DatePicker.multiple");
        style.addProperty("border-left", "0");
        style.addProperty("margin", "0");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMultyMonth, " .gwt-DatePicker.multiple.first");
        style.addProperty("border-left", "1px solid #A8A8A8");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMultyMonth, " .monthSelectorNextMonth");
        style.addProperty("border-right", "1px solid #A8B8B8");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMultyMonth, " .monthSelectorMonthLabel");
        style.addProperty("width", "35%");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMultyMonth, " .monthSelectorYearLabel");
        style.addProperty("width", "25%");
        addStyle(style);

        style = new Style(".", StyleName.DatePickerMultyMonth, " .monthSelectorYearNavigation");
        style.addProperty("width", "10%");
        addStyle(style);
    }
}
