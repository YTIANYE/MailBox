package com.example.mailbox.controller;


import java.io.IOError;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.example.mailbox.controller.MessagingControllerCommands.PendingAppend;
import com.example.mailbox.controller.MessagingControllerCommands.PendingCommand;
import com.example.mailbox.controller.MessagingControllerCommands.PendingDelete;
import com.example.mailbox.controller.MessagingControllerCommands.PendingEmptyTrash;
import com.example.mailbox.controller.MessagingControllerCommands.PendingExpunge;
import com.example.mailbox.controller.MessagingControllerCommands.PendingMarkAllAsRead;
import com.example.mailbox.controller.MessagingControllerCommands.PendingMoveOrCopy;
import com.example.mailbox.controller.MessagingControllerCommands.PendingSetFlag;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;


public class PendingCommandSerializer {
    private static final PendingCommandSerializer INSTANCE = new PendingCommandSerializer();


    private final Map<String, JsonAdapter<? extends PendingCommand>> adapters;


    private PendingCommandSerializer() {
        Moshi moshi = new Moshi.Builder().build();
        HashMap<String, JsonAdapter<? extends PendingCommand>> adapters = new HashMap<>();

        adapters.put(MessagingControllerCommands.COMMAND_MOVE_OR_COPY, moshi.adapter(PendingMoveOrCopy.class));
        adapters.put(MessagingControllerCommands.COMMAND_APPEND, moshi.adapter(PendingAppend.class));
        adapters.put(MessagingControllerCommands.COMMAND_EMPTY_TRASH, moshi.adapter(PendingEmptyTrash.class));
        adapters.put(MessagingControllerCommands.COMMAND_EXPUNGE, moshi.adapter(PendingExpunge.class));
        adapters.put(MessagingControllerCommands.COMMAND_MARK_ALL_AS_READ, moshi.adapter(PendingMarkAllAsRead.class));
        adapters.put(MessagingControllerCommands.COMMAND_SET_FLAG, moshi.adapter(PendingSetFlag.class));
        adapters.put(MessagingControllerCommands.COMMAND_DELETE, moshi.adapter(PendingDelete.class));

        this.adapters = Collections.unmodifiableMap(adapters);
    }


    public static PendingCommandSerializer getInstance() {
        return INSTANCE;
    }


    public <T extends PendingCommand> String serialize(T command) {
        // noinspection unchecked, we know the map has correctly matching adapters
        JsonAdapter<T> adapter = (JsonAdapter<T>) adapters.get(command.getCommandName());
        if (adapter == null) {
            throw new IllegalArgumentException("Unsupported pending command type!");
        }
        return adapter.toJson(command);
    }

    public PendingCommand unserialize(long databaseId, String commandName, String data) {
        JsonAdapter<? extends PendingCommand> adapter = adapters.get(commandName);
        if (adapter == null) {
            throw new IllegalArgumentException("Unsupported pending command type!");
        }
        try {
            PendingCommand command = adapter.fromJson(data);
            command.databaseId = databaseId;
            return command;
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
