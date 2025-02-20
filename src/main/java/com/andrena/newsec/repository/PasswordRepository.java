package com.andrena.newsec.repository;

import com.andrena.newsec.model.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class PasswordRepository {

    private final DataSource dataSource;

    @Autowired
    public PasswordRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Password password) {
        if (password.getId() == null) {
            insert(password);
        } else {
            update(password);
        }
    }

    private void insert(Password password) {
        execute(String.format("INSERT INTO password (username, password) VALUES ('%s', '%s')",
                password.getUsername(), password.getPassword()));
    }

    private void update(Password password) {
        execute(String.format("UPDATE password SET username = '%s', password = '%s' WHERE id = %d",
                password.getUsername(), password.getPassword(), password.getId()));
    }

    public Optional<Password> findByUsername(String username) {
        return queryOne("SELECT * FROM password WHERE username = '" + username + "'", this::mapRowToPassword);
    }

    private void execute(String query) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Optional<T> queryOne(String query, Function<ResultSet, T> mapping) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return Optional.of(mapping.apply(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Password mapRowToPassword(ResultSet rs) {
        try {
            Password password = new Password();
            password.setId(rs.getLong("id"));
            password.setUsername(rs.getString("username"));
            password.setPassword(rs.getString("password"));
            return password;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
