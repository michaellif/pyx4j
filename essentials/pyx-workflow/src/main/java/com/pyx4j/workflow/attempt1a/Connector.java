package com.pyx4j.workflow.attempt1a;

/**
 * {@link Connector} allows to connect {@link State}s and {@link Transition}s and pass values (Tokens)
 * to each other. {@link Connector}s in the Workflow management are unidirectional.
 * 
 */
public interface Connector {

    enum SwitchType {
        AND, OR
    }

    void connectTo(Connector dst);

    void acceptFrom(Connector src);

    void putToken(Connector src, Token token);

    Token getToken(Connector dst);

    boolean hasToken();
}
