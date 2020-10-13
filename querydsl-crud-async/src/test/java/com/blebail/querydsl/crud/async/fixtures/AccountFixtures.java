package com.blebail.querydsl.crud.async.fixtures;

import com.blebail.querydsl.crud.BAccount;
import com.blebail.querydsl.crud.QAccount;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;

public final class AccountFixtures {

    public static BAccount system() {
        return build("0001", "system", "system@test.com");
    }

    public static BAccount admin() {
        return build("0002", "admin", "admin@test.com");
    }

    public static BAccount adminClone() {
        return build("0003", "admin", "adminclone@test.com");
    }

    public static BAccount admin2() {
        return build("0004", "admin2", "admin2@test.com");
    }

    public static BAccount admin3() {
        return build("0005", "admin3", "admin3@test.com");
    }

    public static BAccount johnDoe() {
        return build("0006", "johndoe", "johndoe@test.com");
    }

    private static BAccount build(String id, String username, String email) {
        return new BAccount(email, id, username);
    }

    public static Operation insertDefaultAccounts() {
        return Operations.sequenceOf(
                Operations.deleteAllFrom(QAccount.account.getTableName()),
                insert(system()),
                insert(admin())
        );
    }

    public static Operation insertAdminClone() {
        return insert(adminClone());
    }

    public static Operation insertAdmin2() {
        return insert(admin2());
    }

    public static Operation insertAdmin3() {
        return insert(admin3());
    }

    public static Operation insertJohnDoe() {
        return insert(johnDoe());
    }

    private static Operation insert(BAccount account) {
        return Operations.insertInto(QAccount.account.getTableName())
                .columns("id", "username", "email")
                .values(account.getId(), account.getUsername(), account.getEmail())
                .build();
    }
}
