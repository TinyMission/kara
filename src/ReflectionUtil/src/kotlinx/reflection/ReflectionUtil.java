package kotlinx.reflection;

import kotlin.reflect.KClass;
import kotlin.reflect.jvm.internal.KClassImpl;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;

class ReflectionUtil {
    static ClassDescriptor getClassDescriptor(KClass<?> klass) {
        return ((KClassImpl)klass).getDescriptor();
    }
}
