package com.blebail.querydsl.crud.async.repository;

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
import java.util.concurrent.ExecutionException;

import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.admin;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.insertDefaultAccounts;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.insertJohnDoe;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.johnDoe;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.system;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QDSLAsyncCrudRepositoryIT {

    @RegisterExtension
    static SqlMemoryDb sqlMemoryDb = new SqlMemoryDb();

    @RegisterExtension
    SqlFixture sqlFixture = new SqlFixture(sqlMemoryDb::dataSource, insertDefaultAccounts());

    AsyncCrudRepository<BAccount, String> tested;

    @BeforeEach
    void setUp() {
        tested = new QDSLAsyncCrudRepository<>(
                new IdentifiableQDSLResource<>(QAccount.account, QAccount.account.id, BAccount::getId),
                Factories.defaultQueryFactory(sqlMemoryDb.dataSource())
        );
    }

    @Test
    void shouldThrowException_whenPersistingNull() {
        sqlFixture.readOnly();

        assertThrows(ExecutionException.class, () -> tested.save((BAccount) null).get());
    }

    @Test
    void shouldPersistAResource() throws Exception {
        BAccount newAccount = new BAccount("tom@test.com", "1234", "tomsearle");
        long accountCount = countRows();

        Optional<BAccount> maybeAccount = tested.findOne(tested.save(newAccount).get().getId()).get();

        long accountCountAfterCreate = countRows();

        assertThat(accountCountAfterCreate).isEqualTo(accountCount + 1);
        assertThat(maybeAccount).usingFieldByFieldValueComparator().contains(newAccount);
    }

    @Test
    void shouldReturnThePersistedResource() throws Exception {
        BAccount newAccount = new BAccount("tom@test.com", "1234", "tomsearle");

        BAccount saveAccount = tested.save(newAccount).get();

        assertThat(saveAccount).usingRecursiveComparison().isEqualTo(saveAccount);
    }

    @Test
    void shouldPersistResources() throws Exception {
        BAccount newAccount1 = new BAccount("tom@test.com", "1234", "tomsearle");
        BAccount newAccount2 = new BAccount("dan@test.com", "2345", "dansearle");
        List<BAccount> newAccounts = List.of(newAccount1, newAccount2);

        long accountCount = countRows();

        tested.save(newAccounts).get();

        long accountCountAfterCreate = countRows();

        assertThat(accountCountAfterCreate).isEqualTo(accountCount + newAccounts.size());
    }

    @Test
    void shouldReturnThePersistedResources() throws Exception {
        BAccount newAccount1 = new BAccount("tom@test.com", "1234", "tomsearle");
        BAccount newAccount2 = new BAccount("dan@test.com", "2345", "dansearle");
        List<BAccount> newAccounts = List.of(newAccount1, newAccount2);

        Collection<BAccount> createdAccounts = tested.save(newAccounts).get();

        assertThat(createdAccounts).usingFieldByFieldElementComparator().containsOnly(newAccount1, newAccount2);
    }

    @Test
    void shouldUpdateAResource() throws Exception {
        BAccount accountToUpdate = new BAccount("newmail@test.com", admin().getId(), admin().getUsername());

        tested.save(accountToUpdate).get();

        Optional<BAccount> maybeUpdatedAccount = tested.findOne(accountToUpdate.getId()).get();

        assertThat(maybeUpdatedAccount).usingFieldByFieldValueComparator().contains(accountToUpdate);
    }

    @Test
    void shouldReturnTheUpdatedResource() throws Exception {
        BAccount accountToUpdate = new BAccount("newmail@test.com", admin().getId(), admin().getUsername());
        BAccount updatedAccount = tested.save(accountToUpdate).get();

        assertThat(updatedAccount).usingRecursiveComparison().isEqualTo(accountToUpdate);
    }

    @Test
    void shouldUpdateResources() throws Exception {
        sqlFixture.exec(insertJohnDoe());

        BAccount accountToUpdate1 = new BAccount("newmail1@test.com", system().getId(), system().getUsername());
        BAccount accountToUpdate2 = new BAccount("newmail2@test.com", admin().getId(), admin().getUsername());
        BAccount accountNotToUpdate = johnDoe();
        List<BAccount> accountsToUpdate = List.of(accountToUpdate1, accountToUpdate2);

        tested.save(accountsToUpdate).get();

        Optional<BAccount> maybeUpdatedAccount1 = tested.findOne(accountToUpdate1.getId()).get();
        Optional<BAccount> maybeUpdatedAccount2 = tested.findOne(accountToUpdate2.getId()).get();
        Optional<BAccount> maybeNotUpdatedAccount = tested.findOne(accountNotToUpdate.getId()).get();

        assertThat(maybeUpdatedAccount1).usingFieldByFieldValueComparator().contains(accountToUpdate1);
        assertThat(maybeUpdatedAccount2).usingFieldByFieldValueComparator().contains(accountToUpdate2);
        assertThat(maybeNotUpdatedAccount).usingFieldByFieldValueComparator().contains(accountNotToUpdate);
    }

    @Test
    void shouldReturnUpdatedResources() throws Exception {
        BAccount accountToUpdate1 = new BAccount("newmail1@test.com", system().getId(), system().getUsername());
        BAccount accountToUpdate2 = new BAccount("newmail2@test.com", admin().getId(), admin().getUsername());
        List<BAccount> accountsToUpdate = List.of(accountToUpdate1, accountToUpdate2);

        Collection<BAccount> updatedAccounts = tested.save(accountsToUpdate).get();

        assertThat(updatedAccounts).usingFieldByFieldElementComparator().containsOnly(accountToUpdate1, accountToUpdate2);
    }

    @Test
    void shouldCheckIfResourceExists() throws Exception {
        sqlFixture.readOnly();

        assertThat(tested.exists(system().getId()).get()).isTrue();
    }

    @Test
    void shouldCheckIfResourceDoesntExist_whenIdDoesntExist() throws Exception {
        sqlFixture.readOnly();

        assertThat(tested.exists("1234").get()).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenChekingIfNullIdExist() {
        sqlFixture.readOnly();

        assertThrows(ExecutionException.class, () -> tested.exists(null).get());
    }

    @Test
    void shouldFindResource() throws Exception {
        sqlFixture.readOnly();

        Optional<BAccount> maybeAccount = tested.findOne(system().getId()).get();

        assertThat(maybeAccount).usingFieldByFieldValueComparator().contains(system());
    }

    @Test
    void shouldReturnEmpty_whenFindingNonExistingResource() throws Exception {
        sqlFixture.readOnly();

        assertThat(tested.findOne("1234").get()).isEmpty();
    }

    @Test
    void shouldThrowException_whenFindingNull() {
        sqlFixture.readOnly();

        assertThrows(ExecutionException.class, () -> tested.findOne((String) null).get());
    }

    @Test
    void shouldFindResources() throws Exception {
        sqlFixture.readOnly();

        List<String> ids = List.of(system().getId(), admin().getId());
        Collection<BAccount> accounts = tested.find(ids).get();

        assertThat(accounts)
                .usingFieldByFieldElementComparator()
                .containsOnly(system(), admin());
    }

    @Test
    void shouldReturnNoResources_whenFindingNonExistingResources() throws Exception {
        sqlFixture.readOnly();

        Collection<BAccount> accounts = tested.find(List.of("1234", "2345")).get();

        assertThat(accounts).isEmpty();
    }

    @Test
    void shouldReturnNoResources_whenFindingZeroResources() throws Exception {
        sqlFixture.readOnly();

        Collection<BAccount> uuidEntities = tested.find(List.of()).get();

        assertThat(uuidEntities).isEmpty();
    }

    @Test
    void shouldReturnNoResources_whenFindingNulls() throws Exception {
        sqlFixture.readOnly();

        Collection<BAccount> uuidEntities = tested.find(Arrays.asList(null, null)).get();

        assertThat(uuidEntities).isEmpty();
    }

    @Test
    void shouldReturnOnlyExistingResources_whenFindingEexistingAndNonExistingResources() throws Exception {
        sqlFixture.readOnly();

        List<String> ids = List.of(system().getId(), admin().getId(), "1234");
        Collection<BAccount> accounts = tested.find(ids).get();

        assertThat(accounts)
                .usingFieldByFieldElementComparator()
                .containsOnly(system(), admin());
    }

    @Test
    void shouldReturnFalse_whenDeletingNonExistingResource() throws Exception {
        assertThat(tested.delete("1234").get()).isFalse();
    }

    @Test
    void shouldThrowException_whenDeletingNullId() {
        assertThrows(ExecutionException.class, () -> tested.delete((String) null).get());
    }

    @Test
    void shouldNotDeleteAnyResources_whenDeletingNonExistingResource() throws Exception {
        long accountCount = countRows();

        tested.delete("1234").get();

        long accountCountAfterDelete = countRows();

        assertThat(accountCount).isEqualTo(accountCountAfterDelete);
    }

    @Test
    void shouldNotDeleteAnyResources_whenDeletingNullId() {
        long accountCount = countRows();

        assertThrows(ExecutionException.class, () -> tested.delete((String) null).get());

        long accountCountAfterDelete = countRows();

        assertThat(accountCount).isEqualTo(accountCountAfterDelete);
    }

    @Test
    void shouldDeleteAResource() throws Exception {
        long accountCount = countRows();

        tested.delete(admin().getId()).get();

        long accountCountAfterCreate = countRows();

        assertThat(accountCountAfterCreate).isEqualTo(accountCount - 1);
    }

    @Test
    void shouldReturnTrue_whenDeletingResource() throws Exception {
        boolean delete = tested.delete(admin().getId()).get();

        assertThat(delete).isTrue();
    }

    @Test
    void shouldDeleteMultipleResources() throws Exception {
        List<String> ids = List.of(system().getId(), admin().getId());
        long accountCount = countRows();

        tested.delete(ids).get();

        long accountCountAfterDelete = countRows();

        assertThat(accountCountAfterDelete).isEqualTo(accountCount - ids.size());
    }

    @Test
    void shouldReturnTrue_whenDeletingMultipleResources() throws Exception {
        List<String> ids = List.of(system().getId(), admin().getId());
        boolean delete = tested.delete(ids).get();

        assertThat(delete).isTrue();
    }


    @Test
    void shouldNotDeleteAnyResources_whenDeletingNonExistingResources() throws Exception {
        List<String> ids = List.of("1234", "2345");
        long accountCount = countRows();

        tested.delete(ids).get();

        long accountCountAfterDelete = countRows();

        assertThat(accountCountAfterDelete).isEqualTo(accountCount);
    }

    @Test
    void shouldReturnFalse_whenNotDeletingAnyResources() throws Exception {
        List<String> ids = List.of("1234", "2345");
        boolean delete = tested.delete(ids).get();

        assertThat(delete).isFalse();
    }

    @Test
    void shouldDeleteOnlyExistingResources_whenDeletingExistingAndNonExistingResources() throws Exception {
        List<String> ids = List.of(system().getId(), admin().getId(), "1234");

        long accountCount = countRows();

        tested.delete(ids).get();

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