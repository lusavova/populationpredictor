package com.example.populationpredictor.app.repositories;

import com.example.populationpredictor.app.models.Country;
import com.example.populationpredictor.app.models.PopulationInfo;
import com.example.populationpredictor.app.models.options.PagingOptions;
import com.example.populationpredictor.app.models.options.SortingOptions;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.populationpredictor.app.repositories.MySQLPopulationRepository.Queries.*;

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
        try {
            return Optional.ofNullable(jdbc.queryForObject(Queries.GET_POPULATION_INFO,
                    (rs, rowNum) -> populationFromResultSet(rs), country.toLowerCase(), year));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<PopulationInfo> listPopulationInfos(int year, PagingOptions pagingOptions, SortingOptions sortingOptions) {
        return jdbc.query(getListPopulationInfo(sortingOptions),
                (rs, rowNum) -> populationFromResultSet(rs),
                year,
                pagingOptions.getPage() * pagingOptions.getPageSize(), pagingOptions.getPageSize());
    }

    private Map<String, Country> getCountries() {
        return jdbc.query(LIST_COUNTRIES,
                        (rs, rowNum) -> countryFromResultSet(rs)).stream()
                .collect(Collectors.toMap(Country::getName, (c) -> c));
    }

    @Override
    public void createPopulationInfo(String country, int year, long population) {
        txTemplate.executeWithoutResult(status -> {
            String countryID = UUID.randomUUID().toString();
            jdbc.update(Queries.INSERT_COUNTRY_IF_NOT_EXISTS, countryID, country);
            jdbc.update(Queries.INSERT_POPULATION_DATA, UUID.randomUUID().toString(), year, population, countryID);
        });
    }

    private PopulationInfo populationFromResultSet(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        UUID countryID = UUID.fromString(rs.getString("country_id"));
        String country = rs.getString("country");
        int year = rs.getInt("year");
        long population = rs.getLong("population");

        return new PopulationInfo(id, new Country(countryID, country), year, population);
    }

    private Country countryFromResultSet(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String country = rs.getString("name");

        return new Country(id, country);
    }


    @Override
    public void createPopulationInfo(List<PopulationInfo> populationInfoList) {
        txTemplate.executeWithoutResult(status -> {
            Map<String, Country> countries = getCountries();
            for (PopulationInfo populationInfo : populationInfoList) {
                Country country = populationInfo.getCountry();
                if (countries.containsKey(country.getName()))
                    populationInfo.getCountry().setId(countries.get(country.getName()).getId());
            }

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

    static class Queries {
        public static final String INSERT_COUNTRY_IF_NOT_EXISTS =
                "INSERT IGNORE INTO countries (id, name) " +
                        "VALUES (?, ?)";
        public static final String INSERT_POPULATION_DATA =
                "INSERT IGNORE INTO population_info (id, year, population, country_id) " +
                        "VALUES (?, ?, ?, ?)";
        public static final String GET_POPULATION_INFO =
                "SELECT p.id, c.id AS country_id, c.name AS country, p.year, p.population " +
                        "FROM population_info as p\n" +
                        "JOIN countries as c \n" +
                        "ON c.id = p.country_id\n" +
                        "WHERE LOWER(c.name) = ? AND p.year = ?;";

        public static String getListPopulationInfo(SortingOptions sortingOptions) {
            return String.format("SELECT p.id, p.country_id, c.name AS country, p.year, p.population \n" +
                    "FROM population_info as p \n" +
                    "JOIN countries as c \n" +
                    "ON c.id = p.country_id \n" +
                    "WHERE p.year = ? \n" +
                    "ORDER BY %s %s \n" +
                    "LIMIT ?, ?;", sortingOptions.getField(), sortingOptions.getType().name());
        }

        public static final String LIST_POPULATION_INFO =
                "SELECT p.id, p.country_id, c.name AS country, p.year, p.population \n" +
                        "FROM population_info as p \n" +
                        "JOIN countries as c \n" +
                        "ON c.id = p.country_id \n" +
                        "WHERE p.year = ? \n" +
                        "ORDER BY ? ? \n" +
                        "LIMIT ?, ?;";

        public static final String LIST_COUNTRIES =
                "SELECT id, name FROM countries;";
    }
}
