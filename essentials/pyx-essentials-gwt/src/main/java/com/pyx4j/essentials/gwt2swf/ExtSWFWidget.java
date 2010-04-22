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
 * Created on 2010-04-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.gwt2swf;

import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

public class ExtSWFWidget extends SWFWidget {

    public ExtSWFWidget(String src, int width, int height) {
        super(src, width, height);
    }

    @Override
    public String getSwfId() {
        return super.getSwfId();
    }

    protected void allowScriptAccess() {
        this.addParam("allowScriptAccess", "always");
    }

    public void allowFullscreen() {
        this.addParam("allowfullscreen", "true");
    }

}
