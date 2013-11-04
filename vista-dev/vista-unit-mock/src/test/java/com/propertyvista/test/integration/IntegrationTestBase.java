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
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.test.integration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.test.integration.IntegrationTestBase.TaskScheduler.Schedule;
import com.propertyvista.test.mock.MockConfig;
import com.propertyvista.test.mock.MockDataModel;
import com.propertyvista.test.mock.MockManager;
import com.propertyvista.test.mock.NotificationFacadeMock;
import com.propertyvista.test.mock.schedule.SchedulerMock;

public abstract class IntegrationTestBase extends VistaDBTestBase {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTestBase.class);

    /**
     * This also includes RegressionTests
     */
    public interface FunctionalTests {
    }

    /**
     * Minimal subset of tests executed in dev env
     */
    public interface RegressionTests extends FunctionalTests {
    }

    private MockManager mockManager;

    private TaskScheduler scheduler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        NamespaceManager.setNamespace("t" + System.currentTimeMillis());
        VistaTestDBSetup.initNamespace();

        TestLifecycle.testSession(null, VistaBasicBehavior.CRM);
        TestLifecycle.testNamespace(NamespaceManager.getNamespace());
        TestLifecycle.beginRequest();

        Persistence.service().endTransaction();
        Persistence.service().startBackgroundProcessTransaction();
        setSysDate("01-Jan-2000");

        scheduler = new TaskScheduler();

        ServerSideFactory.register(NotificationFacade.class, NotificationFacadeMock.class);
    }

    @Override
    protected void tearDown() throws Exception {
        // Clear memory
        CacheService.reset();
        try {
            Persistence.service().commit();
        } finally {
            TestLifecycle.tearDown();
            SystemDateManager.resetDate();
            super.tearDown();
        }
        assertTrue("Running with Tester.continueOnError = true", !Tester.continueOnError);
    }

    public <E extends MockDataModel<?>> E getDataModel(Class<E> modelClass) {
        return mockManager.getDataModel(modelClass);
    }

    protected MockConfig createMockConfig() {
        return new MockConfig();
    }

    protected void preloadData() {
        preloadData(createMockConfig());
    }

    protected void preloadData(final MockConfig config) {

        mockManager = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<MockManager, RuntimeException>() {

            @Override
            public MockManager execute() {

                setSysDate("01-Jan-2010");

                MockManager mockManager = new MockManager(config);
                for (Class<? extends MockDataModel<?>> modelType : getMockModelTypes()) {
                    mockManager.addModel(modelType);
                }

                return mockManager;
            }
        });

    }

    protected abstract List<Class<? extends MockDataModel<?>>> getMockModelTypes();

    public static void setSysDate(Date date) {
        SystemDateManager.setDate(date);
    }

    public static void setSysDate(String dateStr) {
        setSysDate(DateUtils.detectDateformat(dateStr));
    }

    public static Date getSysDate() {
        return SystemDateManager.getDate();
    }

    protected void advanceSysDate(String dateStr) throws Exception {
        Date curDate = getSysDate();
        Date setDate = DateUtils.detectDateformat(dateStr);
        if (setDate.before(curDate)) {
            throw new Error("Can't go back in time from " + curDate.toString() + " to " + setDate.toString());
        }
        // run tasks scheduled before the set date
        Calendar calTo = GregorianCalendar.getInstance();
        calTo.setTime(setDate);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(curDate);
        scheduler.runInterval(cal, calTo);
        setSysDate(setDate);
    }

    public void assertEquals(String message, String expected, LogicalDate actual) {
        assertEquals(message, new LogicalDate(DateUtils.detectDateformat(expected)), actual);
    }

    protected void setBillingBatchProcess() {
        scheduler.schedulePmcProcess(PmcProcessType.billing, new Schedule());
        scheduler.schedulePmcProcess(PmcProcessType.initializeFutureBillingCycles, new Schedule());
    }

    protected void setDepositBatchProcess() {
        // schedule deposit interest adjustment batch process to run on 1st of each month
        scheduler.schedulePmcProcess(PmcProcessType.depositInterestAdjustment, new Schedule().set(Calendar.DAY_OF_MONTH, 1));
        // schedule deposit refund batch process to run every day
        scheduler.schedulePmcProcess(PmcProcessType.depositRefund, new Schedule());
    }

    protected void setLeaseBatchProcess() {
        // schedule lease activation and completion process to run daily
        scheduler.schedulePmcProcess(PmcProcessType.leaseActivation, new Schedule());
        scheduler.schedulePmcProcess(PmcProcessType.leaseCompletion, new Schedule());
        scheduler.schedulePmcProcess(PmcProcessType.leaseRenewal, new Schedule());
    }

    protected void setPaymentBatchProcess() {
        // schedule payment process to run daily
        scheduler.schedulePmcProcess(PmcProcessType.paymentsIssue, new Schedule());
        scheduler.schedulePmcProcess(PmcProcessType.paymentsScheduledEcheck, new Schedule());
        scheduler.schedulePmcProcess(PmcProcessType.paymentsLastMonthSuspend, new Schedule());
    }

    protected void setCaledonPadPaymentBatchProcess() {
        scheduler.schedulePmcProcess(PmcProcessType.paymentsPadSend, new Schedule());
        scheduler.schedulePmcProcess(PmcProcessType.paymentsReceiveAcknowledgment, new Schedule());
        scheduler.schedulePmcProcess(PmcProcessType.paymentsReceiveReconciliation, new Schedule());
    }

    //TODO move to SchedulerMock
    public static class TaskScheduler {

        public interface Task {
            void execute() throws Exception;
        }

        public static class Schedule {

            private final int[] fields = new int[Calendar.FIELD_COUNT];

            public Schedule set(int field, int value) {
                fields[field] = value;
                return this;
            }

            public int[] getFields() {
                return fields;
            }

            public boolean match(Calendar cal) {
                int match = 0;
                for (int field = 0; field < fields.length; field++) {
                    if (fields[field] == 0 || fields[field] == cal.get(field)) {
                        match += 1;
                    }
                }
                return (match == fields.length);
            }
        }

        private static class ScheduledTask {

            Task task;

            Schedule schedule;

            public ScheduledTask(Task task, Schedule schedule) {
                super();
                this.task = task;
                this.schedule = schedule;
            }

        }

        private final List<ScheduledTask> taskSchedule = new ArrayList<ScheduledTask>();

        protected void clearSchedule() {
            taskSchedule.clear();
        }

        protected void scheduleTask(Task task, String... dates) {
            for (String dateStr : dates) {
                Schedule entry = new Schedule();
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(DateUtils.detectDateformat(dateStr));
                entry.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
                entry.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                entry.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                taskSchedule.add(new ScheduledTask(task, entry));
            }
        }

        protected void scheduleTask(Task task, Schedule entry) {
            taskSchedule.add(new ScheduledTask(task, entry));
        }

        protected void schedulePmcProcess(final PmcProcessType triggerType, Schedule entry) {
            taskSchedule.add(new ScheduledTask(new Task() {
                @Override
                public void execute() throws Exception {
                    SchedulerMock.runProcess(triggerType, SystemDateManager.getDate());
                }
            }, entry));
        }

        protected void runInterval(Calendar calFrom, Calendar calTo) throws Exception {
            while (calFrom.before(calTo)) {
                calFrom.add(Calendar.DATE, 1);
                for (ScheduledTask entry : taskSchedule) {
                    if (entry.schedule.match(calFrom)) {
                        setSysDate(calFrom.getTime());
                        entry.task.execute();
                    }
                }
            }
        }
    }

    protected LogicalDate getDate(String date) {
        if (date == null) {
            return null;
        }
        return new LogicalDate(DateUtils.detectDateformat(date));
    }

    protected String eval(String amount) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        try {
            return engine.eval(amount).toString();
        } catch (ScriptException e) {
            throw new Error(e);
        }
    }
}
