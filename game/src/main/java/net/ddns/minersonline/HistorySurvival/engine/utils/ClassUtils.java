package net.ddns.minersonline.HistorySurvival.engine.utils;

import java.net.URLDecoder;
import java.util.regex.Pattern;

public class ClassUtils {
	/**
	 * Returns the container url for this class. This varies based on whether or
	 * not the class files are in a zip/jar or not, so this method standardizes
	 * that. The method may return null, if the class is a dynamically generated
	 * class (perhaps with asm, or a proxy class)
	 *
	 * @param c The class to find the container for
	 * @author https://stackoverflow.com/a/17838001/8737805
	 * @return
	 */
	public static String GetClassContainer(Class c) {
		if (c == null) {
			throw new NullPointerException("The Class passed to this method may not be null");
		}
		try {
			while(c.isMemberClass() || c.isAnonymousClass()){
				c = c.getEnclosingClass(); //Get the actual enclosing file
			}
			if (c.getProtectionDomain().getCodeSource() == null) {
				//This is a proxy or other dynamically generated class, and has no physical container,
				//so just return null.
				return null;
			}
			String packageRoot;
			try {
				//This is the full path to THIS file, but we need to get the package root.
				String thisClass = c.getResource(c.getSimpleName() + ".class").toString();
				packageRoot = StringUtils.replaceLast(thisClass, Pattern.quote(c.getName().replaceAll("\\.", "/") + ".class"), "");
				if(packageRoot.endsWith("!/")){
					packageRoot = StringUtils.replaceLast(packageRoot, "!/", "");
				}
			} catch (Exception e) {
				//Hmm, ok, try this then
				packageRoot = c.getProtectionDomain().getCodeSource().getLocation().toString();
			}
			packageRoot = URLDecoder.decode(packageRoot, "UTF-8");
			return packageRoot;
		} catch (Exception e) {
			throw new RuntimeException("While interrogating " + c.getName() + ", an unexpected exception was thrown.", e);
		}
	}
}
