/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-05-29
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.essentials.server.services.reports;

public final class ReportProgressStatus {

    public final String stage;

    public final int stageNum;

    public final int stagesCount;

    public final int stageProgress;

    public final int stageProgressMax;

    public ReportProgressStatus(String stage, int stageNum, int stagesCount, int stageProgress, int stageProgressMax) {
        this.stage = stage;
        this.stageNum = stageNum;
        this.stagesCount = stagesCount;
        this.stageProgress = stageProgress;
        this.stageProgressMax = stageProgressMax;
    }
}
