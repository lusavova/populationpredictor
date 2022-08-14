package com.example.populationpredictor.app.repositories;

import com.example.populationpredictor.app.models.Country;
import com.example.populationpredictor.app.models.PopulationInfo;
import com.example.populationpredictor.app.models.options.PagingOptions;
import com.example.populationpredictor.app.models.options.SortingOptions;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.populationpredictor.app.repositories.MySQLPopulationRepository.Queries.LIST_POPULATION_INFO;

@Repository
public class MySQLPopulationRepository implements PopulationsRepository {

    private final TransactionTemplate txTemplate;
    private final JdbcTemplate jdbc;

    public MySQLPopulationRepository(TransactionTemplate txTemplate, JdbcTemplate jdbc) {
        this.txTemplate = txTemplate;
        this.jdbc = jdbc;
    }

    @Override
    public Optional<PopulationInfo> getPopulationInfo(String country, int year) {
        PopulationInfo populationInfo = jdbc.queryForObject(Queries.GET_POPULATION_INFO,
                (rs, rowNum) -> fromResultSet(rs), country, year);

        if (populationInfo != null)
            return Optional.of(populationInfo);

        return Optional.empty();
    }

    @Override
    public List<PopulationInfo> listPopulationInfos(int year, PagingOptions pagingOptions, SortingOptions sortingOptions) {
        return jdbc.query(LIST_POPULATION_INFO,
                (rs, rowNum) -> fromResultSet(rs),
                pagingOptions.getPage() * pagingOptions.getPageSize(),
                sortingOptions.getField(), sortingOptions.getType());
    }

    @Override
    public void createPopulationInfo(String country, int year, long population) {
        txTemplate.executeWithoutResult(status -> {
            String countryID = UUID.randomUUID().toString();
            jdbc.update(Queries.INSERT_COUNTRY_IF_NOT_EXISTS, countryID, country);
            jdbc.update(Queries.INSERT_POPULATION_DATA, UUID.randomUUID().toString(), year, population, countryID);
        });
    }

    private PopulationInfo fromResultSet(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID countryID = UUID.fromString(rs.getString("country_id"));
        String country = rs.getString("country");
        int year = rs.getInt("year");
        long population = rs.getLong("population");

        return new PopulationInfo(id, new Country(countryID, country), year, population);
    }

    @Override
    public void createPopulationInfo(List<PopulationInfo> populationInfoList) {
        txTemplate.executeWithoutResult(status -> {
            jdbc.batchUpdate(Queries.INSERT_COUNTRY_IF_NOT_EXISTS,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            PopulationInfo populationInfo = populationInfoList.get(i);
                            ps.setString(1, populationInfo.getCountry().getId().toString());
                            ps.setString(2, populationInfo.getCountry().getName());
                        }

                        public int getBatchSize() {
                            return populationInfoList.size();
                        }
                    });

            jdbc.batchUpdate(Queries.INSERT_POPULATION_DATA,
                    new BatchPreparedStatementSetter() {

                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            PopulationInfo populationInfo = populationInfoList.get(i);
                            ps.setString(1, populationInfo.getId().toString());
                            ps.setInt(2, populationInfo.getYear());
                            ps.setLong(3, populationInfo.getPopulation());
                            ps.setString(4, populationInfo.getCountry().getId().toString());
                        }

                        public int getBatchSize() {
                            return populationInfoList.size();
                        }
                    });
        });
    }

    @Override
    public int getLatestGeneratedPopulationYear() {
        /*
        SELECT year FROM countries as c
        JOIN population_info as p
        on c.id = p.country_id
        WHERE c.name = "BG"
        ORDER BY p.year DESC
        LIMIT 1;
        */
        return 0;
    }

    static class Queries {
        public static final String INSERT_COUNTRY_IF_NOT_EXISTS =
                "INSERT INTO country (id, name) " +
                        "VALUES (?, ?)";
        public static final String INSERT_POPULATION_DATA =
                "INSERT INTO population_data (id, year, population, country_id) " +
                        "VALUES (?, ?, ?, ?)";
        public static final String GET_POPULATION_INFO =
                "SELECT p.id, c.id AS country_id, c.name AS country, p.year, p.population " +
                        "FROM population_data as p\n" +
                        "JOIN country as c \n" +
                        "ON c.id = p.country_id\n" +
                        "WHERE c.name = ? AND p.year = ?;";

        public static final String LIST_POPULATION_INFO =
                "SELECT p.id, c.name AS country, p.year, p.population \n" +
                        "FROM population_data as p\n" +
                        "JOIN country as c \n" +
                        "ON c.id = p.country_id\n" +
                        "ORDER BY ? ?\n" +
                        "LIMIT ?;";
    }
}
