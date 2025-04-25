package com.andrena.newsec.repository;

import com.andrena.newsec.model.Newsletter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Repository
public class NewsletterRepository {

    private final DataSource dataSource;

    @Autowired
    public NewsletterRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Newsletter> findAll() {
        String sql = "SELECT * FROM newsletter";
        return execute(sql, (preparedStatement -> {
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                return getListResult(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private List<Newsletter> getListResult(ResultSet resultSet) throws SQLException {
        List<Newsletter> all = new ArrayList<>();
        while (resultSet.next()) {
            all.add(mapRowToNewsletter(resultSet));
        }
        return all;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM newsletter WHERE id = ?";
        executeUpdate(sql, (preparedStatement -> {
            try {
                preparedStatement.setLong(1, id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public void save(Newsletter newsletter) {
        if (newsletter.getId() == null) {
            insert(newsletter);
        } else {
            update(newsletter);
        }
    }

    private void insert(Newsletter newsletter) {
        String sql = "INSERT INTO newsletter (email, name, source, confirmed, mail_properties) VALUES (?, ?, ?, ? ,?)";
        executeUpdate(sql, (preparedStatement -> {
            try {
                preparedStatement.setString(1, newsletter.getEmail());
                preparedStatement.setString(2, escape(newsletter.getName()));
                preparedStatement.setString(3, escape(newsletter.getSource()));
                preparedStatement.setBoolean(4, newsletter.isConfirmed());
                preparedStatement.setString(5, escape(newsletter.getMailProperties()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private void update(Newsletter newsletter) {
        String sql = "UPDATE newsletter SET email = ?, name = ?, source = ?, confirmed = ?, mail_properties = ? WHERE id = ?";
        executeUpdate(sql, (preparedStatement -> {
            try {
                preparedStatement.setString(1, newsletter.getEmail());
                preparedStatement.setString(2, escape(newsletter.getName()));
                preparedStatement.setString(3, escape(newsletter.getSource()));
                preparedStatement.setBoolean(4, newsletter.isConfirmed());
                preparedStatement.setString(5, escape(newsletter.getMailProperties()));
                preparedStatement.setLong(6, newsletter.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private void executeUpdate(String sql, Consumer<PreparedStatement> prep) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)
        ) {
            prep.accept(preparedStatement);
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
        String sql = "SELECT * from newsletter WHERE email = ? or name = ?";
        return execute(sql, (preparedStatement -> {
            try {
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
        }));
    }

    public List<Newsletter> findLikeEmailOrName(String email, String name) {
        String sql = "SELECT * FROM newsletter WHERE email LIKE ? or name LIKE ?";
        return execute(sql, (preparedStatement -> {
            try {
                preparedStatement.setString(1, "%" + email + "%");
                preparedStatement.setString(2, "%" + name + "%");
                ResultSet resultSet = preparedStatement.executeQuery();
                return getListResult(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    private <R> R execute(String sql, Function<PreparedStatement, R> preparedStatementRFunction) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)
        ) {
            return preparedStatementRFunction.apply(preparedStatement);
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
