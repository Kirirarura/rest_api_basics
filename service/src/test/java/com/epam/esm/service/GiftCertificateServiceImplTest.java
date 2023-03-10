package com.epam.esm.service;


import com.epam.esm.dao.impl.GiftCertificateDaoImpl;
import com.epam.esm.dao.impl.TagDaoImpl;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DuplicateEntityException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.exception.InvalidIdException;
import com.epam.esm.exception.NoSuchEntityException;
import com.epam.esm.exceptions.DaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import static com.epam.esm.constants.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceImplTest {

    @Mock
    private GiftCertificateDaoImpl giftCertificateDao = Mockito.mock(GiftCertificateDaoImpl.class);
    @InjectMocks
    private GiftCertificateServiceImpl giftCertificateService;

    @Mock
    private TagDaoImpl tagDao = Mockito.mock(TagDaoImpl.class);
    @InjectMocks
    private TagsServiceImpl tagsService;

    private static final Tag TAG_1 = new Tag(1L, "tag1");
    private static final Tag TAG_2 = new Tag(2L, "tag2");
    private static final Tag TAG_3 = new Tag(3L, "tag3");

    private static final GiftCertificate GIFT_CERTIFICATE_1 = new GiftCertificate(1L, "giftCertificate1",
            "description1", new BigDecimal("10.1"), 10,
            "2020-08-29T06:12:15.156", "2020-08-29T06:12:15.156");
    private static final GiftCertificate GIFT_CERTIFICATE_2 = new GiftCertificate(2L, "giftCertificate2",
            "description2", new BigDecimal("20.1"), 20,
            "2020-08-29T06:12:15.156", "2020-08-29T06:12:15.156");
    private static final GiftCertificate GIFT_CERTIFICATE_3 = new GiftCertificate(3L, "giftCertificate3",
            "description3", new BigDecimal("30.1"), 30,
            "2020-08-29T06:12:15.156", "2020-08-29T06:12:15.156");

    private static final GiftCertificateDto GIFT_CERTIFICATE_DTO_1 = new GiftCertificateDto(
            GIFT_CERTIFICATE_1, new ArrayList<>(Arrays.asList(TAG_1, TAG_2)));

    private static final GiftCertificateDto GIFT_CERTIFICATE_DTO_2 = new GiftCertificateDto(
            GIFT_CERTIFICATE_2, new ArrayList<>(Arrays.asList(TAG_1, TAG_3)));

    private static final GiftCertificateDto GIFT_CERTIFICATE_DTO_3 = new GiftCertificateDto(
            GIFT_CERTIFICATE_3, new ArrayList<>(Arrays.asList(TAG_1, TAG_2, TAG_3)));

    @Test
    void testCreateGiftCertificate() throws DuplicateEntityException, DaoException, InvalidEntityException {
        giftCertificateService.create(GIFT_CERTIFICATE_DTO_1);

        verify(giftCertificateDao, times(2)).findByName(GIFT_CERTIFICATE_1.getName());
        verify(giftCertificateDao).create(GIFT_CERTIFICATE_DTO_1.getGiftCertificate(),
                GIFT_CERTIFICATE_DTO_1.getCertificateTags());
    }

    @Test
    void testUpdateGiftCertificate() throws DaoException, InvalidEntityException, InvalidIdException {
        giftCertificateService.update(1L, GIFT_CERTIFICATE_DTO_1);
        verify(giftCertificateDao).update(GIFT_CERTIFICATE_DTO_1.getGiftCertificate(),
                GIFT_CERTIFICATE_DTO_1.getCertificateTags());
    }

    @Test
    void testGetAll() throws DaoException {
        List<GiftCertificate> giftCertificates = Arrays.asList(
                GIFT_CERTIFICATE_1,
                GIFT_CERTIFICATE_2,
                GIFT_CERTIFICATE_3
        );
        when(giftCertificateDao.getAll()).thenReturn(giftCertificates);

        List<GiftCertificate> actual = giftCertificateService.getAll();

        assertEquals(giftCertificates, actual);

    }

    @Test
    void testGetById() throws DaoException, NoSuchEntityException, InvalidIdException {
        when(giftCertificateDao.getById(GIFT_CERTIFICATE_DTO_2.getGiftCertificate().getId()))
                .thenReturn(Optional.of(GIFT_CERTIFICATE_2));

        GiftCertificate actual = giftCertificateService.getById(GIFT_CERTIFICATE_2.getId());

        assertEquals(GIFT_CERTIFICATE_2, actual);
    }

    @Test
    void testGetByIdOptionalEmpty() throws DaoException {
        when(giftCertificateDao.getById(GIFT_CERTIFICATE_DTO_2.getGiftCertificate().getId()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> giftCertificateService.getById(
                GIFT_CERTIFICATE_DTO_2.getGiftCertificate().getId()));
    }

    @Test
    void testDeleteById() throws DaoException, NoSuchEntityException, InvalidIdException {
        when(giftCertificateDao.deleteById(GIFT_CERTIFICATE_3.getId())).thenReturn(3L);
        when(giftCertificateDao.getById(GIFT_CERTIFICATE_3.getId())).thenReturn(Optional.of(GIFT_CERTIFICATE_3));

        Long actual = giftCertificateService.deleteById(GIFT_CERTIFICATE_3.getId());
        Long expected = 3L;

        assertEquals(expected, actual);
    }

    @Test
    void testDeleteByIdOptionalEmpty() throws DaoException {
        when(giftCertificateDao.getById(GIFT_CERTIFICATE_DTO_3.getGiftCertificate().getId()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> giftCertificateService.deleteById(GIFT_CERTIFICATE_3.getId()));
    }

    private Method getCheckCertificateMethod() throws NoSuchMethodException {
        Method method = GiftCertificateServiceImpl.class.getDeclaredMethod(
                "checkCertificate", GiftCertificate.class);
        method.setAccessible(true);
        return method;
    }

    @Test
    void testCheckCertificate() throws DaoException {
        when(giftCertificateDao.findByName(GIFT_CERTIFICATE_1.getName())).thenReturn(Optional.of(GIFT_CERTIFICATE_1));

        assertEquals(DuplicateEntityException.class, assertThrows(
                InvocationTargetException.class, () -> getCheckCertificateMethod().invoke(
                        giftCertificateService, GIFT_CERTIFICATE_1)).getCause().getClass());
    }

    @Test
    void testDoFilterFindAll() throws DaoException {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(TAG_NAME, null);
        allRequestParams.put(PART_OF_NAME, "giftCertifi");
        allRequestParams.put(PART_OF_DESCRIPTION, null);
        allRequestParams.put(PART_OF_TAG_NAME, null);
        allRequestParams.put(SORT_BY_NAME, null);
        allRequestParams.put(SORT_BY_CREATE_DATE, null);
        allRequestParams.put(SORT_BY_TAG_NAME, null);

        List<GiftCertificate> giftCertificates = Arrays.asList(
                GIFT_CERTIFICATE_2,
                GIFT_CERTIFICATE_1,
                GIFT_CERTIFICATE_3);
        when(giftCertificateDao.getWithFilters(allRequestParams)).thenReturn(giftCertificates);

        List<GiftCertificate> actual = giftCertificateService.doFilter(allRequestParams);

        assertEquals(giftCertificates, actual);
    }

    @Test
    void testDoFilterFindOne() throws DaoException {
        Map<String, String> allRequestParams = new HashMap<>();
        allRequestParams.put(TAG_NAME, null);
        allRequestParams.put(PART_OF_NAME, "giftCertificate1");
        allRequestParams.put(PART_OF_DESCRIPTION, null);
        allRequestParams.put(PART_OF_TAG_NAME, null);
        allRequestParams.put(SORT_BY_NAME, null);
        allRequestParams.put(SORT_BY_CREATE_DATE, null);
        allRequestParams.put(SORT_BY_TAG_NAME, null);

        List<GiftCertificate> giftCertificates = Collections.singletonList(GIFT_CERTIFICATE_1);
        when(giftCertificateDao.getWithFilters(allRequestParams)).thenReturn(giftCertificates);

        List<GiftCertificate> actual = giftCertificateService.doFilter(allRequestParams);

        assertEquals(giftCertificates, actual);
    }
}
