package com.pyx4j.workflow.attempt1a.impl;

import java.util.Iterator;
import java.util.Map;

import com.pyx4j.workflow.attempt1a.Connector;

public class ConnectorImpl implements Connector {

    private Map<Connector, Boolean> dstList;

    private Map<Connector, Boolean> srcList;

    private final SwitchType srcSwitch = SwitchType.OR;

    private final SwitchType dstSwitch = SwitchType.AND;

    private boolean hasValue = false;

    @Override
    public void connectTo(Connector dst) {
        dstList.put(dst, false);
        dst.acceptFrom(this);
    }

    @Override
    public void acceptFrom(Connector src) {
        srcList.put(src, false);
    }

    @Override
    public void putValue(Connector src) {
        if (srcList.containsKey(src)) {
            srcList.put(src, true);
            // now evaluate value according to srcSwitch type:
            // OR-join - any input value sets connector value
            // AND-join - all input values must be set before we set connector value
            Iterator<Boolean> input = srcList.values().iterator();
            hasValue = SwitchType.AND.equals(srcSwitch);
            while (input.hasNext()) {
                if (input.next() != hasValue) {
                    hasValue = !hasValue;
                    break;
                }
            }
        }
    }

    @Override
    public boolean getValue(Connector dst) {
        if (dstList.containsKey(dst)) {
            // OR-split - return value and fully clear out-register
            // AND-split - only clear out-register for the given connector
        }
        return hasValue;
    }

    @Override
    public boolean clearValue() {
        if (hasValue) {
            hasValue = false;
            return true;
        } else {
            return false;
        }
    }

}
