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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.ExceptionMessagesExtractor;

import com.propertyvista.operations.domain.scheduler.CompletionType;
import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.ExecutionReportMessage;
import com.propertyvista.operations.domain.scheduler.ExecutionReportSection;

public class ExecutionMonitor {

    private static final Logger log = LoggerFactory.getLogger(ExecutionMonitor.class);

    private final Map<ReportSectionId, ReportSection> sections;

    private final Map<String, Boolean> excludedSectionsFromTotals;

    private Long processedCount;

    private Long failedCount;

    private Long erredCount;

    private Long expectedTotal;

    private String message;

    private boolean terminationRequested = false;

    private boolean dirty = false;

    public ExecutionMonitor() {
        this(0L, 0L, 0L);
    }

    public ExecutionMonitor(Long processed, Long failed, Long erred) {
        sections = new HashMap<ReportSectionId, ReportSection>();
        excludedSectionsFromTotals = new HashMap<String, Boolean>();
        this.expectedTotal = 0L;
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

    /**
     * Excluded Section Counter From Totals
     */
    public void setExcludedSectionsFromTotals(String sectionName, boolean include) {
        excludedSectionsFromTotals.put(sectionName, include);
    }

    public void requestTermination() {
        terminationRequested = true;
    }

    public boolean isTerminationRequested() {
        return terminationRequested;
    }

    public void setExpectedTotal(Long expectedTotal) {
        this.expectedTotal = expectedTotal;
    }

    public static class ExecutionEvent {
        public final String sectionName;

        public final CompletionType type;

        public final BigDecimal value;

        public final String message;

        public ExecutionEvent(String sectionName, CompletionType type, BigDecimal value, String message) {
            this.sectionName = sectionName;
            this.type = type;
            this.value = value;
            this.message = message;
        }
    }

    public void addEvent(String sectionName, CompletionType type, BigDecimal value, String message) {
        ReportSectionId id = new ReportSectionId(sectionName, type);
        ReportSection section = sections.get(id);
        if (section == null) {
            sections.put(id, section = new ReportSection());
        }

        section.counter++;
        section.add(value);
        section.addMessage(message);

        Boolean excluded = excludedSectionsFromTotals.get(sectionName);
        if (excluded == null) {
            excluded = false;
        }

        if (!excluded) {
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
        }

        log.debug("Execution event [sectionName={} type={} value={} message={}]", sectionName, type, value, message);
        dirty = true;
        onEventAdded(new ExecutionEvent(sectionName, type, value, message));
    }

    //TODO unify RunningProcess  and ExecutionMonitor
    protected void onEventAdded(ExecutionEvent event) {
    }

    public List<String> getSectionNames(CompletionType type) {
        ArrayList<String> names = new ArrayList<>();
        for (ReportSectionId id : sections.keySet()) {
            if (id.type.equals(type)) {
                names.add(id.name);
            }
        }
        return names;
    }

    public BigDecimal getValue(String sectionName, CompletionType type) {
        ReportSectionId id = new ReportSectionId(sectionName, type);
        ReportSection section = sections.get(id);
        if (section != null) {
            return section.accumulator;
        } else {
            return null;
        }
    }

    public Long getCounter(String sectionName, CompletionType type) {
        ReportSectionId id = new ReportSectionId(sectionName, type);
        ReportSection section = sections.get(id);
        if (section != null) {
            return section.counter;
        } else {
            return null;
        }
    }

    public long getTotalCounter(String sectionName) {
        long total = 0;
        for (CompletionType type : CompletionType.values()) {
            Long counter = getCounter(sectionName, type);
            total += (counter == null ? 0 : counter);
        }
        return total;
    }

    public void setExpectedTotal(String sectionName, long total) {
        ReportSectionId id = new ReportSectionId(sectionName, CompletionType.processed);
        ReportSection section = sections.get(id);
        if (section == null) {
            sections.put(id, section = new ReportSection());
            section.expectedTotal = new IterationProgressCounter(1, total);
        }
        section.expectedTotal.onIterationCompleted(total);
    }

    public Long getExpectedTotal(String sectionName) {
        ReportSectionId id = new ReportSectionId(sectionName, CompletionType.processed);
        ReportSection section = sections.get(id);
        return section != null ? section.expectedTotal.getExpectedTotal() : null;
    }

    public IterationProgressCounter getIterationProgressCounter(String sectionName) {
        ReportSectionId id = new ReportSectionId(sectionName, CompletionType.processed);
        ReportSection section = sections.get(id);
        return (section == null || section.expectedTotal == null) ? null : section.expectedTotal;
    }

    public void setIterationProgressCounter(String sectionName, IterationProgressCounter counter) {
        ReportSectionId id = new ReportSectionId(sectionName, CompletionType.processed);
        ReportSection section = sections.get(id);
        if (section == null) {
            sections.put(id, section = new ReportSection());
        }
        section.expectedTotal = counter;
    }

    public void addEvent(String sectionName, CompletionType type, String message) {
        addEvent(sectionName, type, null, message);
    }

    public void addInfoEvent(String sectionName, String message) {
        setExcludedSectionsFromTotals(sectionName, true);
        addEvent(sectionName, CompletionType.processed, message);
    }

    public void addInfoEvent(String sectionName, String message, BigDecimal value) {
        setExcludedSectionsFromTotals(sectionName, true);
        addEvent(sectionName, CompletionType.processed, value, message);
    }

    public void addInfoEvent(String sectionName, CompletionType type, String message, BigDecimal value) {
        setExcludedSectionsFromTotals(sectionName, true);
        addEvent(sectionName, type, value, message);
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
        addFailedEvent(sectionName, ExceptionMessagesExtractor.getAllMessages(throwable));
    }

    public void addFailedEvent(String sectionName, String message, Throwable throwable) {
        log.error("Event Failed {}", message, throwable);
        addFailedEvent(sectionName, message + " " + ExceptionMessagesExtractor.getAllMessages(throwable));
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
        addErredEvent(sectionName, value, ExceptionMessagesExtractor.getAllMessages(throwable));
    }

    public void addErredEvent(String sectionName, BigDecimal value, String message, Throwable throwable) {
        log.error("Event Erred {}", message, throwable);
        addErredEvent(sectionName, value, message + "\n" + ExceptionMessagesExtractor.getAllMessages(throwable));
    }

    public void addErredEvent(String sectionName, String message, Throwable throwable) {
        log.error("Event Erred {}", message, throwable);
        addErredEvent(sectionName, null, message + "\n" + ExceptionMessagesExtractor.getAllMessages(throwable));
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getExpectedTotal() {
        return expectedTotal;
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

        long counter;

        IterationProgressCounter expectedTotal;

        ExecutionReportSection executionReportSection;

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

        private void add(ReportSection otherSection) {
            counter += otherSection.counter;
            add(otherSection.accumulator);
            messages.addAll(otherSection.messages);
            Collections.sort(messages);
        }
    }

    private class ReportMessage implements Comparable<ReportMessage> {

        private final String message;

        private final Date eventTime;

        public ReportMessage(String message) {
            this.message = message;
            this.eventTime = SystemDateManager.getDate();
        }

        ExecutionReportMessage createExecutionReportMessage(ExecutionReportSection report) {
            return null;
        }

        /**
         * Ascending order of event time
         */
        @Override
        public int compareTo(ReportMessage o) {
            return o.eventTime.compareTo(eventTime);
        }

    }

    public void add(ExecutionMonitor other) {
        this.dirty = true;
        processedCount += other.processedCount;
        failedCount += other.failedCount;
        erredCount += other.erredCount;

        for (Map.Entry<ReportSectionId, ReportSection> otherSection : other.sections.entrySet()) {
            ReportSection section = sections.get(otherSection.getKey());
            if (section == null) {
                sections.put(otherSection.getKey(), otherSection.getValue());
            } else {
                section.add(otherSection.getValue());
            }
        }
    }

    public void updateExecutionReport(ExecutionReport dbReport) {
        updateExecutionReportMajorStats(dbReport);

        //  copy executionReport.details()  to sections
        for (ExecutionReportSection dbReportSection : dbReport.details()) {
            ReportSectionId id = new ReportSectionId(dbReportSection.name().getValue(), dbReportSection.type().getValue());
            ReportSection section = sections.get(id);
            if (section == null) {
                sections.put(id, section = new ReportSection());
            }
            section.executionReportSection = dbReportSection;
            section.counter += dbReportSection.counter().getValue();
            if (section.accumulator != null) {
                if (!dbReportSection.value().isNull()) {
                    section.accumulator = section.accumulator.add(dbReportSection.value().getValue());
                }
            } else {
                section.accumulator = dbReportSection.value().getValue();
            }
        }

        dbReport.message().setValue(message);

        for (Map.Entry<ReportSectionId, ReportSection> section : sections.entrySet()) {
            ExecutionReportSection executionReportSection = section.getValue().executionReportSection;
            if (executionReportSection == null) {
                executionReportSection = EntityFactory.create(ExecutionReportSection.class);
                dbReport.details().add(executionReportSection);
            }

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
        }
    }

    public String getTextMessages() {
        return getTextMessages((CompletionType[]) null);
    }

    public String getTextMessages(CompletionType... types) {
        StringBuilder textMessage = new StringBuilder();

        Set<CompletionType> filter = new HashSet<>();
        if (types != null) {
            filter.addAll(Arrays.asList(types));
        }

        for (Map.Entry<ReportSectionId, ReportSection> section : sections.entrySet()) {
            if (types != null && !filter.contains(section.getKey().type)) {
                continue;
            }
            for (ReportMessage message : section.getValue().messages) {
                if (textMessage.length() > 0) {
                    textMessage.append("\n");
                }
                textMessage.append(section.getKey().name).append(": ").append(message.message);
            }
        }
        return textMessage.toString();
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
                + new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("processedCount", processedCount).append("failedCount", failedCount)
                        .append("erredCount", erredCount);
    }

    /** Allows to adjust expected total based on the actual iteration total known upon entering each iteration */
    public static class IterationProgressCounter {
        private final long estimated;

        private long expectedTotal;

        public IterationProgressCounter(long iterations, long estimated) {
            this.estimated = estimated;
            expectedTotal = iterations * estimated;
        }

        public void onIterationCompleted(long actual) {
            long newTotal = expectedTotal - estimated + actual;
            // update when new total is lower, as we can only advance progress bar forward
            if (newTotal < expectedTotal) {
                expectedTotal = newTotal;
            }
        }

        public long getExpectedTotal() {
            return expectedTotal;
        }
    }
}
