package com.razerdp.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Created by razerdp on 2019/5/30
 */
public class Logger {
    private Messager msg;

    public Logger(Messager messager) {
        msg = messager;
    }

    public void i(CharSequence info) {
        if (Utils.noEmpty(info)) {
            msg.printMessage(Diagnostic.Kind.NOTE, info);
        }
    }

    public void e(CharSequence error) {
        if (Utils.noEmpty(error)) {
            msg.printMessage(Diagnostic.Kind.ERROR, "An exception is encountered, [" + error + "]");
        }
    }

    public void e(Throwable error) {
        if (null != error) {
            msg.printMessage(Diagnostic.Kind.ERROR, "An exception is encountered, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()));
        }
    }

    public void w(CharSequence warning) {
        if (Utils.noEmpty(warning)) {
            msg.printMessage(Diagnostic.Kind.WARNING, warning);
        }
    }


    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
