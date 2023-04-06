package com.gnomes.common.persistence;

import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gnomes.TestUtil;

class GnomesEntityManagerTest {

    private GnomesEntityManager manager;

    private EntityManager em;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
        manager = TestUtil.createBean(GnomesEntityManager.class);
        em = mock(EntityManager.class);
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    @DisplayName("エンティティマネージャー取得：ejbBeanから取得")
    void test_getEntityManager_isEjbBatch_true() {
        setupDoReturnGetEntityManager(true);
        manager.getEntityManager();

        verify(manager.ejbBean, times(1)).getEntityManager();
        verify(manager.scrBean, times(0)).getEntityManager();
    }

    @Test
    @DisplayName("エンティティマネージャー取得：gnomesSessionBeanから取得")
    void test_getEntityManager_isEjbBatch_false() {
        setupDoReturnGetEntityManager(false);
        manager.getEntityManager();

        verify(manager.ejbBean, times(0)).getEntityManager();
        verify(manager.scrBean, times(1)).getEntityManager();
    }

    private void setupDoReturnGetEntityManager(boolean isEjbBatch) {
        doReturn(isEjbBatch).when(manager.ejbBean).isEjbBatch();
        if (isEjbBatch) {
            doReturn(em).when(manager.ejbBean).getEntityManager();
        } else {
            doReturn(em).when(manager.scrBean).getEntityManager();
        }
    }

}
