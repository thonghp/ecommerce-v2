package com.hpt.backend.paging;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.hpt.common.utils.CommonUtils.ASCENDING;
import static com.hpt.common.utils.CommonUtils.DESCENDING;

public class PagingAndSortingArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(PagingAndSortingParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer model,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        PagingAndSortingParam annotation = parameter.getParameterAnnotation(PagingAndSortingParam.class);
        String sortType = webRequest.getParameter("sortType");
        String sortField = webRequest.getParameter("sortField");
        String keyword = webRequest.getParameter("keyword");
        String reverseSortType = sortType.equals(ASCENDING) ? DESCENDING : ASCENDING;

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortType", sortType);
        model.addAttribute("reverseSortType", reverseSortType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("moduleURL", annotation.moduleURL());

        return new PagingAndSortingHelper(model, annotation.listName(), sortField, sortType, keyword);
    }
}
