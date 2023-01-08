package com.epam.esm.service;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.DuplicateEntityException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.exception.InvalidIdException;
import com.epam.esm.exception.NoSuchEntityException;
import com.epam.esm.exceptions.DaoException;

import java.util.List;

public interface TagsService {

    Long create(Tag tag) throws DuplicateEntityException, InvalidEntityException, DaoException;

    List<Tag> getAll() throws DaoException;

    Tag getById(Long id) throws NoSuchEntityException, DaoException, InvalidIdException;

    Long deleteById(Long id) throws NoSuchEntityException, DaoException, InvalidIdException;
}
