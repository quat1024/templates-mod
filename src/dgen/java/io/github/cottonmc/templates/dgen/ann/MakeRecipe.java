package io.github.cottonmc.templates.dgen.ann;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MakeRecipe {
	String value();
	
	@SuppressWarnings("ClassExplicitlyAnnotation")
	record Inst(String value) implements MakeRecipe {
		@Override
		public Class<? extends Annotation> annotationType() {
			return MakeRecipe.class;
		}
	}
}
