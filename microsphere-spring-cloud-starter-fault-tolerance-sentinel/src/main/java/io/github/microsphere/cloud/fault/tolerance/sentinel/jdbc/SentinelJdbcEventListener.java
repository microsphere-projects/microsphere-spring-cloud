/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.microsphere.cloud.fault.tolerance.sentinel.jdbc;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.event.SimpleJdbcEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * {@link JdbcEventListener} based on Alibaba Sentinel
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SentinelJdbcEventListener extends SimpleJdbcEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SentinelJdbcEventListener.class);

    private final ThreadLocal<Entry> entryThreadLocal = new ThreadLocal<>();

    @Override
    public void onBeforeAnyExecute(StatementInformation statementInformation) {
        String resourceName = getResourceName(statementInformation);

        String origin = getOrigin(statementInformation);

        String contextName = getContextName(statementInformation);

        ContextUtil.enter(contextName, origin);

        entranceEntry(resourceName);
    }

    @Override
    public void onAfterAnyExecute(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        Entry entry = getEntry();

        if (entry == null) {
            logger.debug("The Sentinel Entry was not bound at the current thread, the statement sql : '{}'", statementInformation.getSql());
            return;
        }

        if (e != null) {
            Tracer.traceEntry(e, entry);
        }

        entry.exit();

        ContextUtil.exit();

        clearEntry();
    }

    private String getResourceName(StatementInformation statementInformation) {
        // TODO may bound the sql to prevent OOM.
        String resourceName = statementInformation.getSql();
        logger.trace("Sentinel JDBC StatementInformation resource[name : '{}']", resourceName);
        return resourceName;
    }

    private String getOrigin(StatementInformation statementInformation) {
        // TODO
        return null;
    }

    private String getContextName(StatementInformation statementInformation) {
        int connectionId = statementInformation.getConnectionInformation().getConnectionId();
        return "microsphere.sentinel.jdbc.context-" + connectionId;
    }

    private void entranceEntry(String resourceName) {
        try {
            Entry entry = SphU.entry(resourceName, ResourceTypeConstants.COMMON, EntryType.IN);
            setEntry(entry);
        } catch (BlockException e) {
            logger.info("Sentinel JDBC StatementInformation resource[name : '{}'] is blocked", resourceName, e);
        }
    }

    private void setEntry(Entry entry) {
        entryThreadLocal.set(entry);
    }

    private Entry getEntry() {
        return entryThreadLocal.get();
    }

    private void clearEntry() {
        entryThreadLocal.remove();
    }
}
