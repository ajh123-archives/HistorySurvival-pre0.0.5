package tk.minersonline.history_survival.util;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ClassProxy<T> {
	private final Class<T> type;

	public ClassProxy(Class<T> type) {
		this.type = type;
	}

	public Class<T> getMyType() {
		return this.type;
	}

	public T getProxy(Object object){
		InvocationHandler handler = (proxy, method, args) -> {
			Method newMethod = object.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
			boolean ok = true;
			if (!newMethod.canAccess(object)) {
				ok = false;
				newMethod.setAccessible(true);
			}
			Object result = newMethod.invoke(object, args);
			Class<?> klass = result.getClass();
			if (!ok) {
				newMethod.setAccessible(false);
			}

			if (result instanceof Collection<?> collection) {
				List<Object> newList = new ArrayList<>();
				Class<?> type = null;
				if (collection.size() > 0) {
					Object o = collection.toArray()[0];
					type = getNearestInterface(o);
				}
				if (type != null) {
					for (Object content : collection) {
//
//						Context otherContext = createPackageContext("com.package.b",
//								CONTEXT_INCLUDE_CODE | CONTEXT_IGNORE_SECURITY);
//						ClassLoader loader = otherContext.getClassLoader();
//						Class<?> btest = Class.forName("com.package.b.BTest", true, loader);

						newList.add(type.cast(content));
					}
				}
				return newList;
			}

			ClassProxy<?> resultProxy = new ClassProxy<>(klass);
			return resultProxy.getProxy(result);
		};

		T proxy = (T) Proxy.newProxyInstance(
				getMyType().getClassLoader(),
				new Class[]{getMyType()},
				handler
		);

		return proxy;
	}

	public Class<?> getNearestInterface(Object object) {
		Class<?> checking = object.getClass();
		while (true) {
			Class<?>[] interfaces = checking.getInterfaces();
			if (interfaces.length == 0) {
				checking = checking.getSuperclass();
			} else {
				System.out.println(checking);
				Class<?>[] interfaces2 = checking.getInterfaces();
				return interfaces2[0];
			}
		}
	}
}
