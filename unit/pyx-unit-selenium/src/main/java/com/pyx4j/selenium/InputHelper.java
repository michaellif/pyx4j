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
 * Created on Mar 25, 2011
 * @author vlads
 */
package com.pyx4j.selenium;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.gwt.server.DateUtils;

class InputHelper {

    static void fireEvent(WebDriver driver, WebElement element, String eventName) {
        new JavascriptLibrary().callEmbeddedSelenium(driver, "doFireEvent", element, eventName);
    }

    static boolean isEditable(WebElement element) {
        String tagName = element.getTagName().toLowerCase();
        boolean acceptableTagName = "input".equals(tagName) || "select".equals(tagName);
        String readonly = "";
        if ("input".equals(tagName)) {
            readonly = element.getAttribute("readonly");
            if (readonly == null || "false".equals(readonly)) {
                readonly = "";
            }
        }
        return element.isEnabled() && acceptableTagName && "".equals(readonly);
    }

    static void setValue(WebElement element, String textValue) {
        String tagName = element.getTagName().toLowerCase();
        if (tagName == null) {
            throw new Error("Undefined element tag " + element);
        }
        if (tagName.equals("input")) {
            element.clear();
            element.sendKeys(textValue);
        } else if (tagName.equals("select")) {
            setSelectText(element, textValue);
        } else {
            throw new Error("Unsupported input <" + tagName + "> for value " + textValue);
        }
    }

    static void setSelectText(WebElement element, String textValue) {
        List<WebElement> allOptions = element.findElements(By.tagName("option"));
        boolean optionFound = false;
        for (WebElement opt : allOptions) {
            if (textValue == null) {
                // Select nothing
                if (opt.isSelected()) {
                    opt.click();
                }
            } else if (textValue.equals(opt.getText())) {
                if (!opt.isSelected()) {
                    opt.click();
                }
                optionFound = true;
                break;
            }
        }

        if ((textValue != null) && (!optionFound)) {
            throw new NoSuchElementException("text '" + textValue + "' in " + element.getAttribute("id"));
        }
    }

    static void setSelectValue(WebElement element, String textValue) {
        List<WebElement> allOptions = element.findElements(By.tagName("option"));
        boolean optionFound = false;
        for (WebElement opt : allOptions) {
            if (textValue == null) {
                // Select nothing
                if (opt.isSelected()) {
                    opt.click();
                }
            } else if (textValue.equals(getValue(opt))) {
                if (!opt.isSelected()) {
                    opt.click();
                }
                optionFound = true;
                break;
            }
        }

        if ((textValue != null) && (!optionFound)) {
            throw new NoSuchElementException("value '" + textValue + "' in " + element.getAttribute("id"));
        }
    }

    static void setValue(WebElement element, boolean selectionValue) {
        if (element.getTagName().equalsIgnoreCase("input")) {
            // CheckBox
            setSelectionValue(element, selectionValue);
        } else {
            // RadioGroup
            String parentId = element.getAttribute("id");
            boolean inputsFound = false;
            for (WebElement childElement : element.findElements(By.tagName("input"))) {
                String id = childElement.getAttribute("id");
                if (id.equals(parentId + "_Y")) {
                    inputsFound = true;
                    if (selectionValue) {
                        childElement.click();
                        break;
                    }
                } else if (id.equals(parentId + "_N")) {
                    inputsFound = true;
                    if (!selectionValue) {
                        childElement.click();
                        break;
                    }
                }
            }
            if (!inputsFound) {
                throw new Error("Can't find components inside RadioGroup " + parentId);
            }
        }
    }

    private static void setSelectionValue(WebElement element, boolean selectionValue) {
        if (element.isSelected() != selectionValue) {
            element.click();
        }
    }

    static Boolean getBooleanValue(WebElement element) {
        Boolean value = null;
        if (element.getTagName().equalsIgnoreCase("input")) {
            // CheckBox
            value = element.isSelected();
        } else {
            // RadioGroup
            String parentId = element.getAttribute("id");
            boolean inputsFound = false;
            for (WebElement childElement : element.findElements(By.tagName("input"))) {
                String id = childElement.getAttribute("id");
                if (id.equals(parentId + "_Y")) {
                    inputsFound = true;
                    if (childElement.isSelected()) {
                        value = Boolean.TRUE;
                        break;
                    }
                } else if (id.equals(parentId + "_N")) {
                    inputsFound = true;
                    if (childElement.isSelected()) {
                        value = Boolean.FALSE;
                        break;
                    }
                }
            }
            if (!inputsFound) {
                throw new Error("Can't find components inside RadioGroup " + parentId);
            }
        }
        return value;
    }

