package com.pyx4j.workflow.attempt1a.impl;

import com.pyx4j.workflow.attempt1a.State;

public class StateImpl extends ConnectorImpl implements State {

    @Override
    public boolean isReady() {
        return hasToken();
    }

}
