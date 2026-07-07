package com.shopdarcel.common.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    /**
     * Minimal concrete subclass used only for testing BaseEntity's own
     * behaviour. BaseEntity is abstract and has no fields of its own beyond
     * id/createdAt/updatedAt, so this test double adds nothing extra.
     */
    static class TestEntity extends BaseEntity {
    }

    @Test
    void equals_returnsTrueWhenIdsMatch_evenIfOtherFieldsDiffer() {
        TestEntity first = new TestEntity();
        first.setId(1L);
        first.setCreatedAt(java.time.Instant.now());

        TestEntity second = new TestEntity();
        second.setId(1L);
        second.setCreatedAt(java.time.Instant.now()
                .minusSeconds(100));

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void equals_returnsFalseWhenIdsDiffer() {
        TestEntity first = new TestEntity();
        first.setId(1L);

        TestEntity second = new TestEntity();
        second.setId(2L);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void gettersAndSetters_workAsExpected() {
        TestEntity entity = new TestEntity();
        java.time.Instant now = java.time.Instant.now();

        entity.setId(99L);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertThat(entity.getId()).isEqualTo(99L);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }
}