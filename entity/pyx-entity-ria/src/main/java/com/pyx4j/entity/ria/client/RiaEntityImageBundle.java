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
 * Created on Dec 26, 2009
 * @author Michael
 * @version $Id: RiaDemoImageBundle.java 4671 2010-01-10 08:04:15Z vlads $
 */
package com.pyx4j.entity.ria.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.pyx4j.ria.client.RiaImageBundle;

public interface RiaEntityImageBundle extends ClientBundle, RiaImageBundle {

    RiaEntityImageBundle INSTANCE = GWT.create(RiaEntityImageBundle.class);

    @Source("image.png")
    ImageResource image();

    @Source("dashboard.gif")
    ImageResource dashboard();

    @Source("report-generate.gif")
    ImageResource reportGenerate();

    @Source("report-criteria.gif")
    ImageResource reportCriteria();

    @Source("report.png")
    ImageResource report();

    @Source("search-result.png")
    ImageResource searchResult();

    @Source("search-criteria.png")
    ImageResource searchCriteria();

    @Source("search-run.png")
    ImageResource searchRun();

    @Source("print.png")
    ImageResource print();

    @Source("save.png")
    ImageResource save();

}
