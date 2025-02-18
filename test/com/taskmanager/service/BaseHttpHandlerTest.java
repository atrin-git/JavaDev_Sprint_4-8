package com.taskmanager.service;

import com.taskmanager.service.handlers.BaseHttpHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseHttpHandlerTest extends BaseHttpHandler {

    @Test
    public void checkBaseHttpHandlerCreated() {
        BaseHttpHandler baseHandler = new BaseHttpHandlerTest();

        assertNotNull(baseHandler, "Объект должен был быть проинициализированным");
        assertEquals(baseHandler.getClass().getSimpleName(), BaseHttpHandlerTest.class.getSimpleName(),
                "Наименования классов не совпадают");
    }
}
