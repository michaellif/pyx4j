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
 * @version $Id$
 */
package com.pyx4j.selenium;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.JavascriptLibrary;

class InputHelper {

    static void fireEvent(WebDriver driver, WebElement we, String eventName) {
        new JavascriptLibrary().callEmbeddedSelenium(driver, "doFireEvent", we, eventName);
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
            setSelectValue(element, textValue);
        } else {
            throw new Error("Unsupported input " + element);
        }
    }

    static void setSelectValue(WebElement element, String textValue) {
        List<WebElement> allOptions = element.findElements(By.tagName("option"));
        boolean optionFound = false;
        for (WebElement opt : allOptions) {
            if (textValue == null) {
                // Select nothing
                if (opt.isSelected()) {
                    opt.toggle();
                }
            } else if (textValue.equals(opt.getText())) {
                opt.setSelected();
                optionFound = true;
                break;
            }
        }

        if ((textValue != null) && (!optionFound)) {
            throw new NoSuchElementException(textValue + " in " + element.getAttribute("id"));
        }
    }

    static void setValue(WebDriver driver, WebElement element, boolean selectionValue) {
        if (element.isSelected()) {
            if (!selectionValue) {
                element.toggle();
                fireEvent(driver, element, "click");
            }
        } else {
            if (selectionValue) {
                element.toggle();
                fireEvent(driver, element, "click");
            }
        }
    }

}
