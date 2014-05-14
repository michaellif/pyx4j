package com.pyx4j.forms.client.ui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class AsyncLoadingHandler implements AsyncCallback<List<?>> {

    private static final Logger log = LoggerFactory.getLogger(AsyncLoadingHandler.class);

    public enum Status {
        Loading, Complete, Cancelled, Failed
    }

    private Status status;

    private final AsyncCallback<List<?>> handlingCallback;

    public AsyncLoadingHandler(AsyncCallback<List<?>> callback) {
        handlingCallback = callback;
        status = Status.Loading;
    }

    public AsyncCallback<List<?>> getHandlingCallback() {
        return handlingCallback;
    }

    public boolean isStatus(Status status) {
        return this.status == status;
    }

    public void cancel() {
        status = Status.Cancelled;
    }

    @Override
    public void onFailure(Throwable caught) {
        if (isStatus(Status.Cancelled)) {
            return;
        }
        status = Status.Failed;
        log.error("Loading failed: {}", caught);
        handlingCallback.onFailure(caught);
    }

    @Override
    public void onSuccess(List<?> result) {
        if (isStatus(Status.Cancelled)) {
            return;
        }
        status = Status.Complete;
        handlingCallback.onSuccess(result);
    }

}
