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
 * Created on 2012-06-02
 * @author vlads
 * @version $Id$
 */
package ch.qos.logback.core.rolling;

import java.io.File;

import ch.qos.logback.core.joran.spi.NoAutoStart;

@NoAutoStart
public class PyxExtensionSizeAndTimeBasedFNATP<E> extends ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP<E> {

    private boolean fistEvenFired = false;

    private boolean rolloverOnStart;

    @Override
    public void start() {
        super.start();
        fistEvenFired = false;
    }

    public void setRolloverOnStart(boolean rolloverOnStart) {
        this.rolloverOnStart = rolloverOnStart;
    }

    public boolean isRolloverOnStart() {
        return rolloverOnStart;
    }

    @Override
    public boolean isTriggeringEvent(File activeFile, final E event) {
        boolean trigger = super.isTriggeringEvent(activeFile, event);
        if (!fistEvenFired && isRolloverOnStart() && activeFile.exists() && (activeFile.length() > 0)) {
            fistEvenFired = true;
            elapsedPeriodsFileName = tbrp.fileNamePatternWCS.convertMultipleArguments(dateInCurrentPeriod, currentPeriodsCounter);
            currentPeriodsCounter++;
            return true;
        } else {
            fistEvenFired = true;
            return trigger;
        }
    }
}
