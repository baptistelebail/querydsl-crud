package com.blebail.querydsl.crud.async.repository;

import com.blebail.junit.SqlFixture;
import com.blebail.junit.SqlMemoryDb;
import com.blebail.querydsl.crud.BAccount;
import com.blebail.querydsl.crud.QAccount;
import com.blebail.querydsl.crud.commons.page.Page;
import com.blebail.querydsl.crud.commons.page.PageRequest;
import com.blebail.querydsl.crud.commons.page.Sort;
import com.blebail.querydsl.crud.commons.resource.QDSLResource;
import com.blebail.querydsl.crud.commons.utils.Factories;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.admin;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.admin2;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.admin3;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.adminClone;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.insertAdmin2;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.insertAdmin3;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.insertAdminClone;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.insertDefaultAccounts;
import static com.blebail.querydsl.crud.async.fixtures.AccountFixtures.system;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QDSLAsyncBaseRepositoryIT {

    @RegisterExtension
    static SqlMemoryDb sqlMemoryDb = new SqlMemoryDb();

    @RegisterExtension
    SqlFixture sqlFixture = new SqlFixture(sqlMemoryDb::dataSource, insertDefaultAccounts());

    AsyncBaseRepository<BAccount> tested;

    @BeforeEach
    void setUp() {
        tested = new QDSLAsyncBaseRepository<>(
                new QDSLResource<>(QAccount.account),
                Factories.defaultQueryFactory(sqlMemoryDb.dataSource())
        );
    }

    @Test
    public void shouldCountResources() throws Exception {
        sqlFixture.readOnly();

        assertThat(tested.count().get()).isEqualTo(countRows());
    }

    @Test
    public void shouldCountAllResources_whenCountingWithNullPredicate() throws Exception {
        sqlFixture.readOnly();

        assertThat(tested.count(null).get()).isEqualTo(countRows());
    }

    @Test
    public void shouldReturnZero_whenCountingWithPredicateThatDoesntMachAnyResource() throws Exception {
        sqlFixture.readOnly();

        Predicate noMatchPredicate = QAccount.account.username.eq("Foo");
        assertThat(tested.count(noMatchPredicate).get()).isEqualTo(0);
    }

    @Test
    public void shouldCountResourcesWithPredicate() throws Exception {
        sqlFixture.readOnly();

        Predicate predicate = QAccount.account.username.eq("system");

        assertThat(tested.count(predicate).get()).isEqualTo(1);
    }

    @Test
    public void shouldThrowException_whenFindingResourceWithNullPredicate() {
        sqlFixture.readOnly();

        assertThrows(ExecutionException.class, () -> tested.findOne(null).get());
    }

    @Test
    public void shouldReturnEmpty_whenPredicateDoesntMatchAnyRow() throws Exception {
        sqlFixture.readOnly();

        Predicate predicate = QAccount.account.username.eq("Foo");

        assertThat(tested.findOne(predicate).get()).isEmpty();
    }

    @Test
    public void shouldFindResource_accordingToPredicate() throws Exception {
        sqlFixture.readOnly();

        Predicate predicate = QAccount.account.username.eq("system");

        assertThat(tested.findOne(predicate).get()).usingFieldByFieldValueComparator().contains(system());
    }

    @Test
    public void shouldFindAllResources_whenPredicateIsNull() throws Exception {
        sqlFixture.readOnly();

        assertThat(tested.find((Predicate) null).get().size()).isEqualTo(countRows());
    }

    @Test
    public void shouldReturnEmptyCollection_whenPredicateDoesntMatchAnyResource() throws Exception {
        sqlFixture.readOnly();

        Predicate predicate = QAccount.account.username.eq("Foo");

        assertThat(tested.find(predicate).get()).isEmpty();
    }

    @Test
    public void shouldFindResources_accordingToPredicate() throws Exception {
        sqlFixture.exec(insertAdmin2());

        Predicate predicate = QAccount.account.username.startsWith("admin");

        assertThat(tested.find(predicate).get()).usingFieldByFieldElementComparator().containsOnly(
                admin(),
                admin2()
        );
    }

    @Test
    public void shouldFindAllResources() throws Exception {
        sqlFixture.readOnly();

        assertThat(tested.findAll().get().size()).isEqualTo(countRows());
    }

    @Test
    public void shouldFindPage1WithSize1SortedByIdDesc() throws Exception {
        sqlFixture.exec(insertAdmin2());

        long accountCount = countRows();

        Page<BAccount> page = tested.find(new PageRequest(1, 1, List.of(new Sort("id", Sort.Direction.DESC)))).get();

        assertThat(page.items())
                .usingFieldByFieldElementComparator()
                .contains(admin());

        assertThat(page.size()).isEqualTo(1);
        assertThat(page.totalItems()).isEqualTo(accountCount);
        assertThat(page.totalPages()).isEqualTo(accountCount);
    }

    @Test
    public void shouldApplySortsInTheSameOrderAsTheyWereRequested() throws Exception {
        sqlFixture.exec(insertAdminClone());

        List<Sort> sorts = Arrays.asList(new Sort("username", Sort.Direction.ASC), new Sort("email", Sort.Direction.DESC));
        Page<BAccount> page = tested.find(new PageRequest(0, (int) countRows(), sorts)).get();

        assertThat(page.items())
                .usingFieldByFieldElementComparator()
                .containsExactly(
                        adminClone(),
                        admin(),
                        system()
                );
    }

    @Test
    public void shouldFindPageWithPredicate() throws Exception {
        sqlFixture.exec(insertAdmin2());
        sqlFixture.exec(insertAdmin3());

        Predicate predicate = QAccount.account.username.contains("admin");
        PageRequest pageRequest = new PageRequest(0, 2, List.of(new Sort("id", Sort.Direction.DESC)));

        Page<BAccount> page = tested.find(predicate, pageRequest).get();

        assertThat(page.items()).usingFieldByFieldElementComparator().containsOnly(
                admin3(),
                admin2()
        );

        assertThat(page.totalItems()).isEqualTo(3);
        assertThat(page.totalPages()).isEqualTo(2);
    }

    @Test
    public void shouldThrowException_whenDeletingWithNullPredicate() {
        sqlFixture.readOnly();

        assertThrows(ExecutionException.class, () -> tested.delete(null).get());
    }

    @Test
    public void shouldNotDeleteAnyResource_whenDeletingWithPredicateThatDoesntMachAnyResource() throws Exception {
        sqlFixture.readOnly();

        long accountCount = countRows();

        Predicate noMatchPredicate = QAccount.account.username.eq("Foo");
        assertThat(tested.delete(noMatchPredicate).get()).isFalse();

        assertThat(countRows()).isEqualTo(accountCount);
    }

    @Test
    public void shouldDeleteResourcesWithPredicate() throws Exception {
        sqlFixture.exec(insertAdmin2());

        long accountCount = countRows();

        Predicate predicate = QAccount.account.username.startsWith("admin");

        assertThat(tested.delete(predicate).get()).isTrue();
        assertThat(countRows()).isEqualTo(accountCount - 2);
    }

    @Test
    public void shouldDeleteAllResources() throws Exception {
        tested.deleteAll().get();

        long accountCountAfterDelete = countRows();

        assertThat(accountCountAfterDelete).isEqualTo(0);
    }

    private long countRows() {
        return Factories.defaultQueryFactory(sqlMemoryDb.dataSource())
                .select(QAccount.account)
                .from(QAccount.account)
                .fetchCount();
    }
}