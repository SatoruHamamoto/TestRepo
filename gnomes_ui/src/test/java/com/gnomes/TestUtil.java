package com.gnomes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

/**
 * テストユーティリティ
 *
 * @author YJP/kei
 */
public class TestUtil {

	/**
	 * クラスを指定してBeanオブジェクトを生成する。
	 *
	 * @param <T>   型パラメータ
	 * @param clazz 対象クラス
	 * @return Beanオブジェクト
	 */
	public static <T> T createBean(Class<T> clazz) {
		try {
			T instance = clazz.newInstance();
			injectMocks(instance);
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	/**
	 * クラスを指定してBeanオブジェクト(SPY)を生成する。
	 *
	 * @param <T>   型パラメータ
	 * @param clazz 対象クラス
	 * @return Beanオブジェクト
	 */
	public static <T> T createSpyBean(Class<T> clazz) {
		try {
			T instance = Mockito.spy(clazz);
			injectMocks(instance);
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	/**
	 * 対象オブジェクトのInject要素のMockを一括生成する。
	 *
	 * @param obj 対象オブジェクト
	 */
	public static void injectMocks(Object bean) {
		for (Field field : getAllFields(bean.getClass())) {
			Class<?> clazz = field.getType();
			if (field.getAnnotation(Inject.class) != null) {
				Object fieldObj = Mockito.mock(clazz);
				Whitebox.setInternalState(bean, field.getName(), fieldObj);
			}
		}
	}

	private static List<Field> getAllFields(Class clazz) {
		if (clazz == null) {
			return Collections.emptyList();
		}

		List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
		List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields())
				.filter(f -> f.getAnnotation(Inject.class) != null).collect(Collectors.toList());
		result.addAll(filteredFields);
		return result;
	}
}
