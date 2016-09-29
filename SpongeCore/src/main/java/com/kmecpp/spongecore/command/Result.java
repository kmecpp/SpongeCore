package com.kmecpp.spongecore.command;

import org.spongepowered.api.command.CommandResult;

public class Result {

	public static final CommandResult SUCCESS = CommandResult.success();
	public static final CommandResult CHILD_LIST = CommandResult.builder().build();
	public static final CommandResult PERMISSION = CommandResult.builder().build();
	public static final CommandResult USAGE = CommandResult.builder().build();

}
