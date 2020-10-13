package com.blebail.querydsl.crud.sync.repository;

import com.blebail.junit.SqlFixture;
import com.blebail.junit.SqlMemoryDb;
import com.blebail.querydsl.crud.BAccount;
import com.blebail.querydsl.crud.QAccount;
import com.blebail.querydsl.crud.commons.resource.IdentifiableQDSLResource;
import com.blebail.querydsl.crud.commons.utils.Factories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.admin;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.insertDefaultAccounts;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.insertJohnDoe;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.johnDoe;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.system;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QDSLCrudRepositoryIT {

    @RegisterExtension
    static SqlMemoryDb sqlMemoryDb = new SqlMemoryDb();

    @RegisterExtension
    SqlFixture sqlFixture = new SqlFixture(sqlMemoryDb::dataSource, insertDefaultAccounts());

    CrudRepository<BAccount, String> tested;

    @BeforeEach
    void setUp() {
        tested = new QDSLCrudRepository<>(
                new IdentifiableQDSLResource<>(QAccount.account, QAccount.account.id, BAccount::getId),
                Factories.defaultQueryFactory(sqlMemoryDb.dataSource())
        );
    }

    @Test
    void shouldThrowException_whenPersistingNull() {
        sqlFixture.readOnly();

        assertThrows(NullPointerException.class, () -> tested.save((BAccount) null));
    }

    @Test
    void shouldPersistAResource() {
        BAccount newAccount = new BAccount("tom@test.com", "1234", "tomsearle");
        long accountCount = countRows();

        Optional<BAccount> maybeAccount = tested.findOne(tested.save(newAccount).getId());

        long accountCountAfterCreate = countRows();

        assertThat(accountCountAfterCreate).isEqualTo(accountCount + 1);
        assertThat(maybeAccount).usingFieldByFieldValueComparator().contains(newAccount);
    }

    @Test
    void shouldReturnThePersistedResource() {
        BAccount newAccount = new BAccount("tom@test.com", "1234", "tomsearle");

        BAccount saveAccount = tested.save(newAccount);

        assertThat(saveAccount).usingRecursiveComparison().isEqualTo(saveAccount);
    }

    @Test
    void shouldPersistResources() {
        BAccount newAccount1 = new BAccount("tom@test.com", "1234", "tomsearle");
        BAccount newAccount2 = new BAccount("dan@test.com", "2345", "dansearle");
        List<BAccount> newAccounts = List.of(newAccount1, newAccount2);

        long accountCount = countRows();

        tested.save(newAccounts);

        long accountCountAfterCreate = countRows();

        assertThat(accountCountAfterCreate).isEqualTo(accountCount + newAccounts.size());
    }

    @Test
    void shouldReturnThePersistedResources() {
        BAccount newAccount1 = new BAccount("tom@test.com", "1234", "tomsearle");
        BAccount newAccount2 = new BAccount("dan@test.com", "2345", "dansearle");
        List<BAccount> newAccounts = List.of(newAccount1, newAccount2);

        Collection<BAccount> createdAccounts = tested.save(newAccounts);

        assertThat(createdAccounts).usingFieldByFieldElementComparator().containsOnly(newAccount1, newAccount2);
    }

    @Test
    void shouldUpdateAResource() {
        BAccount accountToUpdate = new BAccount("newmail@test.com", admin().getId(), admin().getUsername());

        tested.save(accountToUpdate);

        Optional<BAccount> maybeUpdatedAccount = tested.findOne(accountToUpdate.getId());

        assertThat(maybeUpdatedAccount).usingFieldByFieldValueComparator().contains(accountToUpdate);
    }

    @Test
    void shouldReturnTheUpdatedResource() {
        BAccount accountToUpdate = new BAccount("newmail@test.com", admin().getId(), admin().getUsername());
        BAccount updatedAccount = tested.save(accountToUpdate);

        assertThat(updatedAccount).usingRecursiveComparison().isEqualTo(accountToUpdate);
    }

    @Test
    void shouldUpdateResources() {
        sqlFixture.exec(insertJohnDoe());

        BAccount accountToUpdate1 = new BAccount("newmail1@test.com", system().getId(), system().getUsername());
        BAccount accountToUpdate2 = new BAccount("newmail2@test.com", admin().getId(), admin().getUsername());
        BAccount accountNotToUpdate = johnDoe();
        List<BAccount> accountsToUpdate = List.of(accountToUpdate1, accountToUpdate2);

        tested.save(accountsToUpdate);

        Optional<BAccount> maybeUpdatedAccount1 = tested.findOne(accountToUpdate1.getId());
        Optional<BAccount> maybeUpdatedAccount2 = tested.findOne(accountToUpdate2.getId());
        Optional<BAccount> maybeNotUpdatedAccount = tested.findOne(accountNotToUpdate.getId());

        assertThat(maybeUpdatedAccount1).usingFieldByFieldValueComparator().contains(accountToUpdate1);
        assertThat(maybeUpdatedAccount2).usingFieldByFieldValueComparator().contains(accountToUpdate2);
        assertThat(maybeNotUpdatedAccount).usingFieldByFieldValueComparator().contains(accountNotToUpdate);
    }

    @Test
    void shouldReturnUpdatedResources() {
        BAccount accountToUpdate1 = new BAccount("newmail1@test.com", system().getId(), system().getUsername());
        BAccount accountToUpdate2 = new BAccount("newmail2@test.com", admin().getId(), admin().getUsername());
        List<BAccount> accountsToUpdate = List.of(accountToUpdate1, accountToUpdate2);

        Collection<BAccount> updatedAccounts = tested.save(accountsToUpdate);

        assertThat(updatedAccounts).usingFieldByFieldElementComparator().containsOnly(accountToUpdate1, accountToUpdate2);
    }

    @Test
    void shouldCheckIfResourceExists() {
        sqlFixture.readOnly();

        assertThat(tested.exists(system().getId())).isTrue();
    }

    @Test
    void shouldCheckIfResourceDoesntExist_whenIdDoesntExist() {
        sqlFixture.readOnly();

        assertThat(tested.exists("1234")).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenChekingIfNullIdExist() {
        sqlFixture.readOnly();

        assertThrows(NullPointerException.class, () -> tested.exists(null));
    }

    @Test
    void shouldFindResource() {
        sqlFixture.readOnly();

        Optional<BAccount> maybeAccount = tested.findOne(system().getId());

        assertThat(maybeAccount).usingFieldByFieldValueComparator().contains(system());
    }

    @Test
    void shouldReturnEmpty_whenFindingNonExistingResource() {
        sqlFixture.readOnly();

        assertThat(tested.findOne("1234")).isEmpty();
    }

    @Test
    void shouldThrowException_whenFindingNull() {
        sqlFixture.readOnly();

        assertThrows(NullPointerException.class, () -> tested.findOne((String) null));
    }

    @Test
    void shouldFindResources() {
        sqlFixture.readOnly();

        List<String> ids = List.of(system().getId(), admin().getId());
        Collection<BAccount> accounts = tested.find(ids);

        assertThat(accounts)
                .usingFieldByFieldElementComparator()
                .containsOnly(system(), admin());
    }

    @Test
    void shouldReturnNoResources_whenFindingNonExistingResources() {
        sqlFixture.readOnly();

        Collection<BAccount> accounts = tested.find(List.of("1234", "2345"));

        assertThat(accounts).isEmpty();
    }

    @Test
    void shouldReturnNoResources_whenFindingZeroResources() {
        sqlFixture.readOnly();

        Collection<BAccount> uuidEntities = tested.find(List.of());

        assertThat(uuidEntities).isEmpty();
    }

    @Test
    void shouldReturnNoResources_whenFindingNulls() {
        sqlFixture.readOnly();

        Collection<BAccount> uuidEntities = tested.find(Arrays.asList(null, null));

        assertThat(uuidEntities).isEmpty();
    }

    @Test
    void shouldReturnOnlyExistingResources_whenFindingEexistingAndNonExistingResources() {
        sqlFixture.readOnly();

        List<String> ids = List.of(system().getId(), admin().getId(), "1234");
        Collection<BAccount> accounts = tested.find(ids);

        assertThat(accounts)
                .usingFieldByFieldElementComparator()
                .containsOnly(system(), admin());
    }

    @Test
    void shouldReturnFalse_whenDeletingNonExistingResource() {
        assertThat(tested.delete("1234")).isFalse();
    }

    @Test
    void shouldThrowException_whenDeletingNullId() {
        assertThrows(NullPointerException.class, () -> tested.delete((String) null));
    }

    @Test
    void shouldNotDeleteAnyResources_whenDeletingNonExistingResource() {
        long accountCount = countRows();

        tested.delete("1234");

        long accountCountAfterDelete = countRows();

        assertThat(accountCount).isEqualTo(accountCountAfterDelete);
    }

    @Test
    void shouldNotDeleteAnyResources_whenDeletingNullId() {
        long accountCount = countRows();

        assertThrows(NullPointerException.class, () -> tested.delete((String) null));

        long accountCountAfterDelete = countRows();

        assertThat(accountCount).isEqualTo(accountCountAfterDelete);
    }

    @Test
    void shouldDeleteAResource() {
        long accountCount = countRows();

        tested.delete(admin().getId());

        long accountCountAfterCreate = countRows();

        assertThat(accountCountAfterCreate).isEqualTo(accountCount - 1);
    }

    @Test
    void shouldReturnTrue_whenDeletingResource() {
        boolean delete = tested.delete(admin().getId());

        assertThat(delete).isTrue();
    }

    @Test
    void shouldDeleteMultipleResources() {
        List<String> ids = List.of(system().getId(), admin().getId());
        long accountCount = countRows();

        tested.delete(ids);

        long accountCountAfterDelete = countRows();

        assertThat(accountCountAfterDelete).isEqualTo(accountCount - ids.size());
    }

    @Test
    void shouldReturnTrue_whenDeletingMultipleResources() {
        List<String> ids = List.of(system().getId(), admin().getId());
        boolean delete = tested.delete(ids);

        assertThat(delete).isTrue();
    }


    @Test
    void shouldNotDeleteAnyResources_whenDeletingNonExistingResources() {
        List<String> ids = List.of("1234", "2345");
        long accountCount = countRows();

        tested.delete(ids);

        long accountCountAfterDelete = countRows();

        assertThat(accountCountAfterDelete).isEqualTo(accountCount);
    }

    @Test
    void shouldReturnFalse_whenNotDeletingAnyResources() {
        List<String> ids = List.of("1234", "2345");
        boolean delete = tested.delete(ids);

        assertThat(delete).isFalse();
    }

    @Test
    void shouldDeleteOnlyExistingResources_whenDeletingExistingAndNonExistingResources() {
        List<String> ids = List.of(system().getId(), admin().getId(), "1234");

        long accountCount = countRows();

        tested.delete(ids);

        long accountCountAfterDelete = countRows();

        assertThat(accountCountAfterDelete).isEqualTo(accountCount - 2);
    }

    private long countRows() {
        return Factories.defaultQueryFactory(sqlMemoryDb.dataSource())
                .select(QAccount.account)
                .from(QAccount.account)
                .fetchCount();
    }
}