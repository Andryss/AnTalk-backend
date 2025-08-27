package ru.andryss.antalk.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.andryss.antalk.server.BaseDbTest;
import ru.andryss.antalk.server.util.DbTestUtil;

@AutoConfigureMockMvc
public abstract class BaseApiTest extends BaseDbTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DbTestUtil dbTestUtil;
}
