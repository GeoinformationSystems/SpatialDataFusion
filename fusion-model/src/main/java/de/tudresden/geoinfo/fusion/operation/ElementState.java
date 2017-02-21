package de.tudresden.geoinfo.fusion.operation;

/**
 * state of a workflow element
 */
public enum ElementState {

    /**
     * initial state
     */
    INITIALIZED(100),

    /**
     * configuration state
     */
    CONFIGURED(200),

    /**
     * ready state
     */
    READY(300),

    /**
     * success state
     */
    SUCCESS(400),

    /**
     * error states
     */
    ERROR(500),
    IMPROPER_CONFIGURATION(510),
    RUNTIME_EXCEPTION(520),
    RUNTIME_ERROR(530);

    private final int value;

    /**
     * constructor
     *
     * @param value element state value
     */
    ElementState(int value) {
        this.value = value;
    }

    /**
     * get value associated with the state
     *
     * @return state value
     */
    public int getValue() {
        return this.value;
    }

}
