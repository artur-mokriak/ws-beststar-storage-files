package com.beststar.storage.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * {@link CompletableFuture} utils.
 *
 * @author stonar96
 *
 * @see CompletableFuture
 */
public final class CompletableFutureUtils {
    /**
     * Delegates the given Callable to
     * {@link CompletableFuture#completeAsync(Supplier)} using a new
     * CompletableFuture and handles checked exceptions accordingly to unchecked
     * exceptions.
     *
     * @param callable a function returning the value to be used to complete the
     *                 returned CompletableFuture
     * @param          <U> the function's return type
     * @return the new CompletableFuture
     * @see CompletableFuture#completeAsync(Supplier)
     */
    public static <U> CompletableFuture<U> callAsync(Callable<U> callable) {
        return completeAsync(new CompletableFuture<>(), callable);
    }

    /**
     * Delegates the given Callable to
     * {@link CompletableFuture#completeAsync(Supplier)} using the given
     * CompletableFuture and handles checked exceptions accordingly to unchecked
     * exceptions.
     *
     * @param result   the CompletableFuture to be used
     * @param callable a function returning the value to be used to complete the
     *                 returned CompletableFuture
     * @param          <T> the function's return type
     * @return the given CompletableFuture
     * @see CompletableFuture#completeAsync(Supplier)
     */
    public static <T> CompletableFuture<T> completeAsync(CompletableFuture<T> result, Callable<? extends T> callable) {
        return result.completeAsync(callable == null ? null : () -> {
            try {
                return callable.call();
            } catch (Throwable t) {
                if (t instanceof Error) {
                    throw (Error) t;
                }

                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }

                result.completeExceptionally(t);
            }

            return null;
        });
    }

    /**
     * Delegates the given Callable and Executor to
     * {@link CompletableFuture#completeAsync(Supplier, Executor)} using a new
     * CompletableFuture and handles checked exceptions accordingly to unchecked
     * exceptions.
     *
     * @param callable a function returning the value to be used to complete the
     *                 returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @param          <U> the function's return type
     * @return the new CompletableFuture
     * @see CompletableFuture#completeAsync(Supplier, Executor)
     */
    public static <U> CompletableFuture<U> callAsync(Callable<U> callable, Executor executor) {
        return completeAsync(new CompletableFuture<>(), callable, executor);
    }

    /**
     * Delegates the given Callable and Executor to
     * {@link CompletableFuture#completeAsync(Supplier, Executor)} using the given
     * CompletableFuture and handles checked exceptions accordingly to unchecked
     * exceptions.
     *
     * @param result   the CompletableFuture to be used
     * @param callable a function returning the value to be used to complete the
     *                 returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @param          <T> the function's return type
     * @return the given CompletableFuture
     * @see CompletableFuture#completeAsync(Supplier, Executor)
     */
    public static <T> CompletableFuture<T> completeAsync(CompletableFuture<T> result, Callable<? extends T> callable, Executor executor) {
        return result.completeAsync(callable == null ? null : () -> {
            try {
                return callable.call();
            } catch (Throwable t) {
                if (t instanceof Error) {
                    throw (Error) t;
                }

                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }

                result.completeExceptionally(t);
            }

            return null;
        }, executor);
    }

    private CompletableFutureUtils() {
        throw new AssertionError("CompletableFutureUtils cannot be instantiated");
    }
}