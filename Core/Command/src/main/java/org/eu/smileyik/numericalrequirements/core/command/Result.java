package org.eu.smileyik.numericalrequirements.core.command;

public class Result {
    public static final Result RESULT_WRONG_COMMAND = new Result(ExecuteResult.CommandError);
    public static final Result RESULT_NOT_FIND = new Result(ExecuteResult.NotFound);
    public static final Result RESULT_SUCCEED = new Result(ExecuteResult.Succeed);
    public static final Result RESULT_MULTI_TARGET = new Result(ExecuteResult.MultiTarget);
    public static final Result RESULT_NOT_PLAYER = new Result(ExecuteResult.NotPlayer);
    public static final Result RESULT_NO_PERMISSION = new Result(ExecuteResult.NoPermission);


    private final ExecuteResult result;
    private final CommandMethod suggestion;

    public Result(ExecuteResult result, CommandMethod suggestion) {
        this.result = result;
        this.suggestion = suggestion;
    }

    public Result(ExecuteResult result) {
        this.result = result;
        this.suggestion = null;
    }

    public ExecuteResult getResult() {
        return result;
    }

    public CommandMethod getSuggestion() {
        return suggestion;
    }
}
