package com.pyx4j.workflow.attempt1;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link WorkflowManager} registers {@link WorkflowCase}s and makes them available to start and receive {@link WorkflowEvent}s.
 */
public class WorkflowManager {
    private static List<WorkflowCase> cases = new ArrayList<WorkflowCase>();

    private static List<WorkflowEventHandler> evtHandlers = new ArrayList<WorkflowEventHandler>();

    // register case
    public void registerCase(WorkflowCase wfCase) {
        cases.add(wfCase);
    }

    // start and register if not
    public void startCase(WorkflowCase wfCase) {

    }

    public boolean isRegistered(WorkflowCase wfCase) {
        return false;
    }

    public boolean isStarted(WorkflowCase wfCase) {
        return false;
    }

    public static void addWorkflowEventHandler(WorkflowEventHandler handler) {
        evtHandlers.add(handler);
    }

    public static void fireEvent(WorkflowEvent event) {
        for (WorkflowEventHandler handler : evtHandlers) {
            handler.handleEvent(event);
        }
    }
}
