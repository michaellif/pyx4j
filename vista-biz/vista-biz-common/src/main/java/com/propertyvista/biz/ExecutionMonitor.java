/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.operations.domain.scheduler.CompletionType;
import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.ExecutionReportMessage;
import com.propertyvista.operations.domain.scheduler.ExecutionReportSection;

public class ExecutionMonitor {

    private static final Logger log = LoggerFactory.getLogger(ExecutionMonitor.class);

    private final Map<ReportSectionId, ReportSection> sections;

    private Long processedCount;

    private Long failedCount;

    private Long erredCount;

    private String message;

    private boolean dirty = false;

    public ExecutionMonitor() {
        this(0L, 0L, 0L);
    }

    public ExecutionMonitor(Long processed, Long failed, Long erred) {
        sections = new HashMap<ReportSectionId, ReportSection>();
        this.processedCount = processed == null ? 0L : processed;
        this.failedCount = failed == null ? 0L : failed;
        this.erredCount = erred == null ? 0L : erred;
    }

    private class ReportSectionId {

        private final String name;

        private final CompletionType type;

        ReportSectionId(String name, CompletionType type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31).append(name).append(type).toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }

            ReportSectionId rhs = (ReportSectionId) obj;
            return new EqualsBuilder().append(name, rhs.name).append(type, rhs.type).isEquals();
        }
    }

    public void addEvent(String sectionName, CompletionType type, BigDecimal value, String message) {
        ReportSectionId id = new ReportSectionId(sectionName, type);
        if (!sections.containsKey(id)) {
            sections.put(id, new ReportSection());
        }
        ReportSection section = sections.get(id);
        section.counter++;
        section.add(value);
        section.addMessage(message);

        switch (type) {
        case processed:
            processedCount++;
            break;
        case failed:
            failedCount++;
            break;
        case erred:
            erredCount++;
            break;
        default:
            break;
        }

        log.debug("Execution event [sectionName={} type={} value={} message={}]", sectionName, type, value, message);
        dirty = true;

    }

    public void addEvent(String sectionName, CompletionType type, String message) {
        addEvent(sectionName, type, null, message);
    }

    public void addProcessedEvent(String sectionName) {
        addEvent(sectionName, CompletionType.processed, null);
    }

    public void addProcessedEvent(String sectionName, String message) {
        addEvent(sectionName, CompletionType.processed, message);
    }

    public void addProcessedEvent(String sectionName, BigDecimal value) {
        addEvent(sectionName, CompletionType.processed, value, null);
    }

    public void addProcessedEvent(String sectionName, BigDecimal value, String message) {
        addEvent(sectionName, CompletionType.processed, value, message);
    }

    public void addFailedEvent(String sectionName, String message) {
        addEvent(sectionName, CompletionType.failed, null, message);
    }

    public void addFailedEvent(String sectionName, BigDecimal value) {
        addEvent(sectionName, CompletionType.failed, value, null);
    }

    public void addFailedEvent(String sectionName, BigDecimal value, String message) {
        addEvent(sectionName, CompletionType.failed, value, message);
    }

    public void addFailedEvent(String sectionName, Throwable throwable) {
        log.error("Event Failed", throwable);
        addFailedEvent(sectionName, throwable.toString());
    }

    public void addFailedEvent(String sectionName, String message,  Throwable throwable) {
        log.error("Event Failed {}", message, throwable);
        addFailedEvent(sectionName, message + " " + throwable.toString());
    }
    
    public void addErredEvent(String sectionName, String message) {
        addEvent(sectionName, CompletionType.erred, null, message);
    }

    public void addErredEvent(String sectionName, BigDecimal value) {
        addEvent(sectionName, CompletionType.erred, value, null);
    }

    public void addErredEvent(String sectionName, BigDecimal value, String message) {
        addEvent(sectionName, CompletionType.erred, value, message);
    }

    public void addErredEvent(String sectionName, Throwable throwable) {
        log.error("Event Erred", throwable);
        addErredEvent(sectionName, throwable.toString());
    }

    public void addErredEvent(String sectionName, BigDecimal value, Throwable throwable) {
        log.error("Event Erred", throwable);
        addErredEvent(sectionName, value, throwable.toString());
    }

    public void addErredEvent(String sectionName, BigDecimal value, String message, Throwable throwable) {
        log.error("Event Erred {}", message, throwable);
        addErredEvent(sectionName, value, message + " " + throwable.toString());
    }
    
    public void addErredEvent(String sectionName,String message, Throwable throwable) {
        log.error("Event Erred {}", message, throwable);
        addErredEvent(sectionName, null, message + " " + throwable.toString());
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTotal() {
        return processedCount + failedCount + erredCount;
    }

    public Long getProcessed() {
        return processedCount;
    }

    public Long getFailed() {
        return failedCount;
    }

    public Long getErred() {
        return erredCount;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markClean() {
        dirty = false;
    }

    boolean updateExecutionReport(ExecutionReport report, boolean full) {
        return false;
    }

    private class ReportSection {

        private BigDecimal accumulator;

        private final List<ReportMessage> messages;

        public long counter;

        ReportSection() {
            accumulator = BigDecimal.ZERO;
            messages = new ArrayList<ReportMessage>();
        }

        void add(BigDecimal augend) {
            if (augend != null) {
                this.accumulator = accumulator.add(augend);
            }
        }

        void addMessage(String message) {
            if (message != null) {
                messages.add(new ReportMessage(message));
            }
        }

        ExecutionReportSection createExecutionReportSection(ExecutionReport report) {
            return null;
        }
    }

    private class ReportMessage {

        private final String message;

        private final Date eventTime;

        public ReportMessage(String message) {
            this.message = message;
            this.eventTime = SystemDateManager.getDate();
        }

        ExecutionReportMessage createExecutionReportMessage(ExecutionReportSection report) {
            return null;
        }

    }

    public void updateExecutionReport(ExecutionReport executionReport) {
        updateExecutionReportMajorStats(executionReport);

        // TODO copy executionReport.details()  to sections
        // executionReport.details().clear();

        executionReport.message().setValue(message);

        for (Map.Entry<ReportSectionId, ReportSection> section : sections.entrySet()) {
            ExecutionReportSection executionReportSection = EntityFactory.create(ExecutionReportSection.class);

            executionReportSection.name().setValue(section.getKey().name);
            executionReportSection.type().setValue(section.getKey().type);

            executionReportSection.counter().setValue(section.getValue().counter);
            executionReportSection.value().setValue(section.getValue().accumulator);

            for (ReportMessage message : section.getValue().messages) {
                ExecutionReportMessage executionReportMessage = EntityFactory.create(ExecutionReportMessage.class);
                executionReportMessage.message().setValue(truncErrorMessage(message.message));
                executionReportMessage.eventTime().setValue(message.eventTime);
                executionReportSection.messages().add(executionReportMessage);
            }
            executionReport.details().add(executionReportSection);
        }
    }

    //TODO use @Length annotation adapter
    public static String truncErrorMessage(String errorMessage) {
        if ((errorMessage != null) && (errorMessage.length() > 4000)) {
            return errorMessage.substring(0, 4000);
        } else {
            return errorMessage;
        }
    }

    public void updateExecutionReportMajorStats(ExecutionReport executionReport) {
        executionReport.total().setValue(getTotal());
        executionReport.processed().setValue(getProcessed());
        executionReport.failed().setValue(getFailed());
        executionReport.erred().setValue(getErred());
    }

    @Override
    public String toString() {
        return "Execution Monitor: "
                + new ToStringBuilder(this).append("processedCount", processedCount).append("failedCount", failedCount).append("erredCount", erredCount);
    }
}
