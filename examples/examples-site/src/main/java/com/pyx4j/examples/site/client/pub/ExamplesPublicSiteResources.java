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
 * Created on May 24, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client.pub;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface ExamplesPublicSiteResources extends ClientBundle {

    ExamplesPublicSiteResources INSTANCE = GWT.create(ExamplesPublicSiteResources.class);

    @Source("home.html")
    TextResource pageHome();

    @Source("contactUs.html")
    TextResource pageContact();

    @Source("aboutUs.html")
    TextResource pageAboutUs();

    @Source("pageAuthenticationRequired.html")
    TextResource pageAuthenticationRequired();

    @Source("examples.html")
    TextResource pageExamples();

}
