package com.jeffskj.torrent.tasks;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jeffskj.torrent.DefaultModule;

public class TaskBootstrapper {
    public static void main(String[] args) throws Exception {
        Preconditions.checkPositionIndex(0, args.length, "task class name must be specified");
        String currentPackage = TaskBootstrapper.class.getPackage().getName();
        Class<?> taskType = Class.forName(currentPackage + "." + args[0], true, TaskBootstrapper.class.getClassLoader());
        Injector injector = Guice.createInjector(new DefaultModule());
        Runnable task = (Runnable) injector.getInstance(taskType);
        task.run();
    }
}
