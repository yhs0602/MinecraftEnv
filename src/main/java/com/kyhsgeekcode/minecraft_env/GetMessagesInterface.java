package com.kyhsgeekcode.minecraft_env;

import java.util.ArrayList;
import java.util.List;

public interface GetMessagesInterface {
    ArrayList<String> lastDeathMessage = new ArrayList<>();

    default List<String> getLastDeathMessage() {
        return lastDeathMessage;
    }
}