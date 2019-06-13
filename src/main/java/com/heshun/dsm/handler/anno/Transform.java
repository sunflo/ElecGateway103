package com.heshun.dsm.handler.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transform {

	DataGroup group() default DataGroup.YC;

	int index();

	String tag();

	DataType dataType() default DataType.SHT;

	int ratio() default 1;

	public enum DataType {
		SHT, INT, SHT_UNS, INT_UNS,LNG, FLT, DBLE, STR
	}

	public enum DataGroup {
		YC, YX, YM,YK,YT
	}

}
