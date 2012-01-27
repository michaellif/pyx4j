package com.pyx4j.workflow.attempt1a.impl;

import java.util.Iterator;
import java.util.Map;

import com.pyx4j.workflow.attempt1a.Connector;
import com.pyx4j.workflow.attempt1a.Token;

public class ConnectorImpl implements Connector {

    private Map<Connector, Token> dstList;

    private Map<Connector, Token> srcList;

    private final SwitchType srcSwitch = SwitchType.OR;

    private final SwitchType dstSwitch = SwitchType.AND;

    private final Token token = null;

    @Override
    public void connectTo(Connector dst) {
        dstList.put(dst, null);
        dst.acceptFrom(this);
    }

    @Override
    public void acceptFrom(Connector src) {
        srcList.put(src, null);
    }

    @Override
    public void putToken(Connector src, Token token) {
        if (srcList.containsKey(src)) {
            srcList.put(src, token);
            // now evaluate value according to srcSwitch type:
            // OR-join - any input value sets connector value
            // AND-join - all input values must be set before we set connector value
            Iterator<Token> input = srcList.values().iterator();
            boolean hasToken = SwitchType.AND.equals(srcSwitch);
            while (input.hasNext()) {
                if ((input.next() == null) == hasToken) {
                    hasToken = !hasToken;
                    break;
                }
            }
            if (hasToken) {
                token = new Token();
                for (Connector dst : dstList.keySet()) {
                    dstList.put(dst, token);
                }
            } else {
                token = null;
            }
        }
    }

    @Override
    public Token getToken(Connector dst) {
        Token out = null;
        if (dstList.containsKey(dst)) {
            if ((out = dstList.get(dst)) != null) {
                // got token - check split-type
                if (SwitchType.OR.equals(dstSwitch)) {
                    // OR-split - return value and fully clear out-register
                    for (Connector c : dstList.keySet()) {
                        dstList.put(c, null);
                    }
                } else {
                    // AND-split - only clear out-register for the given connector
                    dstList.put(dst, null);
                }
            }
        }
        return out;
    }

    @Override
    public boolean hasToken() {
        return (token != null);
    }
}
