package com.avaya.android.vantage.aaadevbroadcast.model;

/**
 * {@link UICallState} enum containing posible states of {@link UICall}
 */

public enum UICallState {
    NOT_RELEVANT,
    IDLE,
    REMOTE_ALERTING,
    ESTABLISHED,
    HELD,
    TRANSFERRING,
    ENDED,
    FAILED
}
