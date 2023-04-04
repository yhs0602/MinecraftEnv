package com.kyhsgeekcode.minecraft_env;

class ActionSpace {
    private int[] action;
    private String command;

    public ActionSpace(int[] action, String command) {
        this.action = action;
        this.command = command;
    }

    public int[] getAction() {
        return action;
    }

    public String getCommand() {
        return command;
    }
}