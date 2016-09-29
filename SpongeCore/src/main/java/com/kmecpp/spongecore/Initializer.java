package com.kmecpp.spongecore;

public interface Initializer {

	void preInit();

	void init();

	void postInit();

	public static Initializer DEFAULT = new Initializer() {

		@Override
		public void preInit() {
		}

		@Override
		public void postInit() {
		}

		@Override
		public void init() {
		}

	};

}
