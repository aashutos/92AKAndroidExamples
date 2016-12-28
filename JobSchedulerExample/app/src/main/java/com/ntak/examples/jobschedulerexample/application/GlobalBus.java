package com.ntak.examples.jobschedulerexample.application;

import org.greenrobot.eventbus.EventBus;

/**
 * Constant list of Global Event Buses to be referenced by activities and services/tasks etc.
 *
 * Created by akakshepati on 21/12/16.
 */
public class GlobalBus {
    public static final EventBus localTasks = EventBus.getDefault();
    public static final EventBus netTasks = EventBus.getDefault();
    public static final EventBus callbackTasks = EventBus.getDefault();
}
