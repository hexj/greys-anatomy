package com.github.ompc.greys.core.listener;

import com.github.ompc.greys.common.util.GaStringUtils;
import com.github.ompc.greys.core.util.GaMethod;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static com.github.ompc.greys.core.listener.Advice.*;

/**
 * 通知监听器
 */
public abstract class AdviceListener implements InvokeListener {

    private ClassLoader toClassLoader(ClassLoader loader) {
        return null != loader
                ? loader
                : AdviceListener.class.getClassLoader();
    }

    private Class<?> toClass(ClassLoader loader, String className) throws ClassNotFoundException {
        return Class.forName(GaStringUtils.tranClassName(className), true, toClassLoader(loader));
    }


    private GaMethod toMethod(ClassLoader loader, Class<?> clazz, String methodName, String methodDesc)
            throws ClassNotFoundException, NoSuchMethodException {
        final Type asmType = Type.getMethodType(methodDesc);

        // to arg types
        final Class<?>[] argsClasses = new Class<?>[asmType.getArgumentTypes().length];
        for (int index = 0; index < argsClasses.length; index++) {

            // asm class descriptor to jvm class
            final Class<?> argumentClass;
            final Type argumentAsmType = asmType.getArgumentTypes()[index];
            switch (argumentAsmType.getSort()) {
                case Type.BOOLEAN: {
                    argumentClass = boolean.class;
                    break;
                }
                case Type.CHAR: {
                    argumentClass = char.class;
                    break;
                }
                case Type.BYTE: {
                    argumentClass = byte.class;
                    break;
                }
                case Type.SHORT: {
                    argumentClass = short.class;
                    break;
                }
                case Type.INT: {
                    argumentClass = int.class;
                    break;
                }
                case Type.FLOAT: {
                    argumentClass = float.class;
                    break;
                }
                case Type.LONG: {
                    argumentClass = long.class;
                    break;
                }
                case Type.DOUBLE: {
                    argumentClass = double.class;
                    break;
                }
                case Type.ARRAY: {
                    argumentClass = toClass(loader, argumentAsmType.getInternalName());
                    break;
                }
                case Type.VOID: {
                    argumentClass = void.class;
                    break;
                }
                case Type.OBJECT:
                case Type.METHOD:
                default: {
                    argumentClass = toClass(loader, argumentAsmType.getClassName());
                    break;
                }
            }

            argsClasses[index] = argumentClass;
        }

        // to method or constructor
        if (StringUtils.equals(methodName, "<process>")) {
            return new GaMethod.ConstructorImpl(toConstructor(clazz, argsClasses));
        } else {
            return new GaMethod.MethodImpl(toMethod(clazz, methodName, argsClasses));
        }
    }


    private Method toMethod(Class<?> clazz, String methodName, Class<?>[] argClasses) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(methodName, argClasses);
    }

    private Constructor<?> toConstructor(Class<?> clazz, Class<?>[] argClasses) throws NoSuchMethodException {
        return clazz.getDeclaredConstructor(argClasses);
    }


    @Override
    final public void before(
            ClassLoader loader, String className, String methodName, String methodDesc,
            Object target, Object[] args) throws Throwable {
        final Class<?> clazz = toClass(loader, className);

        // 执行before调用
        before(
                newBefore(
                        loader,
                        clazz,
                        toMethod(loader, clazz, methodName, methodDesc),
                        target,
                        args
                )
        );

    }

    @Override
    final public void afterReturning(
            ClassLoader loader, String className, String methodName, String methodDesc,
            Object target, Object[] args, Object returnObject) throws Throwable {

        final Class<?> clazz = toClass(loader, className);
        final Advice advice = newAfterRetuning(
                loader,
                clazz,
                toMethod(loader, clazz, methodName, methodDesc),
                target,
                args,
                returnObject
        );

        afterReturning(advice);
        afterFinishing(advice);

    }

    @Override
    final public void afterThrowing(
            ClassLoader loader, String className, String methodName, String methodDesc,
            Object target, Object[] args, Throwable throwable) throws Throwable {

        final Class<?> clazz = toClass(loader, className);
        final Advice advice = newAfterThrowing(
                loader,
                clazz,
                toMethod(loader, clazz, methodName, methodDesc),
                target,
                args,
                throwable
        );

        afterThrowing(advice);
        afterFinishing(advice);

    }


    /**
     * 前置通知
     *
     * @param advice 通知点
     * @throws Throwable 通知过程出错
     */
    public void before(Advice advice) throws Throwable {

    }

    /**
     * 返回通知
     *
     * @param advice 通知点
     * @throws Throwable 通知过程出错
     */
    public void afterReturning(Advice advice) throws Throwable {

    }

    /**
     * 异常通知
     *
     * @param advice 通知点
     * @throws Throwable 通知过程出错
     */
    public void afterThrowing(Advice advice) throws Throwable {

    }

    /**
     * 结束通知
     *
     * @param advice 通知点
     * @throws Throwable 通知过程出错
     */
    public void afterFinishing(Advice advice) throws Throwable {

    }

}
