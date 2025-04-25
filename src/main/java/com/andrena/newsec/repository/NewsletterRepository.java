package com.andrena.newsec.repository;

import com.andrena.newsec.model.Newsletter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NewsletterRepository {

    private final DataSource dataSource;

    @Autowired
    public NewsletterRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Newsletter> findAll() {
        List<Newsletter> all = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM newsletter");
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                all.add(mapRowToNewsletter(resultSet));
            }
            return all;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(Long id) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM newsletter WHERE id = ?")
        ) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Newsletter newsletter) {
        if (newsletter.getId() == null) {
            insert(newsletter);
        } else {
            update(newsletter);
        }
    }

    private void insert(Newsletter newsletter) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO newsletter (email, name, source, confirmed, mail_properties) VALUES (?, ?, ?, ? ,?)")
        ) {
            preparedStatement.setString(1, newsletter.getEmail());
            preparedStatement.setString(2, escape(newsletter.getName()));
            preparedStatement.setString(3, escape(newsletter.getSource()));
            preparedStatement.setBoolean(4, newsletter.isConfirmed());
            preparedStatement.setString(5, escape(newsletter.getMailProperties()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(Newsletter newsletter) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement("UPDATE newsletter SET email = ?, name = ?, source = ?, confirmed = ?, mail_properties = ? WHERE id = ?")
        ) {
            preparedStatement.setString(1, newsletter.getEmail());
            preparedStatement.setString(2, escape(newsletter.getName()));
            preparedStatement.setString(3, escape(newsletter.getSource()));
            preparedStatement.setBoolean(4, newsletter.isConfirmed());
            preparedStatement.setString(5, escape(newsletter.getMailProperties()));
            preparedStatement.setLong(6, newsletter.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String escape(String name) {
        if (name == null) {
            return name;
        }
        return name.replace("'", "''");
    }

    public Optional<Newsletter> findByEmailOrName(String email, String name) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from newsletter WHERE email = ? or name = ?")
        ){
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapRowToNewsletter(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Newsletter> findLikeEmailOrName(String email, String name) {
        List<Newsletter> all = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM newsletter WHERE email LIKE ? or name LIKE ?")
        ) {
            preparedStatement.setString(1, "%" + email + "%");
            preparedStatement.setString(2, "%" + name + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                all.add(mapRowToNewsletter(resultSet));
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
