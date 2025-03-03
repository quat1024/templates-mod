package io.github.cottonmc.templates.dgen;

import io.github.cottonmc.templates.gensupport.Id;

/** Something with an id. */
public class Idable<D> extends Downcastable<D> {
	public Id id;
	
	public D id(Id id) {
		this.id = id;
		return downcast();
	}
	
	//convenience
	public D id(String id) {
		this.id = new Id(id);
		return downcast();
	}
	
	public String ns() {
		return id.ns;
	}
	
	public String path() {
		return id.path;
	}
}
