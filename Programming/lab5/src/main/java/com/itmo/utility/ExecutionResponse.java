package com.itmo.utility;

public class ExecutionResponse {
    private boolean exitCode;
    private String message;

    public ExecutionResponse(boolean code, String m) {
        exitCode = code;
        message = m;
    }

    public ExecutionResponse(String m) {
        this(true, m);
    }

    public boolean getExitCode() {
        return exitCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.valueOf(exitCode) + ";" + message;
    }
}
