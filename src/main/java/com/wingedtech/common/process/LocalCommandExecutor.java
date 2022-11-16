package com.wingedtech.common.process;

/**
 * LocalCommandExecutor.java
 */
public interface LocalCommandExecutor {
    ExecuteResult executeCommand(String command, long timeout);
}
