package io.github.wmjuguna.daraja.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PaginationUtils<T> {
    public static <T> PagedResponse<T> getPagedResponse(Page<T> page, String baseUrl, Class<?> serviceClass,String methodName, Object... args) {
        List<T> content = page.getContent();
        List<Link> links = new ArrayList<>();
        Pageable pageable = page.getPageable();
        int currentPageNumber = pageable.getPageNumber();
        int totalPages = page.getTotalPages();

        if (totalPages > 1 && page.hasPrevious()) {
            int previousPageNumber = currentPageNumber - 1;
            Method prevMethod = getDeclaredMethod(serviceClass, methodName, Pageable.class);
            Pageable prevPageable = PageRequest.of(previousPageNumber, pageable.getPageSize(), pageable.getSort());
            Link prevLink = linkTo(prevMethod, prevPageable, "prev", serviceClass, args);
            links.add(prevLink);
        }

        if (totalPages > 1 && page.hasNext()) {
            int nextPageNumber = currentPageNumber + 1;
            Method nextMethod = getDeclaredMethod(serviceClass, methodName, Pageable.class);
            Pageable nextPageable = PageRequest.of(nextPageNumber, pageable.getPageSize(), pageable.getSort());
            Link nextLink = linkTo(nextMethod, nextPageable, "next", serviceClass, args);
            links.add(nextLink);
        }

        if (totalPages > 1 && !page.isFirst()) {
            Method firstMethod = getDeclaredMethod(serviceClass, methodName, Pageable.class);
            Pageable firstPageable = PageRequest.of(0, pageable.getPageSize(), pageable.getSort());
            Link firstLink = linkTo(firstMethod, firstPageable, "first", serviceClass, args);
            links.add(firstLink);
        }

        if (totalPages > 1 && !page.isLast()) {
            Method lastMethod = getDeclaredMethod(serviceClass, methodName, Pageable.class);
            Pageable lastPageable = PageRequest.of(totalPages - 1, pageable.getPageSize(), pageable.getSort());
            Link lastLink = linkTo(lastMethod, lastPageable, "last", serviceClass, args);
            links.add(lastLink);
        }

        return new PagedResponse<>(content, currentPageNumber, pageable.getPageSize(), page.getTotalElements(), links, baseUrl);
    }

    private static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Link linkTo(Method method, Pageable pageable, String rel, Class<?> serviceClass, Object... args) {
        UriComponentsBuilder builder = linkTo(serviceClass, args).toUriComponentsBuilder();
        builder.replaceQueryParam("page", pageable.getPageNumber());
        builder.replaceQueryParam("size", pageable.getPageSize());
        pageable.getSort();
        pageable.getSort().forEach(order -> builder.queryParam("sort", order.getProperty() + "," + order.getDirection()));
        URI uri = builder.build().encode().toUri();
        return Link.of(uri.toString(), rel);
    }

    private static WebMvcLinkBuilder linkTo(Class<?> serviceClass, Object... args) {
        return WebMvcLinkBuilder.linkTo(serviceClass, args);
    }


}