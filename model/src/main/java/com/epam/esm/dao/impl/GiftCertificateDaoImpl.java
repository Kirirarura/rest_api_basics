package com.epam.esm.dao.impl;

import com.epam.esm.dao.AbstractDao;
import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.query.Queries;
import com.epam.esm.dao.query.QueryBuilder;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exceptions.DaoException;
import com.epam.esm.extractor.GiftCertificateExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.epam.esm.exceptions.DaoExceptionCodes.NO_ENTITY_WITH_ID;
import static com.epam.esm.exceptions.DaoExceptionCodes.SAVING_ERROR;


/**
 * Class implementation of interface {@link GiftCertificateDao} designed to work with gift certificate table.
 */
@Repository
public class GiftCertificateDaoImpl extends AbstractDao<GiftCertificate> implements GiftCertificateDao {

    private static final String TABLE_NAME = "gift_certificates";
    private final TagDao tagDao;
    private final GiftCertificateExtractor giftCertificateExtractor;
    private final QueryBuilder queryBuilder;

    private static final RowMapper<GiftCertificate> ROW_MAPPER =
            (rs, rowNum) -> new GiftCertificate(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBigDecimal("price"),
                    rs.getInt("duration"),
                    rs.getString("create_date"),
                    rs.getString("last_update_date"));

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GiftCertificateDaoImpl(TagDao tagDao, GiftCertificateExtractor giftCertificateExtractor,
                                  QueryBuilder queryBuilder, JdbcTemplate jdbcTemplate) {
        super(ROW_MAPPER, TABLE_NAME, queryBuilder, jdbcTemplate);
        this.tagDao = tagDao;
        this.giftCertificateExtractor = giftCertificateExtractor;
        this.queryBuilder = queryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void create(GiftCertificate giftCertificate, List<Tag> tags) throws DaoException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(Queries.CREATE_CERTIFICATE, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, giftCertificate.getName());
                ps.setString(2, giftCertificate.getDescription());
                ps.setString(3, String.valueOf(giftCertificate.getPrice()));
                ps.setString(4, String.valueOf(giftCertificate.getDuration()));
                ps.setString(5, giftCertificate.getCreateDate());
                ps.setString(6, giftCertificate.getLastUpdateDate());
                return ps;
            }, keyHolder);
            updateTags(tags, keyHolder.getKey().intValue());
        } catch (DataAccessException e) {
            throw new DaoException(SAVING_ERROR);
        }
    }

    @Override
    public Optional<GiftCertificate> findByName(String name) throws DaoException {
        return findByColumn("name", name);
    }

    private void updateTags(List<Tag> tags, long giftCertificateId) {
        if (tags == null) {
            return;
        }
        List<Long> tagIds = getTagsIds(tags);
        for (Long tagId : tagIds) {
            executeUpdateQuery(Queries.CREATE_CERTIFICATE_TAG_REFERENCE, giftCertificateId, tagId);
        }
    }

    private List<Long> getTagsIds(List<Tag> tags) {
        List<Long> tagIds = new ArrayList<>();

        tags.forEach(tag -> {
            String tagName = tag.getName();
            Optional<Tag> tagWithId = Optional.empty();

            try {
                tagWithId = tagDao.findByName(tagName);
            } catch (DaoException e) {
                e.printStackTrace();
            }

            tagWithId.ifPresent(value -> tagIds.add(value.getId()));
        });
        return tagIds;
    }

    @Override
    @Transactional
    public void update(GiftCertificate giftCertificate, List<Tag> certificateTags) throws DaoException {
        Map<String, String> updateFields = giftCertificateExtractor.extract(giftCertificate);
        String query = queryBuilder.createUpdateQuery(updateFields, TABLE_NAME);
        try {
            executeUpdateQuery(query);
            updateTags(certificateTags, giftCertificate.getId());
        } catch (DataAccessException e) {
            throw new DaoException(NO_ENTITY_WITH_ID);
        }
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }
}
