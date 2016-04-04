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
 * Created on Apr 3, 2016
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.report.adapter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.entity.report.adpater.JasperReportStyledUtils;
import com.pyx4j.gwt.server.IOUtils;

public class JasperReportStyledUtilsTest {

    @Test
    public void copyInhiretedAttributes() throws IOException {

        String htmlText = null;

        htmlText = IOUtils.getTextResource("copyAttributes.html", this.getClass());

        if (htmlText == null) {
            Assert.fail("Unable to load html resource for test");
        }

        Document htmlDocument = Jsoup.parse(htmlText);
        List<Node> childNodes = htmlDocument.select("body").get(0).childNodes();
        int i = 0;
        for (Node node : childNodes) {
            System.out.println("Node " + i++ + node);
        }

        // First scenario: One inherited attribute present in child
        Attributes inhiretedAttributes = childNodes.get(2).attributes();
        Attributes targetAttributes = childNodes.get(4).attributes();
        Attributes expectedAttributes = childNodes.get(6).attributes();

        Attributes result = JasperReportStyledUtils.inheriteAttributes(inhiretedAttributes, targetAttributes);

        Assert.assertTrue("Expected attribute does not match!", expectedAttributes.toString().trim().equalsIgnoreCase(result.toString().trim()));

        // Second scenario: No inherited attributes present in child
        inhiretedAttributes = childNodes.get(10).attributes();
        targetAttributes = childNodes.get(12).attributes();
        expectedAttributes = childNodes.get(14).attributes();

        result = JasperReportStyledUtils.inheriteAttributes(inhiretedAttributes, targetAttributes);
        Assert.assertTrue("Expected attribute does not match!", areSameAttributes(expectedAttributes, result));

        // Third scenario: No attributes present in child
        inhiretedAttributes = childNodes.get(18).attributes();
        targetAttributes = childNodes.get(20).attributes();
        expectedAttributes = childNodes.get(22).attributes();

        result = JasperReportStyledUtils.inheriteAttributes(inhiretedAttributes, targetAttributes);
        Assert.assertTrue("Expected attribute does not match!", areSameAttributes(expectedAttributes, result));

        // Fourth scenario: No attributes present in inheritance
        inhiretedAttributes = childNodes.get(26).attributes();
        targetAttributes = childNodes.get(28).attributes();
        expectedAttributes = childNodes.get(30).attributes();

        result = JasperReportStyledUtils.inheriteAttributes(inhiretedAttributes, targetAttributes);
        Assert.assertTrue("Expected attribute does not match!", areSameAttributes(expectedAttributes, result));
    }

    private boolean areSameAttributes(Attributes attributes1, Attributes attributes2) {
        // Same size
        if (attributes1.size() != attributes2.size()) {
            return false;
        }

        // Same attributes
        for (Attribute attribute : attributes1.asList()) {
            if (!attributes2.hasKey(attribute.getKey())) {
                return false;
            }

            Map<String, Object> attributeValuesIn1 = JasperReportStyledUtils.toMap(attribute.getValue());
            Map<String, Object> attributeValuesIn2 = JasperReportStyledUtils.toMap(attributes2.get(attribute.getKey()));

            // Same values size
            if (attributeValuesIn1.size() != attributeValuesIn2.size()) {
                return false;
            }

            // Same values
            for (Map.Entry<String, Object> entry : attributeValuesIn1.entrySet()) {
                if (!attributeValuesIn2.containsKey(entry.getKey()) || !attributeValuesIn2.get(entry.getKey()).equals(entry.getValue())) {
                    return false;
                }
            }

        }

        return true;
    }
}
