package com.epam.esm.exceptions;

/**
 * Class contains custom error codes.
 */
public class DaoExceptionCodes {

    private DaoExceptionCodes() {}

    public static final String NO_ENTITY = "404000";
    public static final String NO_ENTITY_WITH_ID = "404001";
    public static final String NO_ENTITY_WITH_PARAMETERS = "404002";
    public static final String NO_ENTITY_WITH_NAME = "404003";
    public static final String SAVING_ERROR = "404004";
}