    static Date getDateValue(WebElement element, String format, boolean focusOnGetValue) {
        Date value = null;
        if (element.getTagName().equalsIgnoreCase("input")) {
            if (focusOnGetValue) {
                element.click();
            }
            String text = getValue(element);
            if (CommonsStringUtils.isStringSet(text)) {
                if (format != null) {
                    try {
                        value = new SimpleDateFormat(format).parse(text);
                    } catch (ParseException e) {
                        throw new Error("Invalid date format" + text, e);
                    }
                } else {
                    value = DateUtils.detectDateformat(text);
                }
            }
        } else {
            // RadioGroup
            String parentId = element.getAttribute("id");
            boolean inputsFound = false;
            int y = 0;
            try {
                WebElement elementYY = element.findElement(By.id(parentId + "_yy"));
                inputsFound = true;
                y = Integer.valueOf(getValue(elementYY));
            } catch (org.openqa.selenium.NoSuchElementException notFound) {
            }

            int m = 0;
            try {
                WebElement elementMM = element.findElement(By.id(parentId + "_mm"));
                inputsFound = true;
                m = Integer.valueOf(getValue(elementMM)) - 1;
            } catch (org.openqa.selenium.NoSuchElementException notFound) {
            }

            if (!inputsFound) {
                throw new Error("Can't find components inside DateGroup " + parentId);
            }

            if (y != 0) {
                value = DateUtils.createDate(y, m, 1);
            }
        }
        return value;
    }

    static void setDateValue(WebElement element, Date dateValue, String format) {
        if (element.getTagName().equalsIgnoreCase("input")) {
            element.clear();
            if (dateValue != null) {
                element.sendKeys(new SimpleDateFormat(format).format(dateValue));
            }
        } else {
            // RadioGroup
            String parentId = element.getAttribute("id");
            boolean inputsFound = false;
            try {
                WebElement elementYY = element.findElement(By.id(parentId + "_yy"));
                inputsFound = true;
                if (dateValue == null) {
                    setSelectValue(elementYY, "");
                } else {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(dateValue);
                    setSelectValue(elementYY, String.valueOf(calendar.get(Calendar.YEAR)));
                }
            } catch (org.openqa.selenium.NoSuchElementException notFound) {
            }

            try {
                WebElement elementMM = element.findElement(By.id(parentId + "_mm"));
                inputsFound = true;
                if (dateValue == null) {
                    setSelectValue(elementMM, "");
                } else {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(dateValue);
                    setSelectValue(elementMM, String.valueOf(calendar.get(Calendar.MONTH)));
                }
            } catch (org.openqa.selenium.NoSuchElementException notFound) {
            }

            if (!inputsFound) {
                throw new Error("Can't find components inside DateGroup " + parentId);
            }
        }
    }

    public static <T extends Enum<T>> T enumByText(Class<T> enumClass, String text) {
        for (T value : EnumSet.allOf(enumClass)) {
            if (value.toString().equals(text)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No enum " + enumClass.getModifiers() + " text " + text);
    }

    public static String getValue(WebElement element) {
        return element.getAttribute("value");
    }

    static <T extends Enum<T>> T getEnumValue(WebElement element, Class<T> enumClass, boolean focusOnGetValue) {
        String tagName = element.getTagName();
        if (tagName.equalsIgnoreCase("input") || tagName.equalsIgnoreCase("select")) {
            // ComboBox or Text
            if (focusOnGetValue) {
                if (tagName.equalsIgnoreCase("input")) {
                    element.click();
                }
            }
            //This is textual representation of Enum, i18n.tr(), not is name!
            String text = getValue(element);
            if (CommonsStringUtils.isEmpty(text)) {
                return null;
            } else {
                return enumByText(enumClass, text);
            }
        } else {
            // RadioGroup
            String parentId = element.getAttribute("id");
            boolean inputsFound = false;
            for (WebElement childElement : element.findElements(By.tagName("input"))) {
                String id = childElement.getAttribute("id");
                if (id.startsWith(parentId)) {
                    // Enum name is a part of element debugId
                    String enumName = id.substring(parentId.length() + 1);
                    T value;
                    try {
                        value = Enum.valueOf(enumClass, enumName);
                    } catch (IllegalArgumentException e) {
                        // Wrong input, ignore
                        continue;
                    }
                    if (childElement.isSelected()) {
                        return value;
                    } else {
                        inputsFound = true;
                    }
                }

            }
            if (!inputsFound) {
                throw new Error("Can't find components inside RadioGroup " + parentId);
            }
            return null;
        }
    }

    static void setEnumValue(WebElement element, Enum<?> enumValue) {
        String tagName = element.getTagName();
        if (tagName.equalsIgnoreCase("input") || tagName.equalsIgnoreCase("select")) {
            setValue(element, enumValue.toString());
        } else {
            // RadioGroup
            String parentId = element.getAttribute("id");
            for (WebElement childElement : element.findElements(By.tagName("input"))) {
                String id = childElement.getAttribute("id");
                if (id.startsWith(parentId)) {
                    // Enum name is a part of element debugId
                    String enumName = id.substring(parentId.length() + 1);
                    if (enumName.equals(enumValue.name())) {
                        if (!childElement.isSelected()) {
                            childElement.click();
                        }
                        return;
                    }
                }
            }
            throw new Error("Can't find enum " + enumValue + " component inside RadioGroup " + parentId);
        }
    }
}
