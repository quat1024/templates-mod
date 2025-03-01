package io.github.cottonmc.templates.dgen;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacetHolder<D extends FacetHolder<D>> {
	record Ent<A extends Annotation, T>(A ann, T item){}
	Map<Class<? extends Annotation>, List<Ent<?, ?>>> facets = new HashMap<>();
	
	public D downcast() { return (D) this; }
	
	public <A extends Annotation, T> D addFacet(A a, T facet) {
		Class<A> clzz = (Class<A>) a.annotationType();
		facets.computeIfAbsent(clzz, __ -> new ArrayList<>())
			.add(new Ent<>(a, facet));
		return downcast();
	}
	
	public <A extends Annotation, T> @Nullable Ent<A, T> getFacet(Class<? extends A> clzz) {
		if(!facets.containsKey(clzz)) return null;
		List<Ent<?, ?>> o = facets.get(clzz);
		if(o.isEmpty()) return null;
		return (Ent<A, T>) o.get(0);
	}
	
	public <A extends Annotation, T> List<Ent<A, T>> getFacets(Class<? extends A> clzz) {
		return (List<Ent<A, T>>) (Object) facets.getOrDefault(clzz, List.of());
	}
	
	public void gatherFacets() {
		try {
			
			for(Field f : this.getClass().getDeclaredFields()) {
				for(Annotation a : f.getAnnotations()) {
					if(!a.annotationType().getName().contains("templates")) continue;
					f.setAccessible(true);
					addFacet(a, f.get(this));
				}
			}
			
			for(Method m : this.getClass().getDeclaredMethods()) {
				for(Annotation a : m.getAnnotations()) {
					if(!a.annotationType().getName().contains("templates")) continue;
					m.setAccessible(true);
					addFacet(a, m.invoke(this));
				}
			}
			
		} catch (Exception e) {
			throw new RuntimeException("failed gathering facets", e);
		}
	}
}
