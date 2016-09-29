package com.kmecpp.spongecore.config.annotation;

public class ConfigKey {

	private final String name;
	private final Configurable data;

	public ConfigKey(String name, Configurable data) {
		this.name = name;
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public Configurable getData() {
		return data;
	}

}
