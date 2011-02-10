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
 * Created on 2011-02-09
 * @author jim
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.site.client.place;

public class AppPlaceInfo {

    private final String navigLabel;

    private final String caption;

    private final String staticContent;

    public String getNavigLabel() {
        return navigLabel;
    }

    public String getCaption() {
        return caption;
    }

    public String getResource() {
        return staticContent;
    }

    public AppPlaceInfo(String navigLabel, String caption, String staticContent) {
        this.navigLabel = navigLabel != null && !navigLabel.equals("") ? navigLabel : null;
        this.caption = caption != null && !caption.equals("") ? caption : null;
        this.staticContent = staticContent != null && !staticContent.equals("") ? staticContent : null;
    }
}