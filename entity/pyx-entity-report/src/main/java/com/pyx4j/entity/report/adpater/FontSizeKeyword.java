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
 * Created on Apr 5, 2016
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.report.adpater;

enum FontSizeKeyword {

    xxsmall(6), xsmall(7.5), small(10), medium(12), large(13.5), xlarge(18), xxlarge(24);

    private double pt;

    public static FontSizeKeyword DEFAULT = FontSizeKeyword.medium;

    FontSizeKeyword(double pt) {
        this.pt = pt;
    }

    public double getPt() {
        return this.pt;
    }

    public static String getValueByNumber(int index) {
        if (index == 0) {
            return String.valueOf(xsmall.getPt());
        } else if (index > values().length) {
            return String.valueOf(xxlarge.getPt());
        } else if (index >= 0 && index <= values().length) {
            return String.valueOf(values()[index].getPt());
        } else {
            return null;
        }
    }

    public static String getValueByName(String name) {
        String convertedName = name.replaceAll("-", "");
        if (JasperReportStyledUtils.isValidEnum(FontSizeKeyword.class, convertedName)) {
            FontSizeKeyword keyword = FontSizeKeyword.valueOf(convertedName);
            return String.valueOf(keyword.getPt());
        } else {
            return null;
        }
    }
}