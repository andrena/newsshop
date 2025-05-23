package com.andrena.newsshop.repository;

import com.andrena.newsshop.model.LaunchCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class LaunchCodesRepository {

    private final DataSource dataSource;

    @Autowired
    public LaunchCodesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(LaunchCode launchCode) {
        if (launchCode.getId() == null) {
            insert(launchCode);
        } else {
            update(launchCode);
        }
    }

    private void insert(LaunchCode launchCode) {
        execute(String.format("INSERT INTO launch_code (device, launch_code) VALUES ('%s', '%s')",
                launchCode.getDevice(), launchCode.getLaunchCode()));
    }

    private void update(LaunchCode launchCode) {
        execute(String.format("UPDATE launch_code SET device = '%s', launch_code = '%s' WHERE id = %d",
                launchCode.getDevice(), launchCode.getLaunchCode(), launchCode.getId()));
    }

    public Optional<LaunchCode> findByDevice(String device) {
        return queryOne("SELECT * FROM launch_code WHERE device = '" + device + "'", this::mapRowToLaunchCode);
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

    private LaunchCode mapRowToLaunchCode(ResultSet rs) {
        try {
            LaunchCode launchCode = new LaunchCode();
            launchCode.setId(rs.getLong("id"));
            launchCode.setDevice(rs.getString("device"));
            launchCode.setLaunchCode(rs.getString("launch_code"));
            return launchCode;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
