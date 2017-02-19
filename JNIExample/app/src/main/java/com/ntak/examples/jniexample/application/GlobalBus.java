package com.ntak.examples.jniexample.application;

import org.greenrobot.eventbus.EventBus;

/**
 * Constant list of Global Event Buses to be referenced by activities and services/tasks etc.
 *
 * Created by akakshepati on 21/12/16.
 */
public class GlobalBus {
    public static final EventBus backgroundTasks = EventBus.getDefault();
    public static final EventBus callbackTasks = EventBus.getDefault();
}
