package com.andrena.newsec.repository;

import com.andrena.newsec.model.Newsletter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Repository
public class NewsletterRepository {

    private final DataSource dataSource;

    @Autowired
    public NewsletterRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Newsletter> findAll() {
        return queryAll("SELECT * FROM newsletter", this::mapRowToNewsletter);
    }

    public void deleteById(Long id) {
        execute("DELETE FROM newsletter WHERE id = " + id);
    }

    public void save(Newsletter newsletter) {
        if (newsletter.getId() == null) {
            insert(newsletter);
        } else {
            update(newsletter);
        }
    }

    private void insert(Newsletter newsletter) {
        execute(String.format("INSERT INTO newsletter (email, name, source, confirmed) VALUES ('%s', '%s', '%s', %b)",
                newsletter.getEmail(), newsletter.getName(), newsletter.getSource(), newsletter.isConfirmed()));
    }

    private void update(Newsletter newsletter) {
        execute(String.format("UPDATE newsletter SET email = '%s', name = '%s', source = '%s', confirmed = %b WHERE id = %d",
                newsletter.getEmail(), newsletter.getName(), newsletter.getSource(), newsletter.isConfirmed(), newsletter.getId()));
    }

    public Optional<Newsletter> findByEmail(String email) {
        return queryOne("SELECT * FROM newsletter WHERE email = '" + email + "'", this::mapRowToNewsletter);
    }

    public List<Newsletter> findByName(String namePart) {
        return queryAll("SELECT * FROM newsletter WHERE name LIKE '%" + namePart + "%'", this::mapRowToNewsletter);
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }


    public void confirmSubscription(String email) {
        execute("UPDATE newsletter SET confirmed = true WHERE email = '" + email + "'");
    }

    public List<Newsletter> findByPreference(String preference) {
        return queryAll("SELECT * FROM newsletter WHERE preferences LIKE '%" + preference + "%'", this::mapRowToNewsletter);
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

    private <T> List<T> queryAll(String query, Function<ResultSet, T> mapping) {
        List<T> all = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                all.add(mapping.apply(rs));
            }
            return all;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Newsletter mapRowToNewsletter(ResultSet rs) {
        try {
            Newsletter newsletter = new Newsletter();
            newsletter.setId(rs.getLong("id"));
            newsletter.setEmail(rs.getString("email"));
            newsletter.setName(rs.getString("name"));
            newsletter.setSource(rs.getString("source"));
            newsletter.setConfirmed(rs.getBoolean("confirmed"));
            return newsletter;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
