package io.github.cottonmc.templates.dgen.ann;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MakeLootTable {
	
	
	@SuppressWarnings("ClassExplicitlyAnnotation")
	record Inst() implements MakeLootTable {
		@Override
		public Class<? extends Annotation> annotationType() {
			return MakeLootTable.class;
		}
	}
}
