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
 * Created on Sep 5, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.rpc;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18n.I18nStrategy;
import com.pyx4j.site.shared.domain.reports.ReportMetadata;

@I18n(strategy = I18nStrategy.IgnoreAll)
public class ReportsAppPlace extends AppPlace {

    private final static String REPORT_TYPE_ARG_NAME = "type";

    private final ReportMetadata metadata;

    public ReportsAppPlace() {
        this(null);
    }

    public ReportsAppPlace(ReportMetadata metadata) {
        this.metadata = metadata;
        if (metadata != null) {
            this.queryArg(REPORT_TYPE_ARG_NAME, GWTJava5Helper.getSimpleName(metadata.getInstanceValueClass()));
        }
    }

    public ReportMetadata getReportMetadata() {
        return metadata;
    }

    public String getReportMetadataName() {
        return this.getFirstArg(REPORT_TYPE_ARG_NAME);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

}
