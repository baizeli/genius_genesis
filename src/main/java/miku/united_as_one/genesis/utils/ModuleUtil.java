package miku.united_as_one.genesis.utils;

import sun.reflect.ReflectionFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.util.*;
import java.util.function.Function;

public class ModuleUtil {
    private static final MethodHandles.Lookup lookup;
    private static final MethodHandle unlimitedFinder;
    private static final MethodHandle loadModule;
    private static final MethodHandle mappingFunction;
    private static final MethodHandle addEnableNativeAccess;
    private static final MethodHandle addEnableNativeAccessAllUnnamed;
    private static final MethodHandle addReads;
    private static final MethodHandle addExports;
    private static final MethodHandle addOpens;
    private static final MethodHandle addOpensToAllUnnamed;
    private static final VarHandle nameToModuleVH;
    private static final VarHandle modulesVH;
    private static final VarHandle graphVH;

    static {
        try {
            lookup = (MethodHandles.Lookup) ReflectionFactory
                    .getReflectionFactory()
                    .newConstructorForSerialization(
                            MethodHandles.Lookup.class,
                            MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Class.class, int.class)
                    )
                    .newInstance(Object.class, null, -1);

            Class<?> bootLoaderClass = lookup.findClass("jdk.internal.loader.BootLoader");
            Class<?> moduleBootstrapClass = lookup.findClass("jdk.internal.module.ModuleBootstrap");
            Class<?> moduleLoaderMapClass = lookup.findClass("jdk.internal.module.ModuleLoaderMap");

            unlimitedFinder = lookup.findStatic(
                    moduleBootstrapClass, "unlimitedFinder",
                    MethodType.methodType(ModuleFinder.class));

            loadModule = lookup.findStatic(bootLoaderClass, "loadModule",
                    MethodType.methodType(void.class, ModuleReference.class));

            mappingFunction = lookup.findStatic(
                    moduleLoaderMapClass, "mappingFunction",
                    MethodType.methodType(Function.class, Configuration.class));

            addEnableNativeAccess = lookup.findVirtual(
                    Module.class, "implAddEnableNativeAccess",
                    MethodType.methodType(Module.class));

            addEnableNativeAccessAllUnnamed = lookup.findStatic(
                    Module.class, "implAddEnableNativeAccessAllUnnamed",
                    MethodType.methodType(void.class));

            addReads = lookup.findVirtual(
                    Module.class, "implAddReads",
                    MethodType.methodType(void.class, Module.class));

            addExports = lookup.findVirtual(
                    Module.class, "implAddExports",
                    MethodType.methodType(void.class, String.class, Module.class));

            addOpens = lookup.findVirtual(
                    Module.class, "implAddOpens",
                    MethodType.methodType(void.class, String.class, Module.class));

            addOpensToAllUnnamed = lookup.findVirtual(
                    Module.class, "implAddOpensToAllUnnamed",
                    MethodType.methodType(void.class, String.class));

            nameToModuleVH = lookup.findVarHandle(Configuration.class, "nameToModule", Map.class);
            modulesVH = lookup.findVarHandle(Configuration.class, "modules", Set.class);
            graphVH = lookup.findVarHandle(Configuration.class, "graph", Map.class);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadSystemModule(String moduleName) throws Throwable {
        if (ModuleLayer.boot().findModule(moduleName).isPresent())
            return;

        ModuleLayer top = ModuleLayer.boot();

        ModuleFinder finder = (ModuleFinder) unlimitedFinder.invoke();
        Configuration cf = top.configuration().resolveAndBind(ModuleFinder.of(), finder, Set.of(moduleName));
        ResolvedModule resolved = cf.findModule(moduleName).orElseThrow(
                () -> new IllegalStateException("Failed to resolve module: " + moduleName)
        );

        loadModule.invoke(resolved.reference());

        @SuppressWarnings("unchecked")
        var clf = (Function<String, ClassLoader>) mappingFunction.invoke(cf);
        ModuleLayer newLayer = top.defineModules(cf, clf);

        injectResolvedModuleToConfiguration(ModuleLayer.boot().configuration(), resolved);

        // 开权限部分
        Module module = newLayer.findModule(moduleName).orElseThrow(
                () -> new IllegalStateException("Failed to locate module in new layer: " + moduleName)
        );
        Module myModule = ModuleUtil.class.getModule();

        addEnableNativeAccess.invoke(module);

        if (myModule.isNamed()) {
            addEnableNativeAccess.invoke(myModule);
            addReads.invokeExact(myModule, module);
            for (String pkg : module.getPackages()) {
                try {
                    addOpens.invokeExact(module, pkg, myModule);
                } catch (Throwable ignored) {}
            }
        } else {
            addEnableNativeAccessAllUnnamed.invoke();
            for (String pkg : module.getDescriptor().packages()) {
                try {
                    addOpensToAllUnnamed.invokeExact(module, pkg);
                } catch (Throwable ignored) {}
            }
        }

        Module javaBase = Object.class.getModule();

        // 把 java.base 的所有包全部导出给目标模块，因为模块内部需要访问 java.base
        for (String pkg : javaBase.getPackages()) {
            try {
                addExports.invokeExact(javaBase, pkg, module);
            } catch (Throwable ignored) {}
        }
    }

    @SuppressWarnings("unchecked")
    private static void injectResolvedModuleToConfiguration(Configuration target, ResolvedModule toInject) {
        var newNameToModule = new HashMap<>((Map<String, ResolvedModule>) nameToModuleVH.get(target));
        newNameToModule.put(toInject.name(), toInject);
        nameToModuleVH.set(target, Collections.unmodifiableMap(newNameToModule));

        var newModules = new HashSet<>((Set<ResolvedModule>) modulesVH.get(target));
        newModules.add(toInject);
        modulesVH.set(target, Collections.unmodifiableSet(newModules));

        var newGraph = new HashMap<>((Map<ResolvedModule, Set<ResolvedModule>>) graphVH.get(target));
        newGraph.put(toInject, toInject.reads());
        graphVH.set(target, Collections.unmodifiableMap(newGraph));
    }
}
