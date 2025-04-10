package io.github.cottonmc.templates.dgen;

import io.github.cottonmc.templates.gensupport.Facet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
	
	public <T> List<T> getFacets(Class<T> facetType) {
		if(!facetType.isAnnotationPresent(Facet.class)) throw new IllegalArgumentException("Not a facet type: " + facetType);
		return (List<T>) facets.getOrDefault(facetType, List.of());
	}
	
	public <T> void forEachFacet(Class<T> facetType, Consumer<? super T> action) {
		getFacets(facetType).forEach(action);
	}
	
	public FacetHolder addAll(Collection<? extends FacetHolder> others) {
		others.forEach(other -> other.facets.forEach((key, facets) -> facets.forEach(facet -> addFacetUnchecked(key, facet))));
		return this;
	}
	
//	public <T> Optional<T> getOneFacet(Class<?> facetType) {
//		List<T> list = getFacets(facetType);
//		if(list.isEmpty()) return Optional.empty();
//		if(list.size() == 1) return Optional.of(list.get(0));
//		else throw new IllegalStateException("More than one facet for " + facetType);
//	}
}
