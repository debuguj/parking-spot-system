package pl.debuguj.system.exceptions;

import java.time.LocalDateTime;

public class IncorrectFinishDateException extends RuntimeException {
    public IncorrectFinishDateException(final LocalDateTime start, LocalDateTime finish) {
        super("Finish date: " + start.toString() + " is before start date: " + finish.toString());
    }
}