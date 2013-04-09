package ClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClassLoader440 extends ClassLoader {
	
	public Class<?> getClass(String path, String className) {
		byte[] code = null;
		try {
			code = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return defineClass(className, code, 0, code.length);
	}
}
