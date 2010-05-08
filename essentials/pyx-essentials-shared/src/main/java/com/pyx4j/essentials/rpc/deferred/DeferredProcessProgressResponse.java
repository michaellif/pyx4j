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
 * Created on 2010-05-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.rpc.deferred;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DeferredProcessProgressResponse implements Serializable {

    public static final int PROGRESS_MAXIMUM_NA = -1;

    public static enum ProcessStatus {

        PROCESSING,

        COMPLETED_SUCCESS,

        COMPLETED_WARN,

        CANCELED,

        ERROR;
    }

    private ProcessStatus status = ProcessStatus.PROCESSING;

    private String message;

    private int progress;

    private int progressMaximum;

    public DeferredProcessProgressResponse() {

    }

    public ProcessStatus getStatus() {
        return status;
    }

    public boolean isCompleted() {
        return status != ProcessStatus.PROCESSING;
    }

    public String getMessage() {
        return this.message;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getProgressMaximum() {
        return this.progressMaximum;
    }

    public void setCompleted() {
        if (!isCompleted()) {
            status = ProcessStatus.COMPLETED_SUCCESS;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setProgressMaximum(int progressMaximum) {
        this.progressMaximum = progressMaximum;
    }

    public boolean isError() {
        return status == ProcessStatus.ERROR;
    }

    public void setError() {
        status = ProcessStatus.ERROR;
    }

    public void setWarnStatusMessage(String message) {
        setMessage(message);
        status = ProcessStatus.COMPLETED_WARN;
    }

    public boolean isWarning() {
        return status == ProcessStatus.COMPLETED_WARN;
    }

    public boolean isCanceled() {
        return status == ProcessStatus.CANCELED;
    }

    public boolean isCompletedSuccess() {
        return status == ProcessStatus.COMPLETED_SUCCESS;
    }

    public void setCanceled() {
        status = ProcessStatus.CANCELED;
    }
}
