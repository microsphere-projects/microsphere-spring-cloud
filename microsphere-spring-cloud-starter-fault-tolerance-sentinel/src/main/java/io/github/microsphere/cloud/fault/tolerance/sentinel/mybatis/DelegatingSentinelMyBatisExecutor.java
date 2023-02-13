package io.github.microsphere.cloud.fault.tolerance.sentinel.mybatis;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Delegating Sentinel {@link Executor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
class DelegatingSentinelMyBatisExecutor implements Executor {

    private final Executor delegate;

    public DelegatingSentinelMyBatisExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        return doInSentinel(ms, () -> delegate.update(ms, parameter));
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException {
        return doInSentinel(ms, () -> delegate.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql));
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        return doInSentinel(ms, () -> delegate.query(ms, parameter, rowBounds, resultHandler));
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        return doInSentinel(ms, () -> delegate.queryCursor(ms, parameter, rowBounds));
    }

    @Override
    public List<BatchResult> flushStatements() throws SQLException {
        return delegate.flushStatements();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        delegate.commit(required);
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        delegate.rollback(required);
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        return delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        return delegate.isCached(ms, key);
    }

    @Override
    public void clearLocalCache() {
        delegate.clearLocalCache();
    }

    @Override
    public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        delegate.deferLoad(ms, resultObject, property, key, targetType);
    }

    @Override
    public Transaction getTransaction() {
        return delegate.getTransaction();
    }

    @Override
    public void close(boolean forceRollback) {
        delegate.close(forceRollback);
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        delegate.setExecutorWrapper(executor);
    }

    protected <T> T doInSentinel(MappedStatement ms, Callable<T> callable) throws SQLException {
        String resourceName = getSentinelResourceName(ms);
        T result = null;
        ContextUtil.enter("mybatis-context", "Executor");
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);
            result = callable.call();
        } catch (Throwable e) {
            if (!BlockException.isBlockException(e)) {
                Tracer.trace(e);
            }
            if (e instanceof SQLException) {
                throw (SQLException) e;
            } else {
                throw new SQLException(e);
            }
        } finally {
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
        }
        return result;
    }

    private String getSentinelResourceName(MappedStatement ms) {
        return ms.getId();
    }
}
