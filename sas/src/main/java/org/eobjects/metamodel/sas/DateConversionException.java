package org.eobjects.metamodel.sas;

public class DateConversionException extends RuntimeException {

    public DateConversionException(String msg, Object ... values) {
        super(String.format(msg, values));
    }
}
