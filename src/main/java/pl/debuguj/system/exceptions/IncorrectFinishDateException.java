package pl.debuguj.system.exceptions;

import java.time.LocalDateTime;

public class IncorrectFinishDateException extends RuntimeException {
    public IncorrectFinishDateException(final LocalDateTime start, LocalDateTime finish) {
        super("Finish date: " + finish.toString() + " is before start date: " + start.toString());
    }
}