package org.bukkit.plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;

import com.google.common.base.Preconditions;

import catserver.server.executor.hiddenclass.BukkitEventExecutorFactory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

/**
 * Interface which defines the class for event call backs to plugins
 */
public interface EventExecutor {
    public void execute(Listener listener, Event event) throws EventException;

    /*
    ConcurrentMap<Method, Class<? extends EventExecutor>> eventExecutorMap = new ConcurrentHashMap<Method, Class<? extends EventExecutor>>() {
        @Override
        public Class<? extends EventExecutor> computeIfAbsent(Method key, Function<? super Method, ? extends Class<? extends EventExecutor>> mappingFunction) {
            Class<? extends EventExecutor> executorClass = get(key);
            if (executorClass != null)
                return executorClass;

            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (key) {
                executorClass = get(key);
                if (executorClass != null)
                    return executorClass;

                return super.computeIfAbsent(key, mappingFunction);
            }
        }
    };
     */ // CatRoom - Use hidden class for event executors

    public static EventExecutor create(Method m, Class<? extends Event> eventClass) {
        Preconditions.checkNotNull(m, "Null method");
        Preconditions.checkArgument(m.getParameterCount() != 0, "Incorrect number of arguments %s", m.getParameterCount());
        Preconditions.checkArgument(m.getParameterTypes()[0] == eventClass, "First parameter %s doesn't match event class %s", m.getParameterTypes()[0], eventClass);
        // CatRoom start - Use hidden class for event executors
        if (m.getReturnType() != Void.TYPE) {
            final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(m.getDeclaringClass());
            Bukkit.getLogger().warning("@EventHandler method " + m.getDeclaringClass().getName() + (Modifier.isStatic(m.getModifiers()) ? '.' : '#') + m.getName()
            + " returns non-void type " + m.getReturnType().getName() + ", which is unsupported behaviour."
            + " This should be reported to the author of " + plugin.getDescription().getName() + " (" + String.join(",", plugin.getDescription().getAuthors()) + ')');
        }
        if (!m.trySetAccessible()) {
            final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(m.getDeclaringClass());
            throw new AssertionError(
                    "@EventHandler method " + m.getDeclaringClass().getName() + (Modifier.isStatic(m.getModifiers()) ? '.' : '#') + m.getName() + " is not accessible."
                            + " This should be reported to the author of " + plugin.getDescription().getName() + " (" + String.join(",", plugin.getDescription().getAuthors()) + ')'
            );
        }
        return BukkitEventExecutorFactory.create(m, eventClass);
    }
    // CatRoom end - Use hidden class for event executors
}
