package com.blebail.querydsl.crud.commons.utils;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.types.JSR310InstantType;

import javax.sql.DataSource;

public final class Factories {

    public static SQLQueryFactory defaultQueryFactory(DataSource dataSource) {
        SQLTemplates templates = SQLTemplates.DEFAULT;
        Configuration configuration = new Configuration(templates);
        configuration.register(new JSR310InstantType());

        return new SQLQueryFactory(configuration, dataSource);
    }
}
