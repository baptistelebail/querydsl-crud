package com.blebail.querydsl.crud.sync.repository;

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

import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.admin;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.admin2;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.admin3;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.adminClone;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.insertAdmin2;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.insertAdmin3;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.insertAdminClone;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.insertDefaultAccounts;
import static com.blebail.querydsl.crud.sync.fixtures.AccountFixtures.system;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QDSLBaseRepositoryIT {

    @RegisterExtension
    static SqlMemoryDb sqlMemoryDb = new SqlMemoryDb();

    @RegisterExtension
    SqlFixture sqlFixture = new SqlFixture(sqlMemoryDb::dataSource, insertDefaultAccounts());

    BaseRepository<BAccount> tested;

    @BeforeEach
    void setUp() {
        tested = new QDSLBaseRepository<>(
                new QDSLResource<>(QAccount.account),
                Factories.defaultQueryFactory(sqlMemoryDb.dataSource())
        );
    }

    @Test
    public void shouldCountResources() {
        sqlFixture.readOnly();

        assertThat(tested.count()).isEqualTo(countRows());
    }

    @Test
    public void shouldCountAllResources_whenCountingWithNullPredicate() {
        sqlFixture.readOnly();

        assertThat(tested.count(null)).isEqualTo(countRows());
    }

    @Test
    public void shouldReturnZero_whenCountingWithPredicateThatDoesntMachAnyResource() {
        sqlFixture.readOnly();

        Predicate noMatchPredicate = QAccount.account.username.eq("Foo");
        assertThat(tested.count(noMatchPredicate)).isEqualTo(0);
    }

    @Test
    public void shouldCountResourcesWithPredicate() {
        sqlFixture.readOnly();

        Predicate predicate = QAccount.account.username.eq("system");

        assertThat(tested.count(predicate)).isEqualTo(1);
    }

    @Test
    public void shouldThrowException_whenFindingResourceWithNullPredicate() {
        sqlFixture.readOnly();

        assertThrows(NullPointerException.class, () -> tested.findOne(null));
    }

    @Test
    public void shouldReturnEmpty_whenPredicateDoesntMatchAnyRow() {
        sqlFixture.readOnly();

        Predicate predicate = QAccount.account.username.eq("Foo");

        assertThat(tested.findOne(predicate)).isEmpty();
    }

    @Test
    public void shouldFindResource_accordingToPredicate() {
        sqlFixture.readOnly();

        Predicate predicate = QAccount.account.username.eq("system");

        assertThat(tested.findOne(predicate)).usingFieldByFieldValueComparator().contains(system());
    }

    @Test
    public void shouldFindAllResources_whenPredicateIsNull() {
        sqlFixture.readOnly();

        assertThat(tested.find((Predicate) null).size()).isEqualTo(countRows());
    }

    @Test
    public void shouldReturnEmptyCollection_whenPredicateDoesntMatchAnyResource() {
        sqlFixture.readOnly();

        Predicate predicate = QAccount.account.username.eq("Foo");

        assertThat(tested.find(predicate)).isEmpty();
    }

    @Test
    public void shouldFindResources_accordingToPredicate() {
        sqlFixture.exec(insertAdmin2());

        Predicate predicate = QAccount.account.username.startsWith("admin");

        assertThat(tested.find(predicate)).usingFieldByFieldElementComparator().containsOnly(
                admin(),
                admin2()
        );
    }

    @Test
    public void shouldFindAllResources() {
        sqlFixture.readOnly();

        assertThat(tested.findAll().size()).isEqualTo(countRows());
    }

    @Test
    public void shouldFindPage1WithSize1SortedByIdDesc() {
        sqlFixture.exec(insertAdmin2());

        long accountCount = countRows();

        Page<BAccount> page = tested.find(new PageRequest(1, 1, List.of(new Sort("id", Sort.Direction.DESC))));

        assertThat(page.items())
                .usingFieldByFieldElementComparator()
                .contains(admin());

        assertThat(page.size()).isEqualTo(1);
        assertThat(page.totalItems()).isEqualTo(accountCount);
        assertThat(page.totalPages()).isEqualTo(accountCount);
    }

    @Test
    public void shouldApplySortsInTheSameOrderAsTheyWereRequested() {
        sqlFixture.exec(insertAdminClone());

        List<Sort> sorts = Arrays.asList(new Sort("username", Sort.Direction.ASC), new Sort("email", Sort.Direction.DESC));
        Page<BAccount> page = tested.find(new PageRequest(0, (int) countRows(), sorts));

        assertThat(page.items())
                .usingFieldByFieldElementComparator()
                .containsExactly(
                        adminClone(),
                        admin(),
                        system()
                );
    }

    @Test
    public void shouldFindPageWithPredicate() {
        sqlFixture.exec(insertAdmin2());
        sqlFixture.exec(insertAdmin3());

        Predicate predicate = QAccount.account.username.contains("admin");
        PageRequest pageRequest = new PageRequest(0, 2, List.of(new Sort("id", Sort.Direction.DESC)));

        Page<BAccount> page = tested.find(predicate, pageRequest);

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

        assertThrows(NullPointerException.class, () -> tested.delete(null));
    }

    @Test
    public void shouldNotDeleteAnyResource_whenDeletingWithPredicateThatDoesntMachAnyResource() {
        sqlFixture.readOnly();

        long accountCount = countRows();

        Predicate noMatchPredicate = QAccount.account.username.eq("Foo");
        assertThat(tested.delete(noMatchPredicate)).isFalse();

        assertThat(countRows()).isEqualTo(accountCount);
    }

    @Test
    public void shouldDeleteResourcesWithPredicate() {
        sqlFixture.exec(insertAdmin2());

        long accountCount = countRows();

        Predicate predicate = QAccount.account.username.startsWith("admin");

        assertThat(tested.delete(predicate)).isTrue();
        assertThat(countRows()).isEqualTo(accountCount - 2);
    }

    @Test
    public void shouldDeleteAllResources() {
        tested.deleteAll();

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