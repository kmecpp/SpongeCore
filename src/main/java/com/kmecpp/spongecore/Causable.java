package com.kmecpp.spongecore;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;

public interface Causable {

	default Object getCause() {
		return this;
	}

	default Cause asCause() {
		return asCause(NamedCause.SOURCE);
	}

	default Cause asCause(String name) {
		return Cause.of(asNamedCause(name));
	}

	default NamedCause asNamedCause() {
		return asNamedCause(NamedCause.SOURCE);
	}

	default NamedCause asNamedCause(String name) {
		return NamedCause.of(name, getCause());
	}

	default Cause asCause(NamedCause... causes) {
		return Cause.of(asNamedCause(), (NamedCause[]) causes);
	}

}
