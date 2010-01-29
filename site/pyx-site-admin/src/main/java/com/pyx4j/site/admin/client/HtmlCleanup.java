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
 * Created on Jan 28, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.admin.client;

public class HtmlCleanup {

    public static String cleanup(String html) {
        html = html.replaceAll(" type=\"text/javascript\"", "");
        html = html.replaceAll("<script.*</script>", "");
        if (html.contains("<script>")) {
            html = html.replace("<script>", "<div style=\"display: none;\">");
        }
        if (html.contains("<script ")) {
            html = html.replace("<script ", "<div style=\"display: none;\" ");
        }
        if (html.contains("</script>")) {
            html = html.replace("</script>", "</div>");
        }
        return html;
    }
}
