package io.github.cottonmc.templates.gensupport;

public class Downcastable<D> {
	protected D downcast() {
		return (D) this;
	}
}
