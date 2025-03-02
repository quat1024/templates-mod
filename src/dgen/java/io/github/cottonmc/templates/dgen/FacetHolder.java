package io.github.cottonmc.templates.dgen;

import io.github.cottonmc.templates.dgen.ann.Facet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacetHolder {
	public Map<Class<?>, List<Object>> facets = new HashMap<>();
	
	//find "root" class with @Facet annotation
	protected Class<?> getFacetKey(Class<?> c) {
		if(c == null) return null;
		else if(c.isAnnotationPresent(Facet.class)) return c;
		else return getFacetKey(c.getSuperclass());
	}
	
	public <T> T addFacet(T facet) {
		Class<?> facetKey = getFacetKey(facet.getClass());
		if(facetKey == null) throw new IllegalArgumentException("Not a facet type: " + facet.getClass());
		addFacetUnchecked(facetKey, facet);
		return facet;
	}
	
	protected void addFacetUnchecked(Class<?> facetKey, Object facet) {
		facets.computeIfAbsent(facetKey, __ -> new ArrayList<>(2)).add(facet);
	}
	
	public <T> List<T> getFacets(Class<?> facetType) {
		if(!facetType.isAnnotationPresent(Facet.class)) throw new IllegalArgumentException("Not a facet type: " + facetType);
		return (List<T>) facets.getOrDefault(facetType, List.of());
	}
	
	public void gatherFacets() {
		try {
			for(Field field : this.getClass().getDeclaredFields()) {
				try {
					field.setAccessible(true);
				} catch (Exception e) {
					continue;
				}
				
				if(field.getType().isPrimitive()) continue;
				if(Modifier.isTransient(field.getModifiers())) continue;
				
				Class<?> facetKey = getFacetKey(field.getType());
				if(facetKey == null) continue;
				
				Object facet = field.get(this);
				
				addFacetUnchecked(facetKey, facet);
			}
			
			//todo methods
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
//	public <T> Optional<T> getOneFacet(Class<?> facetType) {
//		List<T> list = getFacets(facetType);
//		if(list.isEmpty()) return Optional.empty();
//		if(list.size() == 1) return Optional.of(list.get(0));
//		else throw new IllegalStateException("More than one facet for " + facetType);
//	}
}
