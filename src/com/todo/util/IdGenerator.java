package com.todo.util;

import java.util.Collection;

public final class IdGenerator {
    private IdGenerator() {
    }

    public static long nextId(Collection<Long> existingIds) {
        return existingIds.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1L;
    }
}
