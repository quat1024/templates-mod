package io.github.cottonmc.templates.dgen;

public class Downcastable<D> {
	protected D downcast() {
		return (D) this;
	}
}
