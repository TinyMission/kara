/*
 * @author max
 */
package kara;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class CustomClassloaderObjectInputStream extends ObjectInputStream {
    private ClassLoader myClassLoader;
    public CustomClassloaderObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
        super(in);
        myClassLoader = classLoader;
    }

    @NotNull
    @Override
    final protected Class<?> resolveClass(@NotNull ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            return Class.forName(desc.getName(), true, myClassLoader);
        }
        catch (Throwable e) {
            return super.resolveClass(desc);
        }
    }
}
