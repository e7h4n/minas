package com.jinyufeili.minas.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by pw on 6/10/16.
 */
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class PreconditionFailedException extends RuntimeException {

    public PreconditionFailedException() {
        super();
    }

    public PreconditionFailedException(String message) {
        super(message);
    }
}
