package pl.debuguj.system.exceptions;

import java.util.Date;

public class IncorrectFinishDateException extends RuntimeException {
    public IncorrectFinishDateException(final Date start, Date finish) {
        super("Finish date: " + finish.toString() + " is before start date: " + finish.toString());
    }
}