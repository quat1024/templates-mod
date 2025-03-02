package io.github.cottonmc.templates.dgen;

/** Something with an id. */
public class Idable<D> extends Downcastable<D> {
	public String id;
	
	public D id(String id) {
		this.id = id;
		return downcast();
	}
	
	//getter
	public String id() {
		return namespace() + ":" + path();
	}
	
	public String namespace() {
		if(id.indexOf(':') == -1) return "templates";
		else return id.split(":")[0];
	}
	
	public String path() {
		if(id.indexOf(':') == -1) return id;
		else return id.split(":")[1];
	}
}
