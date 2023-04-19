package me.oragejuice.eventbus;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.objectweb.asm.Type;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ASMListenerFactory {
    static int factoryUID = 0;

    final String factoryId;
    private ConcurrentHashMap<Method, ASMListener> listenerCache = new ConcurrentHashMap();
    private static String interfaceName = Type.getInternalName(EventInvoker.class);
    private static EventClassLoader classLoader = new EventClassLoader(ASMListenerFactory.class.getClassLoader());
    private int invokerId;

    public ASMListenerFactory(String factoryId) {
        this.factoryId = factoryId;

    }

    public ASMListenerFactory() {
        this(String.valueOf(factoryUID));
        factoryUID++;
    }




    private ASMListener createInvoker(Method method, Object owner) {

        String ownerType = Type.getInternalName(method.getDeclaringClass());
        String eventType = Type.getInternalName(method.getParameterTypes()[0]);
        String handlerName = String.format(factoryId + "OrageEvent$%d", ++invokerId);

        ClassWriter w = new ClassWriter(0);

        w.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, handlerName, null, "java/lang/Object", new String[]{interfaceName});

        MethodVisitor init = w.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        init.visitCode();
        init.visitVarInsn(Opcodes.ALOAD, 0);
        init.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        init.visitInsn(Opcodes.RETURN);
        init.visitMaxs(1, 1);
        init.visitEnd();

        MethodVisitor m = w.visitMethod(Opcodes.ACC_PUBLIC, "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
        m.visitCode();
        m.visitVarInsn(Opcodes.ALOAD, 1);
        m.visitTypeInsn(Opcodes.CHECKCAST, ownerType);
        m.visitVarInsn(Opcodes.ALOAD, 2);
        m.visitTypeInsn(Opcodes.CHECKCAST, eventType);
        m.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ownerType, method.getName(), String.format("(L%s;)V", eventType), false);
        m.visitInsn(Opcodes.RETURN);
        m.visitMaxs(2, 3);
        m.visitEnd();

        w.visitEnd();

        byte[] classBytes = w.toByteArray();

        Class clazz = classLoader.defineClass(handlerName, classBytes);

        try {
            EventInvoker eventInvoker = (EventInvoker) clazz.newInstance();
            return new ASMListener(owner, method, eventInvoker);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ASMListener getOrPutAndReturn(Method method, Object owner){
        ASMListener r = listenerCache.putIfAbsent(method, createInvoker(method, owner));
        return r == null ? listenerCache.get(method) : r;
    }


}
