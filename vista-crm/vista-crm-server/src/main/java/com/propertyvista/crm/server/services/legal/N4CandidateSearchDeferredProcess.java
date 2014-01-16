/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.legal;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.crm.rpc.dto.legal.n4.LegalNoticeCandidateDTO;
import com.propertyvista.crm.rpc.dto.legal.n4.N4CandidateSearchCriteriaDTO;

public class N4CandidateSearchDeferredProcess extends AbstractDeferredProcess {

    public static final String SEARCH_RESULTS_KEY = "N4Cadidates";

    private static final Logger log = LoggerFactory.getLogger(N4CandidateSearchDeferredProcess.class);

    private static final long serialVersionUID = 1L;

    private volatile Throwable error;

    private final ExecutionMonitor monitor;

    private final N4CandidateSearcher searcher;

    public N4CandidateSearchDeferredProcess(N4CandidateSearchCriteriaDTO searchCriteria) {
        this.monitor = new ExecutionMonitor();
        this.searcher = new N4CandidateSearcher(searchCriteria, this.monitor);
    }

    @Override
    public void execute() {
        try {
            Vector<LegalNoticeCandidateDTO> candidates = new UnitOfWork(TransactionScopeOption.RequiresNew, ConnectionTarget.TransactionProcessing)
                    .execute(new Executable<Vector<LegalNoticeCandidateDTO>, RuntimeException>() {
                        @Override
                        public Vector<LegalNoticeCandidateDTO> execute() {
                            searcher.searchForCandidates();
                            return searcher.legalNoticeCandidates();
                        }
                    });
            Context.getVisit().setAttribute(SEARCH_RESULTS_KEY, candidates);
        } catch (Throwable caught) {
            log.error("got error while searching for N4 candidates", caught);
            error = caught;
        } finally {
            completed = true;
        }
    }

    @Override
    public void cancel() {
        this.monitor.requestTermination();
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse status = new DeferredProcessProgressResponse();
        if (this.monitor.isTerminationRequested()) {
            status.setCanceled();
        } else if (completed) {
            if (error != null) {
                status.setError();
                status.setErrorStatusMessage(error.getMessage());
            }
            status.setCompleted();

        } else {
            status.setProgress(this.monitor.getProcessed().intValue());
            status.setProgressMaximum(this.monitor.getExpectedTotal().intValue());
        }
        return status;
    }

}
