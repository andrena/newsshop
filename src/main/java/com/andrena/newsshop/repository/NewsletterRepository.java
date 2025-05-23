package com.andrena.newsshop.repository;

import com.andrena.newsshop.model.Newsletter;
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
        execute(String.format("INSERT INTO newsletter (email, name, source, confirmed, mail_properties) VALUES ('%s', '%s', '%s', %b, '%s')",
                newsletter.getEmail(), newsletter.getName(), escape(newsletter.getSource()), newsletter.isConfirmed(), escape(newsletter.getMailProperties())));
    }

    private void update(Newsletter newsletter) {
        execute(String.format("UPDATE newsletter SET email = '%s', name = '%s', source = '%s', confirmed = %b, mail_properties = '%s' WHERE id = %d",
                newsletter.getEmail(), newsletter.getName(), escape(newsletter.getSource()), newsletter.isConfirmed(), escape(newsletter.getMailProperties()), newsletter.getId()));
    }

    private String escape(String name) {
        if (name == null) {
            return name;
        }
        return name.replace("'", "''");
    }

    public Optional<Newsletter> findByEmailOrName(String email, String name) {
        return queryOne("SELECT * FROM newsletter WHERE email = '" + email + "' or name = '" + name + "'", this::mapRowToNewsletter);
    }

    public List<Newsletter> findLikeEmailOrName(String email, String name) {
        return queryAll("SELECT * FROM newsletter WHERE email LIKE '%" + email + "%' or name LIKE '%" + name + "%'", this::mapRowToNewsletter);

    }

    public List<Newsletter> findByName(String namePart) {
        return queryAll("SELECT * FROM newsletter WHERE name LIKE '%" + namePart + "%'", this::mapRowToNewsletter);
    }

    public void confirmSubscription(String email) {
        execute("UPDATE newsletter SET confirmed = true WHERE email = '" + email + "'");
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
            newsletter.setMailProperties(rs.getString("mail_properties"));
            return newsletter;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
